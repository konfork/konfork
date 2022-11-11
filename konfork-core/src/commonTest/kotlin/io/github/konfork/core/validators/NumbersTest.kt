package io.github.konfork.core.validators

import io.github.konfork.core.Validator
import io.github.konfork.test.assertThat
import io.github.konfork.test.withHintMatches
import kotlin.test.Test
import kotlin.test.assertFailsWith

class NumbersTest {

    @Test
    fun multipleOfConstraint() {
        val validator = Validator { multipleOf(2.5, 0.0) }

        assertThat(validator, 0)
            .isValid()
        assertThat(validator, -2.5)
            .isValid()
        assertThat(validator, 2.5)
            .isValid()
        assertThat(validator, 5)
            .isValid()
        assertThat(validator, 25)
            .isValid()

        assertThat(Validator { multipleOf(0.00001, 0.0001) }, 25.13)
            .isValid()
        assertThat(Validator { multipleOf(0.00001, 0.0) }, 25.13)
            .isInvalid()
            .withHintMatches("must be a multiple of '.*'")

        assertFailsWith(IllegalArgumentException::class) { Validator { multipleOf(0, 0.0) } }
        assertFailsWith(IllegalArgumentException::class) { Validator { multipleOf(-1, 0.0) } }
        assertFailsWith(IllegalArgumentException::class) { Validator { multipleOf(1, -0.000001) } }
    }

    @Test
    fun maximumConstraint() {
        val validator = Validator { maximum(10) }

        assertThat(validator, Double.NEGATIVE_INFINITY)
            .isValid()
        assertThat(validator, -10)
            .isValid()
        assertThat(validator, 9)
            .isValid()
        assertThat(validator, 10)
            .isValid()
        assertThat(validator, 10.0)
            .isValid()

        assertThat(validator, 10.00001)
            .isInvalid()
            .withHint("must be at most '10'")
        assertThat(validator, 11)
            .isInvalid()
            .withHint("must be at most '10'")
        assertThat(validator, Double.POSITIVE_INFINITY)
            .isInvalid()
            .withHint("must be at most '10'")


        assertThat(Validator { maximum(Double.POSITIVE_INFINITY) }, Double.POSITIVE_INFINITY)
            .isValid()
    }

    @Test
    fun exclusiveMaximumConstraint() {
        val validator = Validator { exclusiveMaximum(10) }

        assertThat(validator, Double.NEGATIVE_INFINITY)
            .isValid()
        assertThat(validator, -10)
            .isValid()
        assertThat(validator, 9)
            .isValid()
        assertThat(validator, 9.99999999)
            .isValid()

        assertThat(validator, 10)
            .isInvalid()
            .withHint("must be less than '10'")
        assertThat(validator, 10.0)
            .isInvalid()
            .withHint("must be less than '10'")
        assertThat(validator, 10.00001)
            .isInvalid()
            .withHint("must be less than '10'")
        assertThat(validator, 11)
            .isInvalid()
            .withHint("must be less than '10'")
        assertThat(validator, Double.POSITIVE_INFINITY)
            .isInvalid()
            .withHint("must be less than '10'")
        assertThat(Validator { exclusiveMaximum(Double.POSITIVE_INFINITY) }, Double.POSITIVE_INFINITY)
            .isInvalid()
            .withHint("must be less than 'Infinity'")
    }

    @Test
    fun minimumConstraint() {
        val validator = Validator { minimum(10) }

        assertThat(validator, Double.POSITIVE_INFINITY)
            .isValid()
        assertThat(validator, 20)
            .isValid()
        assertThat(validator, 11)
            .isValid()
        assertThat(validator, 10.1)
            .isValid()
        assertThat(validator, 10.0)
            .isValid()

        assertThat(validator, 9.99999999999)
            .isInvalid()
            .withHint("must be at least '10'")
        assertThat(validator, 8)
            .isInvalid()
            .withHint("must be at least '10'")
        assertThat(validator, Double.NEGATIVE_INFINITY)
            .isInvalid()
            .withHint("must be at least '10'")

        assertThat(Validator { minimum(Double.NEGATIVE_INFINITY) }, Double.NEGATIVE_INFINITY)
            .isValid()
    }

    @Test
    fun minimumExclusiveConstraint() {
        val validator = Validator { exclusiveMinimum(10) }

        assertThat(validator, Double.POSITIVE_INFINITY)
            .isValid()
        assertThat(validator, 20)
            .isValid()
        assertThat(validator, 11)
            .isValid()
        assertThat(validator, 10.1)
            .isValid()

        assertThat(validator, 10)
            .isInvalid()
            .withHint("must be greater than '10'")
        assertThat(validator, 10.0)
            .isInvalid()
            .withHint("must be greater than '10'")
        assertThat(validator, 9.99999999999)
            .isInvalid()
            .withHint("must be greater than '10'")
        assertThat(validator, 8)
            .isInvalid()
            .withHint("must be greater than '10'")
        assertThat(validator, Double.NEGATIVE_INFINITY)
            .isInvalid()
            .withHint("must be greater than '10'")
        assertThat(Validator { exclusiveMinimum(Double.NEGATIVE_INFINITY) }, Double.NEGATIVE_INFINITY)
            .isInvalid()
            .withHint("must be greater than '-Infinity'")
    }
}