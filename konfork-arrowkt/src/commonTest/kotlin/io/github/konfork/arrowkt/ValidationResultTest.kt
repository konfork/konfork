package io.github.konfork.arrowkt

import arrow.core.left
import arrow.core.right
import io.github.konfork.core.Invalid
import io.github.konfork.core.PropertyValidationErrors
import io.github.konfork.core.Valid
import kotlin.test.Test
import kotlin.test.assertEquals

class ValidationResultTest {

    @Test
    fun toEitherLeft() {
        val errors = mapOf("path" to listOf("Oops"))
        val invalid = Invalid(errors)
        val propertyErrors = listOf(PropertyValidationErrors("path", listOf("Oops")))

        assertEquals(propertyErrors.left(), invalid.toEither())
    }

    @Test
    fun toEitherRight() {
        val value = "Some value"
        val valid = Valid(value)

        assertEquals(value.right(), valid.toEither())
    }

    @Test
    fun toEitherFlattenLeft() {
        val errors = mapOf("path" to listOf("Oops"), "path2" to listOf("Oops2"))
        val invalid = Invalid(errors)

        assertEquals(listOf("Oops", "Oops2").left(), invalid.toEitherFlatten())
    }

    @Test
    fun toEitherFlattenRight() {
        val value = "Some value"
        val valid = Valid(value)

        assertEquals(value.right(), valid.toEitherFlatten())
    }
}
