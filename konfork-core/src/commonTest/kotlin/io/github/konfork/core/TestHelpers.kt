package io.github.konfork.core

import kotlin.test.DefaultAsserter.assertTrue
import kotlin.test.assertEquals
import kotlin.test.asserter

data class InvalidAssertion<E>(
    val subject: Invalid<E>,
) {
    fun withErrorCount(errorCount: Int, vararg properties: Any): InvalidAssertion<E> {
        val actualCount = errors(properties).sumOf { it.errors.size }
        assertTrue({ "Expected <${errorCount}> errors, actual <${actualCount}>" }, actualCount == errorCount)
        return this
    }

    fun withHint(expected: E, vararg properties: Any): InvalidAssertion<E> {
        assertEquals(expected, subject.get(*properties).errors[0], "Incorrect hint")
        return this
    }

    fun withHintMatch(vararg properties: Any, predicate: (E) -> Boolean): InvalidAssertion<E> {
        assertTrue("Incorrect hint", predicate(subject.get(*properties).errors[0]))
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

data class ValidAssertion<T>(
    val result: Valid<T>,
    val value: T,
)

data class ValidationResultAssertion<E, T>(
    val result: ValidationResult<E, T>,
    val value: T,
) {
    fun isValid(): ValidAssertion<T> =
        when (result) {
            is Invalid -> asserter.fail("Expected <Valid>, actual <Invalid>.")
            is Valid -> when (result.value) {
                value -> ValidAssertion(result, value)
                else -> asserter.fail("Incorrect 'value'. Expected <$value>, actual <${result.value}>.")
            }
        }

    fun isInvalid(): InvalidAssertion<E> =
        when (result) {
            is Invalid -> InvalidAssertion(result)
            is Valid -> asserter.fail("Expected <Invalid>, actual <Valid>.")
        }
}

fun <E, T> assertThat(validation: Validation<Unit, T, E>, value: T): ValidationResultAssertion<E, T> =
    ValidationResultAssertion(validation(value), value)

fun <C, E, T> assertThat(validation: Validation<C, T, E>, context: C, value: T): ValidationResultAssertion<E, T> =
    ValidationResultAssertion(validation(context, value), value)
