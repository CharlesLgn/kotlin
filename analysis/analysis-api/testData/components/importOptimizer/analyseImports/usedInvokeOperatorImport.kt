// FILE: main.kt
package test

import dependency.invoke

fun usage(foo: Int) {
    foo()
}

// FILE: dependency.kt
package dependency

operator fun Int.invoke() {}