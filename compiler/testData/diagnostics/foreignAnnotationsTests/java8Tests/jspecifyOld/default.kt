// FILE: NullnessUnspecifiedTypeParameter.java
import org.jspecify.nullness.*;

@NullMarked
public class NullnessUnspecifiedTypeParameter<T> {
    public void foo(T t) {}

    public void bar(Test s, T t) {} // t should not become not nullable
}

// FILE: Test.java
public class Test {}

// FILE: main.kt
fun main(a1: NullnessUnspecifiedTypeParameter<Any>, a2: NullnessUnspecifiedTypeParameter<<!UPPER_BOUND_VIOLATED_BASED_ON_JAVA_ANNOTATIONS!>Any?<!>>, x: Test): Unit {
    a1.foo(<!NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS!>null<!>)
    a1.foo(1)

    a2.foo(<!NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS!>null<!>)
    a2.foo(1)

    a1.bar(<!NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS!>null<!>, <!NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS!>null<!>)
    a1.bar(x, <!NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS!>null<!>)
    a1.bar(x, 1)

    a2.bar(<!NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS!>null<!>, <!NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS!>null<!>)
    a2.bar(x, <!NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS!>null<!>)
    a2.bar(x, 1)
}