// !LANGUAGE: +InlineClasses
// Completely incorrect bytecode in some of these - see `box/inlineClasses/toStringOfUnboxedNullable.kt`
// IGNORE_BACKEND: JVM

// FILE: classes.kt
inline class Z(val x: Int)
inline class W(val x: Any)

// FILE: test.kt
fun testZ(z: Z) = z.toString()
fun testZT(z: Z) = "$z"
fun testNZ(z: Z?) = z?.toString()
fun testNZA(z: Z?) = z.toString() // boxed
fun testNZT(z: Z?) = "$z" // boxed

fun testW(w: W) = w.toString()
fun testWT(w: W) = "$w"
fun testNW(w: W?) = w?.toString()
fun testNWA(w: W?) = w.toString()
fun testNWT(w: W?) = "$w"

// @TestKt.class:
// 3 INVOKESTATIC Z\.toString-impl \(I\)Ljava/lang/String;
// 5 INVOKESTATIC W\.toString-impl \(Ljava/lang/Object;\)Ljava/lang/String;
// 2 INVOKESTATIC java/lang/String.valueOf
