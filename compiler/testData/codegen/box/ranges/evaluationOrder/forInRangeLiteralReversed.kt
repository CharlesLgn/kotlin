// WITH_STDLIB
import kotlin.test.*

val log = StringBuilder()

fun logged(message: String, value: Int) =
    value.also { log.append(message) }

fun box(): String {
    var sum = 0
    for (i in (logged("start;", 1)..logged("end;", 4)).reversed()) {
        sum = sum * 10 + i
    }

    assertEquals(4321, sum)

    assertEquals("start;end;", log.toString())

    return "OK"
}