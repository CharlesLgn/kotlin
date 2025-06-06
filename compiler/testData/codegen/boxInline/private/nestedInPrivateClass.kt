// TARGET_BACKEND: JVM
// ^ This is no supposed to work on KLIB-backends, see KT-69681
// FILE: 1.kt

package test

private class S public constructor() {
    class Z {
        fun a(): String {
            return "K"
        }
    }
}
// This function exposes S.Z which is a class nested into a private class S (package-private in the byte code)
// It can be accessed outside the `test` package now that S.Z. is public in the byte code, but it may be changed later
@Suppress("IR_PRIVATE_TYPE_USED_IN_NON_PRIVATE_INLINE_FUNCTION_ERROR")
internal inline fun call(s: () -> String): String {
    return s() + test().a()
}

private fun test(): S.Z {
    return S.Z()
}

// FILE: 2.kt

import test.*

fun box(): String {
    return call {
        "O"
    }
}
