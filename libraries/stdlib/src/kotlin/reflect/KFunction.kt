/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:kotlin.internal.JvmBuiltin

package kotlin.reflect

/**
 * Represents a function with introspection capabilities.
 */
public expect interface KFunction<out R> : KCallable<R>, Function<R>