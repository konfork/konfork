package io.github.konfork.core.internal

import io.github.konfork.core.*
import io.github.konfork.test.assertThat
import kotlin.test.Test

class LazyValidationNodeTest {

    private val throwingValidation =
        object : Validation<Unit, String, String> {
            override fun validate(context: Unit, value: String): ValidationResult<String, String> {
                throw IllegalStateException("This validation always throws")
            }
        }

    private val invalidValidation =
        object : Validation<Unit, String, String> {
            override fun validate(context: Unit, value: String): ValidationResult<String, String> =
                Invalid("failingValidation")
        }

    private val validValidation =
        object : Validation<Unit, String, String> {
            override fun validate(context: Unit, value: String): ValidationResult<String, String> =
                Valid(value)
        }

    @Test
    fun stopsAfterFirstInvalid() {

        val validation = LazyValidationNode(
            listOf(
                validValidation,
                invalidValidation,
                invalidValidation,
                throwingValidation,
            )
        )

        assertThat(validation, "")
            .isInvalid()
            .withErrorCount(1)
    }
}
