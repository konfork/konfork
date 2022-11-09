package io.github.konfork.test

import io.github.konfork.core.*
import kotlin.test.asserter

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
