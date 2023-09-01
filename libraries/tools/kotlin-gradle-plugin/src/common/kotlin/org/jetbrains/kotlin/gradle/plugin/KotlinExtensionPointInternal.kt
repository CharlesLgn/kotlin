/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.utils.storedProjectProperty
import org.jetbrains.kotlin.tooling.core.UnsafeApi

/**
 * Creates a new [KotlinExtensionPoint]
 * See [KotlinExtensionPoint] documentation for the intended usage!
 */
internal fun <T> KotlinExtensionPoint(): KotlinExtensionPoint<T> {
    @OptIn(UnsafeApi::class)
    return KotlinExtensionPointInternal()
}

/**
 * Only intended implementation of [KotlinExtensionPoint]!
 * Visible for tests to allow test utils to overwrite certain extension points
 */
@UnsafeApi("Visible for tests only")
internal class KotlinExtensionPointInternal<T> : KotlinExtensionPoint<T> {

    private val Project.registeredExtensions by storedProjectProperty<MutableList<T>> { mutableListOf() }

    override fun get(project: Project): List<T> {
        return project.registeredExtensions.toList()
    }

    override fun register(project: Project, extension: T) {
        project.registeredExtensions.add(extension)
    }

    fun set(project: Project, extensions: List<T>) {
        project.registeredExtensions.clear()
        project.registeredExtensions.addAll(extensions)
    }
}
