/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin.mpp

import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleIdentifier
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.component.ComponentWithCoordinates
import org.gradle.api.component.ComponentWithVariants
import org.gradle.api.publish.maven.MavenPublication
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinTargetComponent
import org.jetbrains.kotlin.gradle.plugin.mpp.publishing.ModuleCoordinates
import org.jetbrains.kotlin.gradle.utils.dashSeparatedName
import org.jetbrains.kotlin.gradle.utils.lowerCamelCaseName
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly

internal const val UNSPECIFIED_GAV_FIELD = "unspecified"

internal interface KotlinTargetComponentWithPublication : KotlinTargetComponent {
    // This property is declared in the separate parent type to allow the usages to reference it without forcing the subtypes to load,
    // which is needed for compatibility with older Gradle versions
    var publicationDelegate: MavenPublication?
}

internal data class ModuleVersionIdentifierWithUnspecifiedValue(private val moduleCoordinates: ModuleCoordinates) :
    ModuleVersionIdentifier {
    override fun getGroup() = moduleCoordinates.group ?: UNSPECIFIED_GAV_FIELD
    override fun getName() = moduleCoordinates.name
    override fun getVersion() = moduleCoordinates.version ?: UNSPECIFIED_GAV_FIELD

    override fun getModule(): ModuleIdentifier = object : ModuleIdentifier {
        override fun getGroup(): String = moduleCoordinates.group ?: UNSPECIFIED_GAV_FIELD
        override fun getName(): String = moduleCoordinates.name
    }
}

internal fun getCoordinatesFromGroupNameAndVersion(
    moduleGroup: String?,
    moduleName: String,
    moduleVersion: String?,
): ModuleVersionIdentifier {
    return ModuleVersionIdentifierWithUnspecifiedValue(ModuleCoordinates(moduleGroup, moduleName, moduleVersion))
}

internal fun getCoordinatesFromPublicationDelegateAndProject(
    publication: MavenPublication?,
    project: Project,
    target: KotlinTarget?,
): ModuleVersionIdentifier {
    val moduleName = publication?.artifactId ?: dashSeparatedName(project.name, target?.name?.lowercase())
    val moduleGroup = publication?.groupId ?: project.group.toString()
    val moduleVersion = publication?.version ?: project.version.toString()
    return getCoordinatesFromGroupNameAndVersion(moduleGroup, moduleName, moduleVersion)
}

private interface KotlinTargetComponentWithCoordinatesAndPublication :
    KotlinTargetComponentWithPublication,
    ComponentWithCoordinates /* Gradle 4.7+ API, don't use with older versions */ {
    override fun getCoordinates() = getCoordinatesFromPublicationDelegateAndProject(publicationDelegate, target.project, target)
}

open class KotlinVariant(
    val producingCompilation: KotlinCompilation<*>,
    private val usages: Set<DefaultKotlinUsageContext>,
) : InternalKotlinTargetComponent(), KotlinTargetComponentWithPublication {
    var componentName: String? = null

    var artifactTargetName: String = target.targetName

    final override val target: KotlinTarget
        get() = producingCompilation.target

    override fun getUsages(): Set<KotlinUsageContext> = usages.publishableUsages()

    override fun getName(): String = componentName ?: producingCompilation.target.targetName

    override var publishable: Boolean = true
    override val publishableOnCurrentHost: Boolean
        get() = publishable && target.publishable

    @Deprecated(
        message = "Sources artifacts are now published as separate variant " +
                "use target.sourcesElementsConfigurationName to obtain necessary information. Scheduled for removal in Kotlin 2.3.",
        replaceWith = ReplaceWith("target.sourcesElementsConfigurationName"),
        level = DeprecationLevel.ERROR,
    )
    override val sourcesArtifacts: Set<PublishArtifact>
        get() = target
            .project
            .configurations
            .findByName(target.sourcesElementsConfigurationName)
            ?.artifacts
            ?: emptySet()

    internal var defaultArtifactIdSuffix: String? = null

    override val defaultArtifactId: String
        get() = dashSeparatedName(target.project.name, artifactTargetName.toLowerCaseAsciiOnly(), defaultArtifactIdSuffix)

    override var publicationDelegate: MavenPublication? = null
}

open class KotlinVariantWithCoordinates(
    producingCompilation: KotlinCompilation<*>,
    usages: Set<DefaultKotlinUsageContext>,
) : KotlinVariant(producingCompilation, usages),
    KotlinTargetComponentWithCoordinatesAndPublication /* Gradle 4.7+ API, don't use with older versions */

class KotlinVariantWithMetadataVariant(
    producingCompilation: KotlinCompilation<*>,
    usages: Set<DefaultKotlinUsageContext>,
    internal val metadataTarget: AbstractKotlinTarget,
) : KotlinVariantWithCoordinates(producingCompilation, usages), ComponentWithVariants {
    override fun getVariants() = metadataTarget.components
}

class JointAndroidKotlinTargetComponent(
    override val target: KotlinAndroidTarget,
    private val nestedVariants: Set<KotlinVariant>,
    val flavorNames: List<String>,
) : InternalKotlinTargetComponent(), KotlinTargetComponentWithCoordinatesAndPublication {

    override fun getUsages(): Set<KotlinUsageContext> = nestedVariants.filter { it.publishable }.flatMap { it.usages }.toSet()

    override fun getName(): String = lowerCamelCaseName(target.targetName, *flavorNames.toTypedArray())

    override val publishable: Boolean
        get() = nestedVariants.any { it.publishable }

    override val publishableOnCurrentHost: Boolean
        get() = publishable

    override val defaultArtifactId: String =
        dashSeparatedName(
            target.project.name,
            target.targetName.toLowerCaseAsciiOnly(),
            *flavorNames.map { it.toLowerCaseAsciiOnly() }.toTypedArray()
        )

    override var publicationDelegate: MavenPublication? = null

    @Deprecated(
        message = "Sources artifacts are now published as separate variant " +
                "use target.sourcesElementsConfigurationName to obtain necessary information",
        replaceWith = ReplaceWith("target.sourcesElementsConfigurationName")
    )
    override val sourcesArtifacts: Set<PublishArtifact> = emptySet()
}
