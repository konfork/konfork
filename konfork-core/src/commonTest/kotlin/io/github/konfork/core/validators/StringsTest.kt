package io.github.konfork.core.validators

import io.github.konfork.core.Validation
import io.github.konfork.core.assertThat
import kotlin.test.Test

class StringsTest {

    @Test
    fun minLengthConstraint() {
        val validation = Validation { minLength(10) }

        assertThat(validation, "HelloWorld")
            .isValid()
        assertThat(validation, "Hello World")
            .isValid()

        assertThat(validation, "Hello")
            .isInvalid()
            .withHint("must have at least 10 characters")
        assertThat(validation, "")
            .isInvalid()
            .withHint("must have at least 10 characters")
    }

    @Test
    fun maxLengthConstraint() {
        val validation = Validation { maxLength(10) }

        assertThat(validation, "HelloWorld")
            .isValid()
        assertThat(validation, "Hello")
            .isValid()
        assertThat(validation, "")
            .isValid()

        assertThat(validation, "Hello World")
            .isInvalid()
            .withHint("must have at most 10 characters")
    }

    @Test
    fun patternConstraint() {
        val validation = Validation { pattern(".+@.+") }

        assertThat(validation, "a@a")
            .isValid()
        assertThat(validation, "a@a@a@a")
            .isValid()
        assertThat(validation, " a@a ")
            .isValid()

        assertThat(validation, "a")
            .isInvalid()
            .withHint("must match the expected pattern")
    }

    @Test
    fun precompiledPatternConstraint() {
        val validation = Validation { pattern("^\\w+@\\w+\\.\\w+$".toRegex()) }

        assertThat(validation, "tester@example.com")
            .isValid()

        assertThat(validation, "tester@example")
            .isInvalid()
            .withHint("must match the expected pattern")
        assertThat(validation, " tester@example.com")
            .isInvalid()
            .withHint("must match the expected pattern")
        assertThat(validation, "tester@example.com ")
            .isInvalid()
            .withHint("must match the expected pattern")
    }
}