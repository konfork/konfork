package io.github.konfork.core.internal

import io.github.konfork.core.*
import io.github.konfork.test.assertThat
import kotlin.test.Test

class LazyValidatorNodeTest {

    private val throwingValidator =
        object : Validator<Unit, String, String> {
            override fun validate(context: Unit, value: String): ValidationResult<String, String> {
                throw IllegalStateException("This validator always throws")
            }
        }

    private val invalidValidator =
        object : Validator<Unit, String, String> {
            override fun validate(context: Unit, value: String): ValidationResult<String, String> =
                Invalid("failingValidator")
        }

    private val validValidator =
        object : Validator<Unit, String, String> {
            override fun validate(context: Unit, value: String): ValidationResult<String, String> =
                Valid(value)
        }

    @Test
    fun stopsAfterFirstInvalid() {

        val validator = LazyValidatorNode(
            listOf(
                validValidator,
                invalidValidator,
                invalidValidator,
                throwingValidator,
            )
        )

        assertThat(validator, "")
            .isInvalid()
            .withErrorCount(1)
    }
}
