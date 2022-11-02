package io.github.konfork.core.validators

import io.github.konfork.core.Validation
import io.github.konfork.core.assertThat
import kotlin.test.Test

class ObjectsTest {

    @Test
    fun typeStringConstraint() {
        val validation = Validation<Any> { type<String>() }

        assertThat(validation, "This is a String")
            .isValid()

        assertThat(validation, 1)
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be of the correct type")

        assertThat(validation, 1.0)
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be of the correct type")

        assertThat(validation, true)
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be of the correct type")
    }

    @Test
    fun typeNumberConstraint() {
        val validation = Validation<Any> { type<Int>() }

        assertThat(validation, 1)
            .isValid()

        assertThat(validation, "String")
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be of the correct type")

        assertThat(validation, true)
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be of the correct type")
    }

    @Test
    fun genericTypeConstraint() {
        val validation = Validation<String, Any> {
            type<String, String, String> { _, t ->
                "Expected object of type 'String'"
            }
        }

        assertThat(validation, "context", "This is a String")
            .isValid()

        assertThat(validation, "context", 1)
            .isInvalid()
            .withErrorCount(1)
            .withHint("Expected object of type 'String'")
        assertThat(validation, "context", true)
            .isInvalid()
            .withErrorCount(1)
            .withHint("Expected object of type 'String'")
    }

    @Test
    fun nullableTypeConstraint() {
        val validation = Validation<Any?> { type<String?>() }

        assertThat(validation, "This is a String")
            .isValid()

        assertThat(validation, null)
            .isValid()

        assertThat(validation, true)
            .isInvalid()
    }

    @Test
    fun constConstraint() {
        val validation = Validation { const("Konfork") }

        assertThat(validation, "Konfork")
            .isValid()

        assertThat(validation, "")
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be 'Konfork'")
    }

    @Test
    fun nullConstConstraint() {
        val validation = Validation<String?> { const(null) }

        assertThat(validation, null)
            .isValid()

        assertThat(validation, "")
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be null")

        assertThat(validation, "null")
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be null")
    }

    @Test
    fun nonNullConstConstraint() {
        val validation = Validation<String?> { const("Konfork") }

        assertThat(validation, "Konfork")
            .isValid()

        assertThat(validation, null)
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be 'Konfork'")

        assertThat(validation, "Konform")
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be 'Konfork'")
    }
}