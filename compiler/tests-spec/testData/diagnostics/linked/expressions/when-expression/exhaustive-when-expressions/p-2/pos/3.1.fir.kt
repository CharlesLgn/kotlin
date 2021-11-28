// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-100
 * MAIN LINK: expressions, when-expression, exhaustive-when-expressions -> paragraph 2 -> sentence 3
 * NUMBER: 1
 * DESCRIPTION: Exhaustive when using boolean values.
 */

// TESTCASE NUMBER: 1
fun case_1(value_1: Boolean): String = when (value_1) {
    true -> ""
    false -> ""
}

// TESTCASE NUMBER: 2
fun case_2(value_1: Boolean): String = <!NO_ELSE_IN_WHEN!>when<!> (value_1) {
    <!CONFUSING_BRANCH_CONDITION_WARNING!>true && false && ((true || false)) || true && !!!false && !!!true<!> -> ""
    <!CONFUSING_BRANCH_CONDITION_WARNING!>true && false && ((true || false)) || true && !!!false<!> -> ""
}
