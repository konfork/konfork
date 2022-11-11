package io.github.konfork.core.validators

import io.github.konfork.core.Validator
import io.github.konfork.test.assertThat
import kotlin.test.Test

class ObjectsTest {

    @Test
    fun typeStringConstraint() {
        val validator = Validator<Any> { type<String>() }

        assertThat(validator, "This is a String")
            .isValid()

        assertThat(validator, 1)
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be of the correct type")

        assertThat(validator, 1.0)
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be of the correct type")

        assertThat(validator, true)
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be of the correct type")
    }

    @Test
    fun typeNumberConstraint() {
        val validator = Validator<Any> { type<Int>() }

        assertThat(validator, 1)
            .isValid()

        assertThat(validator, "String")
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be of the correct type")

        assertThat(validator, true)
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be of the correct type")
    }

    @Test
    fun genericTypeConstraint() {
        val validator = Validator<String, Any> {
            type<String, String, String> { _, t ->
                "Expected object of type 'String'"
            }
        }

        assertThat(validator, "context", "This is a String")
            .isValid()

        assertThat(validator, "context", 1)
            .isInvalid()
            .withErrorCount(1)
            .withHint("Expected object of type 'String'")
        assertThat(validator, "context", true)
            .isInvalid()
            .withErrorCount(1)
            .withHint("Expected object of type 'String'")
    }

    @Test
    fun nullableTypeConstraint() {
        val validator = Validator<Any?> { type<String?>() }

        assertThat(validator, "This is a String")
            .isValid()

        assertThat(validator, null)
            .isValid()

        assertThat(validator, true)
            .isInvalid()
    }

    @Test
    fun constConstraint() {
        val validator = Validator { const("Konfork") }

        assertThat(validator, "Konfork")
            .isValid()

        assertThat(validator, "")
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be 'Konfork'")
    }

    @Test
    fun nullConstConstraint() {
        val validator = Validator<String?> { const(null) }

        assertThat(validator, null)
            .isValid()

        assertThat(validator, "")
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be null")

        assertThat(validator, "null")
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be null")
    }

    @Test
    fun nonNullConstConstraint() {
        val validator = Validator<String?> { const("Konfork") }

        assertThat(validator, "Konfork")
            .isValid()

        assertThat(validator, null)
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be 'Konfork'")

        assertThat(validator, "Konform")
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be 'Konfork'")
    }
}