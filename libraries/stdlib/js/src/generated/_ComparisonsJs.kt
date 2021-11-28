/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.comparisons

//
// NOTE: THIS FILE IS AUTO-GENERATED by the GenerateStandardLib.kt
// See: https://github.com/JetBrains/kotlin/tree/master/libraries/stdlib
//

import kotlin.js.*

/**
 * Returns the greater of two values.
 * 
 * If values are equal, returns the first one.
 */
@SinceKotlin("1.1")
public actual fun <T : Comparable<T>> maxOf(a: T, b: T): T {
    return if (a >= b) a else b
}

/**
 * Returns the greater of two values.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun maxOf(a: Byte, b: Byte): Byte {
    return maxOf(a.toInt(), b.toInt()).unsafeCast<Byte>()
}

/**
 * Returns the greater of two values.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun maxOf(a: Short, b: Short): Short {
    return maxOf(a.toInt(), b.toInt()).unsafeCast<Short>()
}

/**
 * Returns the greater of two values.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun maxOf(a: Int, b: Int): Int {
    return JsMath.max(a, b)
}

/**
 * Returns the greater of two values.
 */
@SinceKotlin("1.1")
@Suppress("NOTHING_TO_INLINE")
public actual inline fun maxOf(a: Long, b: Long): Long {
    return if (a >= b) a else b
}

/**
 * Returns the greater of two values.
 * 
 * If either value is `NaN`, returns `NaN`.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun maxOf(a: Float, b: Float): Float {
    return JsMath.max(a, b)
}

/**
 * Returns the greater of two values.
 * 
 * If either value is `NaN`, returns `NaN`.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun maxOf(a: Double, b: Double): Double {
    return JsMath.max(a, b)
}

/**
 * Returns the greater of three values.
 * 
 * If there are multiple equal maximal values, returns the first of them.
 */
@SinceKotlin("1.1")
public actual fun <T : Comparable<T>> maxOf(a: T, b: T, c: T): T {
    return maxOf(a, maxOf(b, c))
}

/**
 * Returns the greater of three values.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun maxOf(a: Byte, b: Byte, c: Byte): Byte {
    return JsMath.max(a.toInt(), b.toInt(), c.toInt()).unsafeCast<Byte>()
}

/**
 * Returns the greater of three values.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun maxOf(a: Short, b: Short, c: Short): Short {
    return JsMath.max(a.toInt(), b.toInt(), c.toInt()).unsafeCast<Short>()
}

/**
 * Returns the greater of three values.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun maxOf(a: Int, b: Int, c: Int): Int {
    return JsMath.max(a, b, c)
}

/**
 * Returns the greater of three values.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun maxOf(a: Long, b: Long, c: Long): Long {
    return maxOf(a, maxOf(b, c))
}

/**
 * Returns the greater of three values.
 * 
 * If any value is `NaN`, returns `NaN`.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun maxOf(a: Float, b: Float, c: Float): Float {
    return JsMath.max(a, b, c)
}

/**
 * Returns the greater of three values.
 * 
 * If any value is `NaN`, returns `NaN`.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun maxOf(a: Double, b: Double, c: Double): Double {
    return JsMath.max(a, b, c)
}

/**
 * Returns the greater of the given values.
 * 
 * If there are multiple equal maximal values, returns the first of them.
 */
@SinceKotlin("1.4")
public actual fun <T : Comparable<T>> maxOf(a: T, vararg other: T): T {
    var max = a
    for (e in other) max = maxOf(max, e)
    return max
}

/**
 * Returns the greater of the given values.
 */
@SinceKotlin("1.4")
public actual fun maxOf(a: Byte, vararg other: Byte): Byte {
    var max = a
    for (e in other) max = maxOf(max, e)
    return max
}

/**
 * Returns the greater of the given values.
 */
@SinceKotlin("1.4")
public actual fun maxOf(a: Short, vararg other: Short): Short {
    var max = a
    for (e in other) max = maxOf(max, e)
    return max
}

/**
 * Returns the greater of the given values.
 */
@SinceKotlin("1.4")
public actual fun maxOf(a: Int, vararg other: Int): Int {
    var max = a
    for (e in other) max = maxOf(max, e)
    return max
}

/**
 * Returns the greater of the given values.
 */
@SinceKotlin("1.4")
public actual fun maxOf(a: Long, vararg other: Long): Long {
    var max = a
    for (e in other) max = maxOf(max, e)
    return max
}

/**
 * Returns the greater of the given values.
 * 
 * If any value is `NaN`, returns `NaN`.
 */
@SinceKotlin("1.4")
public actual fun maxOf(a: Float, vararg other: Float): Float {
    var max = a
    for (e in other) max = maxOf(max, e)
    return max
}

/**
 * Returns the greater of the given values.
 * 
 * If any value is `NaN`, returns `NaN`.
 */
@SinceKotlin("1.4")
public actual fun maxOf(a: Double, vararg other: Double): Double {
    var max = a
    for (e in other) max = maxOf(max, e)
    return max
}

/**
 * Returns the smaller of two values.
 * 
 * If values are equal, returns the first one.
 */
@SinceKotlin("1.1")
public actual fun <T : Comparable<T>> minOf(a: T, b: T): T {
    return if (a <= b) a else b
}

/**
 * Returns the smaller of two values.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun minOf(a: Byte, b: Byte): Byte {
    return minOf(a.toInt(), b.toInt()).unsafeCast<Byte>()
}

/**
 * Returns the smaller of two values.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun minOf(a: Short, b: Short): Short {
    return minOf(a.toInt(), b.toInt()).unsafeCast<Short>()
}

/**
 * Returns the smaller of two values.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun minOf(a: Int, b: Int): Int {
    return JsMath.min(a, b)
}

/**
 * Returns the smaller of two values.
 */
@SinceKotlin("1.1")
@Suppress("NOTHING_TO_INLINE")
public actual inline fun minOf(a: Long, b: Long): Long {
    return if (a <= b) a else b
}

/**
 * Returns the smaller of two values.
 * 
 * If either value is `NaN`, returns `NaN`.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun minOf(a: Float, b: Float): Float {
    return JsMath.min(a, b)
}

/**
 * Returns the smaller of two values.
 * 
 * If either value is `NaN`, returns `NaN`.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun minOf(a: Double, b: Double): Double {
    return JsMath.min(a, b)
}

/**
 * Returns the smaller of three values.
 * 
 * If there are multiple equal minimal values, returns the first of them.
 */
@SinceKotlin("1.1")
public actual fun <T : Comparable<T>> minOf(a: T, b: T, c: T): T {
    return minOf(a, minOf(b, c))
}

/**
 * Returns the smaller of three values.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun minOf(a: Byte, b: Byte, c: Byte): Byte {
    return JsMath.min(a.toInt(), b.toInt(), c.toInt()).unsafeCast<Byte>()
}

/**
 * Returns the smaller of three values.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun minOf(a: Short, b: Short, c: Short): Short {
    return JsMath.min(a.toInt(), b.toInt(), c.toInt()).unsafeCast<Short>()
}

/**
 * Returns the smaller of three values.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun minOf(a: Int, b: Int, c: Int): Int {
    return JsMath.min(a, b, c)
}

/**
 * Returns the smaller of three values.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun minOf(a: Long, b: Long, c: Long): Long {
    return minOf(a, minOf(b, c))
}

/**
 * Returns the smaller of three values.
 * 
 * If any value is `NaN`, returns `NaN`.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun minOf(a: Float, b: Float, c: Float): Float {
    return JsMath.min(a, b, c)
}

/**
 * Returns the smaller of three values.
 * 
 * If any value is `NaN`, returns `NaN`.
 */
@SinceKotlin("1.1")
@kotlin.internal.InlineOnly
public actual inline fun minOf(a: Double, b: Double, c: Double): Double {
    return JsMath.min(a, b, c)
}

/**
 * Returns the smaller of the given values.
 * 
 * If there are multiple equal minimal values, returns the first of them.
 */
@SinceKotlin("1.4")
public actual fun <T : Comparable<T>> minOf(a: T, vararg other: T): T {
    var min = a
    for (e in other) min = minOf(min, e)
    return min
}

/**
 * Returns the smaller of the given values.
 */
@SinceKotlin("1.4")
public actual fun minOf(a: Byte, vararg other: Byte): Byte {
    var min = a
    for (e in other) min = minOf(min, e)
    return min
}

/**
 * Returns the smaller of the given values.
 */
@SinceKotlin("1.4")
public actual fun minOf(a: Short, vararg other: Short): Short {
    var min = a
    for (e in other) min = minOf(min, e)
    return min
}

/**
 * Returns the smaller of the given values.
 */
@SinceKotlin("1.4")
public actual fun minOf(a: Int, vararg other: Int): Int {
    var min = a
    for (e in other) min = minOf(min, e)
    return min
}

/**
 * Returns the smaller of the given values.
 */
@SinceKotlin("1.4")
public actual fun minOf(a: Long, vararg other: Long): Long {
    var min = a
    for (e in other) min = minOf(min, e)
    return min
}

/**
 * Returns the smaller of the given values.
 * 
 * If any value is `NaN`, returns `NaN`.
 */
@SinceKotlin("1.4")
public actual fun minOf(a: Float, vararg other: Float): Float {
    var min = a
    for (e in other) min = minOf(min, e)
    return min
}

/**
 * Returns the smaller of the given values.
 * 
 * If any value is `NaN`, returns `NaN`.
 */
@SinceKotlin("1.4")
public actual fun minOf(a: Double, vararg other: Double): Double {
    var min = a
    for (e in other) min = minOf(min, e)
    return min
}

