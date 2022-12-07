package io.github.konfork.test

import io.github.konfork.core.Invalid
import io.github.konfork.core.ValidationErrors
import kotlin.test.DefaultAsserter
import kotlin.test.assertEquals

data class InvalidAssertion<E>(
    val subject: Invalid<E>,
) {
    fun withErrorCount(errorCount: Int, vararg properties: Any): InvalidAssertion<E> {
        val actualCount = errors(properties).sumOf { it.errors.size }
        DefaultAsserter.assertTrue(
            { "Expected <${errorCount}> errors, actual <${actualCount}>" },
            actualCount == errorCount
        )
        return this
    }

    fun withHints(expected: List<E>, vararg properties: Any): InvalidAssertion<E> {
        assertEquals(expected, subject.get(*properties).errors, "Incorrect hints")
        return this
    }

    fun withHint(expected: E, index: Int, vararg properties: Any): InvalidAssertion<E> {
        assertEquals(expected, subject.get(*properties).errors[index], "Incorrect hint")
        return this
    }

    fun withHint(expected: E, vararg properties: Any): InvalidAssertion<E> =
        withHints(listOf(expected), *properties)

    fun withHintMatch(vararg properties: Any, predicate: (E) -> Boolean): InvalidAssertion<E> {
        DefaultAsserter.assertTrue("Incorrect hint", predicate(subject.get(*properties).errors[0]))
        return this
    }

    private fun errors(properties: Array<out Any>): List<ValidationErrors<E>> =
        if (properties.isEmpty()) {
            subject.errors
        } else {
            listOf(subject.get(*properties))
        }
}

fun InvalidAssertion<String>.withHintMatches(regex: String, vararg properties: Any): InvalidAssertion<String> =
    this.withHintMatch(*properties) {
        it.matches(Regex(regex))
    }
