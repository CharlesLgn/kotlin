/*
 * Copyright 2010-2016 JetBrains s.r.o.
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

package org.jetbrains.kotlin.gradle.plugin

import org.gradle.api.internal.HasConvention
import org.gradle.api.plugins.ExtensionAware

internal inline fun <reified T : Any> Any.addConvention(name: String, plugin: T) {
    (this as HasConvention).convention.plugins[name] = plugin
}

internal inline fun <reified T : Any> Any.addExtension(name: String, extension: T) =
    (this as ExtensionAware).extensions.add(T::class.java, name, extension)

internal fun Any.getConvention(name: String): Any? =
    (this as HasConvention).convention.plugins[name]