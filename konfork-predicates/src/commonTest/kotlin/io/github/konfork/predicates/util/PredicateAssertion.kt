package io.github.konfork.predicates.util

import kotlin.test.assertEquals

data class PredicateAssertion<T>(
    val value: T,
    val expected: Boolean,
)

infix fun <T> T.with(result: Boolean): PredicateAssertion<T> = PredicateAssertion(this, result)

fun <T> assert(validValues: List<T>, invalidValues: List<T>, fn: (T) -> Boolean): Unit =
    (validValues.map { it with true } + invalidValues.map { it with false })
        .assert(fn)

fun <T> List<PredicateAssertion<T>>.assert(fn: (T) -> Boolean): Unit =
    this.forEachIndexed { index, assertion ->
        assertEquals(
            assertion.expected,
            fn(assertion.value),
            "The result of predicate $index (${assertion.value}) was not ${assertion.expected}",
        )
    }
