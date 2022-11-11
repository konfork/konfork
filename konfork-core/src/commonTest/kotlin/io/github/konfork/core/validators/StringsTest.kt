package io.github.konfork.core.validators

import io.github.konfork.core.Validator
import io.github.konfork.test.assertThat
import kotlin.test.Test

class StringsTest {

    @Test
    fun minLengthConstraint() {
        val validator = Validator { minLength(10) }

        assertThat(validator, "HelloWorld")
            .isValid()
        assertThat(validator, "Hello World")
            .isValid()

        assertThat(validator, "Hello")
            .isInvalid()
            .withHint("must have at least 10 characters")
        assertThat(validator, "")
            .isInvalid()
            .withHint("must have at least 10 characters")
    }

    @Test
    fun maxLengthConstraint() {
        val validator = Validator { maxLength(10) }

        assertThat(validator, "HelloWorld")
            .isValid()
        assertThat(validator, "Hello")
            .isValid()
        assertThat(validator, "")
            .isValid()

        assertThat(validator, "Hello World")
            .isInvalid()
            .withHint("must have at most 10 characters")
    }

    @Test
    fun patternConstraint() {
        val validator = Validator { pattern(".+@.+") }

        assertThat(validator, "a@a")
            .isValid()
        assertThat(validator, "a@a@a@a")
            .isValid()
        assertThat(validator, " a@a ")
            .isValid()

        assertThat(validator, "a")
            .isInvalid()
            .withHint("must match the expected pattern")
    }

    @Test
    fun precompiledPatternConstraint() {
        val validator = Validator { pattern("^\\w+@\\w+\\.\\w+$".toRegex()) }

        assertThat(validator, "tester@example.com")
            .isValid()

        assertThat(validator, "tester@example")
            .isInvalid()
            .withHint("must match the expected pattern")
        assertThat(validator, " tester@example.com")
            .isInvalid()
            .withHint("must match the expected pattern")
        assertThat(validator, "tester@example.com ")
            .isInvalid()
            .withHint("must match the expected pattern")
    }
}