// ERROR: Unsupported [Dynamic types are not supported in this context]

fun foo() {
    fun <T> bar(c: () -> dynamic, f: () -> T): Unit {}
    bar({
            val a = 1
            Unit
        }) {
        val a = 1
        Unit<caret>
    }
}