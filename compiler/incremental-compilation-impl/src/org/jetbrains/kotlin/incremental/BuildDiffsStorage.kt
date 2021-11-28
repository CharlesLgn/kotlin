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

package org.jetbrains.kotlin.incremental

import org.jetbrains.annotations.TestOnly
import org.jetbrains.kotlin.build.report.ICReporter
import org.jetbrains.kotlin.name.FqName
import java.io.File
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

data class BuildDifference(val ts: Long, val isIncremental: Boolean, val dirtyData: DirtyData)

// todo: storage format can be optimized by compressing fq-names
data class BuildDiffsStorage(val buildDiffs: List<BuildDifference>) {
    companion object {
        fun readFromFile(file: File, reporter: ICReporter?): BuildDiffsStorage? {
            val diffs = readDiffsFromFile(file, reporter)
            return diffs?.let { BuildDiffsStorage(it) }
        }

        fun readDiffsFromFile(file: File, reporter: ICReporter?): MutableList<BuildDifference>? {
            fun reportFail(reason: String) {
                reporter?.report { "Could not read diff from file $file: $reason" }
            }

            if (!file.exists()) return null

            try {
                ObjectInputStream(file.inputStream().buffered()).use { input ->
                    val version = input.readInt()
                    if (version != CURRENT_VERSION) {
                        reportFail("incompatible version $version, actual version is $CURRENT_VERSION")
                        return null
                    }

                    val size = input.readInt()
                    val result = ArrayList<BuildDifference>(size)
                    repeat(size) {
                        result.add(input.readBuildDifference())
                    }
                    return result
                }
            } catch (e: IOException) {
                reportFail(e.toString())
            }

            return null
        }

        fun writeToFile(file: File, storage: BuildDiffsStorage, reporter: ICReporter?) {
            file.parentFile.mkdirs()

            try {
                ObjectOutputStream(file.outputStream().buffered()).use { output ->
                    output.writeInt(CURRENT_VERSION)

                    val diffsToWrite = storage.buildDiffs.sortedBy { it.ts }.takeLast(MAX_DIFFS_ENTRIES)
                    output.writeInt(diffsToWrite.size)
                    for (diff in diffsToWrite) {
                        output.writeBuildDifference(diff)
                    }
                }
            } catch (e: IOException) {
                reporter?.report { "Could not write diff to file $file: $e" }
            }
        }

        private fun ObjectInputStream.readBuildDifference(): BuildDifference {
            val ts = readLong()
            val isIncremental = readBoolean()
            val dirtyData = readDirtyData()
            return BuildDifference(ts, isIncremental, dirtyData)
        }

        private fun ObjectOutputStream.writeBuildDifference(diff: BuildDifference) {
            writeLong(diff.ts)
            writeBoolean(diff.isIncremental)
            writeDirtyData(diff.dirtyData)
        }

        private fun ObjectInputStream.readDirtyData(): DirtyData {
            val lookupSymbols = readLookups()
            val dirtyClassesFqNames = readFqNames()

            return DirtyData(lookupSymbols, dirtyClassesFqNames)
        }

        fun ObjectInputStream.readFqNames(): ArrayList<FqName> {
            val dirtyClassesSize = readInt()
            val dirtyClassesFqNames = ArrayList<FqName>(dirtyClassesSize)
            repeat(dirtyClassesSize) {
                val fqNameString = readUTF()
                dirtyClassesFqNames.add(FqName(fqNameString))
            }
            return dirtyClassesFqNames
        }

        fun ObjectInputStream.readLookups(): ArrayList<LookupSymbol> {
            val lookupSymbolSize = readInt()
            val lookupSymbols = ArrayList<LookupSymbol>(lookupSymbolSize)
            repeat(lookupSymbolSize) {
                val name = readUTF()
                val scope = readUTF()
                lookupSymbols.add(LookupSymbol(name = name, scope = scope))
            }
            return lookupSymbols
        }

        private fun ObjectOutputStream.writeDirtyData(dirtyData: DirtyData) {
            writeLookups(dirtyData.dirtyLookupSymbols)
            writeFqNames(dirtyData.dirtyClassesFqNames)
        }

        fun ObjectOutputStream.writeFqNames(dirtyClassesFqNames: Collection<FqName> ) {
            writeInt(dirtyClassesFqNames.size)
            for (fqName in dirtyClassesFqNames) {
                writeUTF(fqName.asString())
            }
        }

        fun ObjectOutputStream.writeLookups(lookupSymbols: Collection<LookupSymbol>) {
            writeInt(lookupSymbols.size)
            for ((name, scope) in lookupSymbols) {
                writeUTF(name)
                writeUTF(scope)
            }
        }

        internal const val MAX_DIFFS_ENTRIES: Int = 10

        @set:TestOnly
        var CURRENT_VERSION: Int = 0
    }
}