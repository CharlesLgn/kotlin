// IGNORE_BACKEND: JS_IR
// IGNORE_KLIB_SYNTHETIC_ACCESSORS_CHECKS: JS_IR
// ^^^ To be fixed in KT-72883: org.jetbrains.kotlin.ir.expressions.impl.IrExpressionBodyImpl cannot be cast to org.jetbrains.kotlin.ir.expressions.IrBlockBody
// WITH_COROUTINES
// WITH_STDLIB
// NO_CHECK_LAMBDA_INLINING

// IGNORE_NATIVE: cacheMode=STATIC_USE_HEADERS_EVERYWHERE
// ^^^ To be fixed in KT-72862: No function found for symbol

// MODULE: lib
// FILE: a.kt
private suspend fun privateSuspendMethod() = "OK"

internal suspend inline fun internalInline() = privateSuspendMethod()

// MODULE: main()(lib)
// FILE: main.kt
import kotlin.coroutines.*

fun runBlocking(c: suspend () -> String): String {
    var res: String? = null
    c.startCoroutine(Continuation(EmptyCoroutineContext) {
        res = it.getOrThrow()
    })
    return res!!
}

fun box(): String = runBlocking {
    return@runBlocking internalInline()
}