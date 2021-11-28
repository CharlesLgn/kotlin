// WITH_STDLIB

/*
 * KOTLIN CODEGEN BOX SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-300
 * MAIN LINK: expressions, constant-literals, boolean-literals -> paragraph 1 -> sentence 2
 * NUMBER: 24
 * DESCRIPTION: The use of Boolean literals as the identifier (with backtick) in the variableDeclarationEntry.
 * NOTE: this test data is generated by FeatureInteractionTestDataGenerator. DO NOT MODIFY CODE MANUALLY!
 */

val `true` = {`false`: Boolean -> !`false` }

fun f1(value: Pair<String, String>): Boolean {
    val (`false`, `true`) = value

    if (`false` != "1") return false
    if (`true` != "2") return false

    return true
}

fun box(): String? {
    var i = 0
    for (`false`: Int in 0..10) {
        i++
    }

    if (!`true`(false)) return null

    val `true` = { `false`: Boolean, `true`: Int -> true }
    var `false`: Boolean

    `false` = false

    if (!f1(Pair("1", "2"))) return null
    if (i != 11) return null
    if (!`true`(false, 10)) return null
    if (`false`) return null

    return "OK"
}