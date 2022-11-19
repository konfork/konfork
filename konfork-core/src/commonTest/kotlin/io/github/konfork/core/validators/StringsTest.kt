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
    fun lengthInConstraint() {
        val validator = Validator { lengthIn(4..10) }

        assertThat(validator, "HelloWorld")
            .isValid()

        assertThat(validator, "Hello World")
            .isInvalid()
            .withHint("must have at least 4 and at most 10 characters")
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
        val validator = Validator { pattern("\\w+@\\w+\\.\\w+".toRegex()) }

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

    @Test
    fun uuidConstraint() {
        val validator = Validator { uuid() }

        assertThat(validator, "c63f510c-6214-11ed-9b6a-0242ac120002")
            .isValid()

        assertThat(validator, "tester@example.com")
            .isInvalid()
            .withHint("is not a valid uuid")
    }

    @Test
    fun uuidWithVersionConstraint() {
        val validator = Validator { uuid(1) }

        assertThat(validator, "c63f510c-6214-11ed-9b6a-0242ac120002")
            .isValid()

        assertThat(validator, "c63f510c-6214-21ed-9b6a-0242ac120002")
            .isInvalid()
            .withHint("is not a valid uuid version 1")
    }

    @Test
    fun nilUuidConstraint() {
        val validator = Validator { nilUuid() }

        assertThat(validator, "00000000-0000-0000-0000-000000000000")
            .isValid()

        assertThat(validator, "c63f510c-6214-21ed-9b6a-0242ac120002")
            .isInvalid()
            .withHint("is not the nil uuid")
    }

    @Test
    fun allDigitsConstraint() {
        val validator = Validator { allDigits() }

        assertThat(validator, "1234567890")
            .isValid()

        assertThat(validator, "123456789X")
            .isInvalid()
            .withHint("is not all digits")
    }

    @Test
    fun isbnConstraint() {
        val validator = Validator { isbn() }

        assertThat(validator, "3-598-21507-X")
            .isValid()
        assertThat(validator, "9781486010974")
            .isValid()

        assertThat(validator, "1234567899")
            .isInvalid()
            .withHint("is not a valid isbn")
    }

    @Test
    fun isbn10Constraint() {
        val validator = Validator { isbn10() }

        assertThat(validator, "3-598-21507-X")
            .isValid()

        assertThat(validator, "9781486010974")
            .isInvalid()
            .withHint("is not a valid isbn10")
    }

    @Test
    fun isbn13Constraint() {
        val validator = Validator { isbn13() }

        assertThat(validator, "9781486010974")
            .isValid()

        assertThat(validator, "3-598-21507-X")
            .isInvalid()
            .withHint("is not a valid isbn13")
    }

    @Test
    fun mod10Constraint() {
        val validator = Validator { mod10(1, 3) }

        assertThat(validator, "098412808666")
            .isValid()

        assertThat(validator, "3-598-21507-X")
            .isInvalid()
            .withHint("does not have a valid mod10 check digit")
    }

    @Test
    fun eanConstraint() {
        val validator = Validator { ean(12) }

        assertThat(validator, "098412808666")
            .isValid()

        assertThat(validator, "74709960")
            .isInvalid()
            .withHint("is not a valid ean12")
    }

    @Test
    fun luhnConstraint() {
        val validator = Validator { luhn() }

        assertThat(validator, "234567891234")
            .isValid()

        assertThat(validator, "74709960")
            .isInvalid()
            .withHint("does not have a valid luhn check digit")
    }

    @Test
    fun mod11Constraint() {
        val validator = Validator { mod11(7, 2) }

        assertThat(validator, "324324234235")
            .isValid()

        assertThat(validator, "74709960")
            .isInvalid()
            .withHint("does not have a valid mod11 check digit")
    }
}
