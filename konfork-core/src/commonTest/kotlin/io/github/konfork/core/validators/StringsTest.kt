package io.github.konfork.core.validators

import io.github.konfork.core.Validator
import io.github.konfork.test.assertThat
import kotlin.test.Test

class StringsTest {
    @Test
    fun allConstraint() {
        val validator = Validator { all(Char::isDigit) }

        assertThat(validator, "0123456789")
            .isValid()

        assertThat(validator, "0123X")
            .isInvalid()
            .withHint("not all characters comply")
    }

    @Test
    fun anyConstraint() {
        val validator = Validator { any(Char::isDigit) }

        assertThat(validator, "abcd0")
            .isValid()

        assertThat(validator, "abcde")
            .isInvalid()
            .withHint("none of the characters comply")
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
    fun containsConstraint() {
        val validator = Validator { contains('a') }

        assertThat(validator, "abcde")
            .isValid()

        assertThat(validator, "bcdef")
            .isInvalid()
            .withHint("does not contain character 'a'")
    }

    @Test
    fun containsIgnoringCaseConstraint() {
        val validator = Validator { contains('a', true) }

        assertThat(validator, "Abcde")
            .isValid()

        assertThat(validator, "bcdef")
            .isInvalid()
            .withHint("does not contain character 'a' when ignoring case")
    }

    @Test
    fun contentEqualsConstraint() {
        val validator = Validator { contentEquals("Hello, World!") }

        assertThat(validator, "Hello, World!")
            .isValid()

        assertThat(validator, "Goodbye, World!")
            .isInvalid()
            .withHint("content not equal")
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
    fun emailConstraint() {
        val validator = Validator { email() }

        assertThat(validator, "tester@example.com")
            .isValid()

        assertThat(validator, "testerexample.com")
            .isInvalid()
            .withHint("is not a valid email")
    }

    @Test
    fun endsWithConstraint() {
        val validator = Validator { endsWith("World!") }

        assertThat(validator, "Hello, World!")
            .isValid()

        assertThat(validator, "Hello, Moon!")
            .isInvalid()
            .withHint("does not end with \"World!\"")
    }

    @Test
    fun endsWithIgnoringCaseConstraint() {
        val validator = Validator { endsWith("world!", true) }

        assertThat(validator, "Hello, World!")
            .isValid()

        assertThat(validator, "Hello, Moon!")
            .isInvalid()
            .withHint("does not end with \"world!\" when ignoring case")
    }

    @Test
    fun isBlankConstraint() {
        val validator = Validator { isBlank() }

        assertThat(validator, "    ")
            .isValid()

        assertThat(validator, "a")
            .isInvalid()
            .withHint("is not blank")
    }

    @Test
    fun isEmptyConstraint() {
        val validator = Validator { isEmpty() }

        assertThat(validator, "")
            .isValid()

        assertThat(validator, " ")
            .isInvalid()
            .withHint("is not empty")
    }

    @Test
    fun isNotBlankConstraint() {
        val validator = Validator { isNotBlank() }

        assertThat(validator, "a")
            .isValid()

        assertThat(validator, "  ")
            .isInvalid()
            .withHint("is blank")
    }

    @Test
    fun isNotEmptyConstraint() {
        val validator = Validator { isNotEmpty() }

        assertThat(validator, " ")
            .isValid()

        assertThat(validator, "")
            .isInvalid()
            .withHint("is empty")
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
    fun lengthInConstraint() {
        val validator = Validator { lengthIn(4..10) }

        assertThat(validator, "HelloWorld")
            .isValid()

        assertThat(validator, "Hello World")
            .isInvalid()
            .withHint("must have at least 4 and at most 10 characters")
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
    fun mod10Constraint() {
        val validator = Validator { mod10(1, 3) }

        assertThat(validator, "098412808666")
            .isValid()

        assertThat(validator, "3-598-21507-X")
            .isInvalid()
            .withHint("does not have a valid mod10 check digit")
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
    fun noneConstraint() {
        val validator = Validator { none(Char::isDigit) }

        assertThat(validator, "abcd")
            .isValid()

        assertThat(validator, "abc0")
            .isInvalid()
            .withHint("some character does comply")
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
    fun startsWithConstraint() {
        val validator = Validator { startsWith("Hello") }

        assertThat(validator, "Hello, World!")
            .isValid()

        assertThat(validator, "Goodbye, World!")
            .isInvalid()
            .withHint("does not start with \"Hello\"")
    }

    @Test
    fun startsWithIgnoringCaseConstraint() {
        val validator = Validator { startsWith("hello", true) }

        assertThat(validator, "Hello, World!")
            .isValid()

        assertThat(validator, "Goodbye, World!")
            .isInvalid()
            .withHint("does not start with \"hello\" when ignoring case")
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

}
