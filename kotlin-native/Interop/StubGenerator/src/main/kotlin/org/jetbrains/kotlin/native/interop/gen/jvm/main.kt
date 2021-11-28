/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.native.interop.gen.jvm

import org.jetbrains.kotlin.konan.TempFiles
import org.jetbrains.kotlin.konan.exec.Command
import org.jetbrains.kotlin.konan.util.DefFile
import org.jetbrains.kotlin.native.interop.gen.*
import org.jetbrains.kotlin.native.interop.gen.wasm.processIdlLib
import org.jetbrains.kotlin.native.interop.indexer.*
import org.jetbrains.kotlin.native.interop.tool.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import org.jetbrains.kotlin.konan.ForeignExceptionMode
import org.jetbrains.kotlin.konan.library.*
import org.jetbrains.kotlin.konan.target.CompilerOutputKind
import org.jetbrains.kotlin.konan.target.Distribution
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.util.KonanHomeProvider
import org.jetbrains.kotlin.library.KotlinLibrary
import org.jetbrains.kotlin.library.packageFqName
import org.jetbrains.kotlin.library.resolver.TopologicalLibraryOrder
import org.jetbrains.kotlin.library.resolver.impl.KotlinLibraryResolverImpl
import org.jetbrains.kotlin.library.resolver.impl.libraryResolver
import org.jetbrains.kotlin.library.toUnresolvedLibraries
import org.jetbrains.kotlin.util.removeSuffixIfPresent
import org.jetbrains.kotlin.util.suffixIfNot
import java.io.File
import java.lang.IllegalArgumentException
import java.nio.file.*
import java.util.*

data class InternalInteropOptions(val generated: String, val natives: String, val manifest: String? = null,
                                  val cstubsName: String? = null)

fun main(args: Array<String>) {
    // Adding flavor option for interop plugin.
    class FullCInteropArguments: CInteropArguments() {
        val flavor by argParser.option(ArgType.Choice<KotlinPlatform>(), description = "Interop target")
                .default(KotlinPlatform.JVM)
        val generated by argParser.option(ArgType.String, description = "place generated bindings to the directory")
                .required()
        val natives by argParser.option(ArgType.String, description = "where to put the built native files")
                .required()
    }
    val arguments = FullCInteropArguments()
    arguments.argParser.parse(args)
    val flavorName = arguments.flavor
    processCLib(flavorName, arguments, InternalInteropOptions(arguments.generated, arguments.natives))
}

fun interop(
        flavor: String, args: Array<String>,
        additionalArgs: InternalInteropOptions
): Array<String>? = when(flavor) {
            "jvm", "native" -> {
                val cinteropArguments = CInteropArguments()
                cinteropArguments.argParser.parse(args)
                val platform = KotlinPlatform.values().single { it.name.equals(flavor, ignoreCase = true) }
                processCLib(platform, cinteropArguments, additionalArgs)
            }
            "wasm" -> processIdlLib(args, additionalArgs)
            else -> error("Unexpected flavor")
        }

// Options, whose values are space-separated and can be escaped.
val escapedOptions = setOf("-compilerOpts", "-linkerOpts", "-compiler-options", "-linker-options")

private fun String.asArgList(key: String) =
        if (escapedOptions.contains(key))
            this.split(Regex("(?<!\\\\)\\Q \\E")).filter { it.isNotEmpty() }.map { it.replace("\\ ", " ") }
        else
            listOf(this)

private fun <T> Collection<T>.atMostOne(): T? {
    return when (this.size) {
        0 -> null
        1 -> this.iterator().next()
        else -> throw IllegalArgumentException("Collection has more than one element.")
    }
}

private fun List<String>?.isTrue(): Boolean {
    // The rightmost wins, null != "true".
    return this?.last() == "true"
}

private fun runCmd(command: Array<String>, verbose: Boolean = false) {
    if (verbose) println("COMMAND: " + command.joinToString(" "))
    Command(*command).getOutputLines(true).let { lines ->
        if (verbose) lines.forEach(::println)
    }
}

private fun Properties.storeProperties(file: File) {
    file.outputStream().use {
        this.store(it, null)
    }
}

private fun Properties.putAndRunOnReplace(key: Any, newValue: Any, beforeReplace: (Any, Any, Any) -> Unit) {
    val oldValue = this[key]
    if (oldValue != null && oldValue != newValue) {
        beforeReplace(key, oldValue, newValue)
    }
    this[key] = newValue
}

private fun selectNativeLanguage(config: DefFile.DefFileConfig): Language {
    val languages = mapOf(
            "C" to Language.C,
            "C++" to Language.CPP,
            "Objective-C" to Language.OBJECTIVE_C
    )

    // C++ is not publicly supported.
    val publicLanguages = languages.keys.minus("C++")

    val lang = config.language?.let {
        languages[it]
                ?: error("Unexpected language '${config.language}'. Possible values are: ${publicLanguages.joinToString { "'$it'" }}")
    } ?: Language.C

    return lang

}

private fun parseImports(dependencies: List<KotlinLibrary>): ImportsImpl =
        dependencies.filterIsInstance<KonanLibrary>().mapNotNull { library ->
            // TODO: handle missing properties?
            library.packageFqName?.let { packageFqName ->
                val headerIds = library.includedHeaders
                headerIds.map { HeaderId(it) to PackageInfo(packageFqName, library) }
            }
        }.reversed().flatten().toMap().let(::ImportsImpl)

fun getCompilerFlagsForVfsOverlay(headerFilterPrefix: Array<String>, def: DefFile): List<String> {
    val relativeToRoot = mutableMapOf<Path, Path>() // TODO: handle clashes

    val filteredIncludeDirs = headerFilterPrefix .map { Paths.get(it) }
    if (filteredIncludeDirs.isNotEmpty()) {
        val headerFilterGlobs = def.config.headerFilter
        if (headerFilterGlobs.isEmpty()) {
            error("'$HEADER_FILTER_ADDITIONAL_SEARCH_PREFIX' option requires " +
                    "'headerFilter' to be specified in .def file")
        }

        relativeToRoot += findFilesByGlobs(roots = filteredIncludeDirs, globs = headerFilterGlobs)
    }

    if (relativeToRoot.isEmpty()) {
        return emptyList()
    }

    val virtualRoot = Paths.get(System.getProperty("java.io.tmpdir")).resolve("konanSystemInclude")

    val virtualPathToReal = relativeToRoot.map { (relativePath, realRoot) ->
        virtualRoot.resolve(relativePath) to realRoot.resolve(relativePath)
    }.toMap()

    val vfsOverlayFile = createVfsOverlayFile(virtualPathToReal)

    return listOf("-I${virtualRoot.toAbsolutePath()}", "-ivfsoverlay", vfsOverlayFile.toAbsolutePath().toString())
}

private fun findFilesByGlobs(roots: List<Path>, globs: List<String>): Map<Path, Path> {
    val relativeToRoot = mutableMapOf<Path, Path>()

    val pathMatchers = globs.map { FileSystems.getDefault().getPathMatcher("glob:$it") }

    roots.reversed()
            .filter { path ->
                return@filter when {
                    path.toFile().exists() -> true
                    else -> { warn("$path doesn't exist"); false }
                }
            }
            .forEach { root ->
                // TODO: don't scan the entire tree, skip subdirectories according to globs.
                Files.walk(root, FileVisitOption.FOLLOW_LINKS).forEach { path ->
                    val relativePath = root.relativize(path)
                    if (!Files.isDirectory(path) && pathMatchers.any { it.matches(relativePath) }) {
                        relativeToRoot[relativePath] = root
                    }
                }
            }
    return relativeToRoot
}

private fun processCLib(flavor: KotlinPlatform, cinteropArguments: CInteropArguments,
                        additionalArgs: InternalInteropOptions): Array<String>? {
    val ktGenRoot = additionalArgs.generated
    val nativeLibsDir = additionalArgs.natives
    val defFile = cinteropArguments.def?.let { File(it) }
    val manifestAddend = additionalArgs.manifest?.let { File(it) }

    if (defFile == null && cinteropArguments.pkg == null) {
        cinteropArguments.argParser.printError("-def or -pkg should be provided!")
    }

    val tool = prepareTool(cinteropArguments.target, flavor, parseKeyValuePairs(cinteropArguments.overrideKonanProperties))

    val def = DefFile(defFile, tool.substitutions)
    val isLinkerOptsSetByUser = (cinteropArguments.linkerOpts.valueOrigin == ArgParser.ValueOrigin.SET_BY_USER) ||
            (cinteropArguments.linkerOptions.valueOrigin == ArgParser.ValueOrigin.SET_BY_USER) ||
            (cinteropArguments.linkerOption.valueOrigin == ArgParser.ValueOrigin.SET_BY_USER)
    if (flavor == KotlinPlatform.NATIVE && isLinkerOptsSetByUser) {
        warn("-linker-option(s)/-linkerOpts option is not supported by cinterop. Please add linker options to .def file or binary compilation instead.")
    }

    val additionalLinkerOpts = cinteropArguments.linkerOpts.value.toTypedArray() + cinteropArguments.linkerOption.value.toTypedArray() +
            cinteropArguments.linkerOptions.value.toTypedArray()
    val verbose = cinteropArguments.verbose

    val entryPoint = def.config.entryPoints.atMostOne()
    val linkerName = cinteropArguments.linker ?: def.config.linker
    val linker = "${tool.llvmHome}/bin/$linkerName"
    val compiler = "${tool.llvmHome}/bin/clang"
    val excludedFunctions = def.config.excludedFunctions.toSet()
    val excludedMacros = def.config.excludedMacros.toSet()
    val staticLibraries = def.config.staticLibraries + cinteropArguments.staticLibrary.toTypedArray()
    val libraryPaths = def.config.libraryPaths + cinteropArguments.libraryPath.toTypedArray()
    val fqParts = (cinteropArguments.pkg ?: def.config.packageName)?.split('.')
            ?: defFile!!.name.split('.').reversed().drop(1)

    val outKtPkg = fqParts.joinToString(".")

    val mode = run {
        val providedMode = cinteropArguments.mode

        if (providedMode == GenerationMode.METADATA && flavor == KotlinPlatform.JVM) {
            warn("Metadata mode isn't supported for Kotlin/JVM! Falling back to sourcecode.")
            GenerationMode.SOURCE_CODE
        } else {
            providedMode
        }
    }

    val resolver = getLibraryResolver(cinteropArguments, tool.target)

    val allLibraryDependencies = when (flavor) {
        KotlinPlatform.NATIVE -> resolveDependencies(resolver, cinteropArguments)
        else -> listOf()
    }

    val libName = additionalArgs.cstubsName ?: fqParts.joinToString("") + "stubs"

    val tempFiles = TempFiles(libName, cinteropArguments.tempDir)

    val imports = parseImports(allLibraryDependencies)

    val library = buildNativeLibrary(tool, def, cinteropArguments, imports)

    val plugin = Plugins.plugin(def.config.pluginName)

    val (nativeIndex, compilation) = plugin.buildNativeIndex(library, verbose)

    // Our current approach to arm64_32 support is to compile armv7k version of bitcode
    // for arm64_32. That's the reason for this substitution.
    // TODO: Add proper support with the next LLVM update.
    val target = when (tool.target) {
        KonanTarget.WATCHOS_ARM64 -> KonanTarget.WATCHOS_ARM32
        else -> tool.target
    }

    val klibSuffix = CompilerOutputKind.LIBRARY.suffix(target)
    val moduleName = cinteropArguments.moduleName
            ?: File(cinteropArguments.output).name.removeSuffixIfPresent(klibSuffix)

    val configuration = InteropConfiguration(
            library = compilation,
            pkgName = outKtPkg,
            excludedFunctions = excludedFunctions,
            excludedMacros = excludedMacros,
            strictEnums = def.config.strictEnums.toSet(),
            nonStrictEnums = def.config.nonStrictEnums.toSet(),
            noStringConversion = def.config.noStringConversion.toSet(),
            exportForwardDeclarations = def.config.exportForwardDeclarations,
            disableDesignatedInitializerChecks = def.config.disableDesignatedInitializerChecks,
            target = target
    )


    File(nativeLibsDir).mkdirs()
    val outCFile = tempFiles.create(libName, ".${library.language.sourceFileExtension}")

    val logger = if (verbose) {
        { message: String -> println(message) }
    } else {
        {}
    }

    val stubIrContext = StubIrContext(logger, configuration, nativeIndex, imports, flavor, mode, libName, plugin)
    val stubIrOutput = run {
        val outKtFileCreator = {
            val outKtFileName = fqParts.last() + ".kt"
            val outKtFileRelative = (fqParts + outKtFileName).joinToString("/")
            val file = File(ktGenRoot, outKtFileRelative)
            file.parentFile.mkdirs()
            file
        }
        val driverOptions = StubIrDriver.DriverOptions(
                entryPoint,
                moduleName,
                File(outCFile.absolutePath),
                outKtFileCreator,
                cinteropArguments.dumpBridges ?: false
        )
        val stubIrDriver = StubIrDriver(stubIrContext, driverOptions)
        stubIrDriver.run()
    }

    // TODO: if a library has partially included headers, then it shouldn't be used as a dependency.
    def.manifestAddendProperties["includedHeaders"] = nativeIndex.includedHeaders.joinToString(" ") { it.value }

    def.manifestAddendProperties.putAndRunOnReplace("package", outKtPkg) {
        _, oldValue, newValue ->
            warn("The package value `$oldValue` specified in .def file is overridden with explicit $newValue")
    }
    def.manifestAddendProperties["interop"] = "true"
    if (stubIrOutput is StubIrDriver.Result.Metadata) {
        def.manifestAddendProperties["ir_provider"] = KLIB_INTEROP_IR_PROVIDER_IDENTIFIER
    }
    stubIrContext.addManifestProperties(def.manifestAddendProperties)
    // cinterop command line option overrides def file property
    val foreignExceptionMode = cinteropArguments.foreignExceptionMode?: def.config.foreignExceptionMode
    foreignExceptionMode?.let {
        def.manifestAddendProperties[ForeignExceptionMode.manifestKey] =
                ForeignExceptionMode.byValue(it).value   // may throw IllegalArgumentException
    }

    manifestAddend?.parentFile?.mkdirs()
    manifestAddend?.let { def.manifestAddendProperties.storeProperties(it) }

    val compilerArgs = stubIrContext.libraryForCStubs.compilerArgs.toTypedArray()
    val nativeOutputPath: String = when (flavor) {
        KotlinPlatform.JVM -> {
            val outOFile = tempFiles.create(libName,".o")
            val compilerCmd = arrayOf(compiler, *compilerArgs,
                    "-c", outCFile.absolutePath, "-o", outOFile.absolutePath)
            runCmd(compilerCmd, verbose)
            val linkerOpts =
                    def.config.linkerOpts.toTypedArray() +
                            tool.getDefaultCompilerOptsForLanguage(library.language) +
                            additionalLinkerOpts
            val outLib = File(nativeLibsDir, System.mapLibraryName(libName))
            val linkerCmd = arrayOf(linker,
                    outOFile.absolutePath, "-shared", "-o", outLib.absolutePath,
                    *linkerOpts)
            runCmd(linkerCmd, verbose)
            outOFile.absolutePath
        }
        KotlinPlatform.NATIVE -> {
            val outLib = File(nativeLibsDir, "$libName.bc")
            val compilerCmd = arrayOf(compiler, *compilerArgs,
                    "-emit-llvm", "-c", outCFile.absolutePath, "-o", outLib.absolutePath)
            runCmd(compilerCmd, verbose)
            outLib.absolutePath
        }
    }

    val compiledFiles = compileSources(nativeLibsDir, tool, cinteropArguments)

    return when (stubIrOutput) {
        is StubIrDriver.Result.SourceCode -> {
            val bitcodePaths = compiledFiles.map {  listOf("-native-library", it) }.flatten()
            argsToCompiler(staticLibraries, libraryPaths) + bitcodePaths
        }
        is StubIrDriver.Result.Metadata -> {
            val stdlibDependency = resolver.resolveWithDependencies(
                    emptyList(),
                    noDefaultLibs = true,
                    noEndorsedLibs = true
            ).getFullList()

            val nopack = cinteropArguments.nopack
            val outputPath = cinteropArguments.output.let {
                val suffix = CompilerOutputKind.LIBRARY.suffix(tool.target)
                if (nopack) it.removeSuffixIfPresent(suffix) else it.suffixIfNot(suffix)
            }

            createInteropLibrary(
                    metadata = stubIrOutput.metadata,
                    nativeBitcodeFiles = compiledFiles + nativeOutputPath,
                    target = tool.target,
                    moduleName = moduleName,
                    outputPath = outputPath,
                    manifest = def.manifestAddendProperties,
                    dependencies = stdlibDependency + imports.requiredLibraries.toList(),
                    nopack = nopack,
                    shortName = cinteropArguments.shortModuleName,
                    staticLibraries = resolveLibraries(staticLibraries, libraryPaths)
            )
            return null
        }
    }
}

private fun compileSources(
        nativeLibsDir: String,
        toolConfig: ToolConfig,
        cinteropArguments: CInteropArguments
): List<String> = cinteropArguments.compileSource.mapIndexed { index, source ->
    // Mangle file name to avoid collisions.
    val mangledFileName = "${index}_${File(source).nameWithoutExtension}"
    val outputFileName = "$nativeLibsDir/${mangledFileName}.bc"
    val compilerArgs = cinteropArguments.sourceCompileOptions.toTypedArray()
    val compilerCmd = toolConfig.clang.clangCXX(*compilerArgs, source, "-emit-llvm", "-c", "-o", outputFileName)
    runCmd(compilerCmd.toTypedArray(), verbose = cinteropArguments.verbose)
    outputFileName
}

private fun getLibraryResolver(
        cinteropArguments: CInteropArguments, target: KonanTarget
): KotlinLibraryResolverImpl<KonanLibrary> {
    val libraries = cinteropArguments.library
    val repos = cinteropArguments.repo
    return defaultResolver(
            repos,
            libraries.filter { it.contains(org.jetbrains.kotlin.konan.file.File.separator) },
            target,
            Distribution(KonanHomeProvider.determineKonanHome())
    ).libraryResolver()
}

private fun resolveDependencies(
        resolver: KotlinLibraryResolverImpl<KonanLibrary>, cinteropArguments: CInteropArguments
): List<KotlinLibrary> {
    val libraries = cinteropArguments.library
    val noDefaultLibs = cinteropArguments.nodefaultlibs || cinteropArguments.nodefaultlibsDeprecated
    val noEndorsedLibs = cinteropArguments.noendorsedlibs
    return resolver.resolveWithDependencies(
            libraries.toUnresolvedLibraries,
            noStdLib = false,
            noDefaultLibs = noDefaultLibs,
            noEndorsedLibs = noEndorsedLibs
    ).getFullList(TopologicalLibraryOrder)
}

internal fun prepareTool(target: String?, flavor: KotlinPlatform, propertyOverrides: Map<String, String> = emptyMap()): ToolConfig {
    val tool = ToolConfig(target, flavor, propertyOverrides)
    tool.downloadDependencies()

    System.load(tool.libclang)

    return tool
}

internal fun buildNativeLibrary(
        tool: ToolConfig,
        def: DefFile,
        arguments: CInteropArguments,
        imports: ImportsImpl
): NativeLibrary {
    val additionalHeaders = (arguments.header).toTypedArray()
    val additionalCompilerOpts = (arguments.compilerOpts +
            arguments.compilerOptions + arguments.compilerOption).toTypedArray()

    val headerFiles = def.config.headers + additionalHeaders
    val language = selectNativeLanguage(def.config)
    val compilerOpts: List<String> = mutableListOf<String>().apply {
        addAll(def.config.compilerOpts)
        addAll(tool.getDefaultCompilerOptsForLanguage(language))
        addAll(additionalCompilerOpts)
        addAll(getCompilerFlagsForVfsOverlay(arguments.headerFilterPrefix.toTypedArray(), def))
    }

    val compilation = CompilationImpl(
            includes = headerFiles,
            additionalPreambleLines = def.defHeaderLines,
            compilerArgs = defaultCompilerArgs(language) + compilerOpts + tool.platformCompilerOpts,
            language = language
    )

    val headerFilter: NativeLibraryHeaderFilter
    val includes: List<String>

    val modules = def.config.modules

    if (modules.isEmpty()) {
        val excludeDependentModules = def.config.excludeDependentModules

        val headerFilterGlobs = def.config.headerFilter
        val headerInclusionPolicy = HeaderInclusionPolicyImpl(headerFilterGlobs)

        headerFilter = NativeLibraryHeaderFilter.NameBased(headerInclusionPolicy, excludeDependentModules)
        includes = headerFiles
    } else {
        require(language == Language.OBJECTIVE_C) { "cinterop supports 'modules' only when 'language = Objective-C'" }
        require(headerFiles.isEmpty()) { "cinterop doesn't support having headers and modules specified at the same time" }
        require(def.config.headerFilter.isEmpty()) { "cinterop doesn't support 'headerFilter' with 'modules'" }

        val modulesInfo = getModulesInfo(compilation, modules)

        headerFilter = NativeLibraryHeaderFilter.Predefined(modulesInfo.ownHeaders)
        includes = modulesInfo.topLevelHeaders
    }

    val excludeSystemLibs = def.config.excludeSystemLibs

    val headerExclusionPolicy = HeaderExclusionPolicyImpl(imports)

    return NativeLibrary(
            includes = includes,
            additionalPreambleLines = compilation.additionalPreambleLines,
            compilerArgs = compilation.compilerArgs,
            headerToIdMapper = HeaderToIdMapper(sysRoot = tool.sysRoot),
            language = compilation.language,
            excludeSystemLibs = excludeSystemLibs,
            headerExclusionPolicy = headerExclusionPolicy,
            headerFilter = headerFilter
    )
}

private fun parseKeyValuePairs(
    argumentValue: List<String>,
): Map<String, String> = argumentValue.mapNotNull {
    val keyValueSeparatorIndex = it.indexOf('=')
    if (keyValueSeparatorIndex > 0) {
        it.substringBefore('=') to it.substringAfter('=')
    } else {
        warn("incorrect property format: expected '<key>=<value>', got '$it'")
        null
    }
}.toMap()