package io.github.konfork.core.validators

import io.github.konfork.core.Validation
import io.github.konfork.test.assertThat
import io.github.konfork.test.withHintMatches
import kotlin.test.Test
import kotlin.test.assertFailsWith

class NumbersTest {

    @Test
    fun multipleOfConstraint() {
        val validation = Validation { multipleOf(2.5, 0.0) }

        assertThat(validation, 0)
            .isValid()
        assertThat(validation, -2.5)
            .isValid()
        assertThat(validation, 2.5)
            .isValid()
        assertThat(validation, 5)
            .isValid()
        assertThat(validation, 25)
            .isValid()

        assertThat(Validation { multipleOf(0.00001, 0.0001) }, 25.13)
            .isValid()
        assertThat(Validation { multipleOf(0.00001, 0.0) }, 25.13)
            .isInvalid()
            .withHintMatches("must be a multiple of '.*'")

        assertFailsWith(IllegalArgumentException::class) { Validation { multipleOf(0, 0.0) } }
        assertFailsWith(IllegalArgumentException::class) { Validation { multipleOf(-1, 0.0) } }
        assertFailsWith(IllegalArgumentException::class) { Validation { multipleOf(1, -0.000001) } }
    }

    @Test
    fun maximumConstraint() {
        val validation = Validation { maximum(10) }

        assertThat(validation, Double.NEGATIVE_INFINITY)
            .isValid()
        assertThat(validation, -10)
            .isValid()
        assertThat(validation, 9)
            .isValid()
        assertThat(validation, 10)
            .isValid()
        assertThat(validation, 10.0)
            .isValid()

        assertThat(validation, 10.00001)
            .isInvalid()
            .withHint("must be at most '10'")
        assertThat(validation, 11)
            .isInvalid()
            .withHint("must be at most '10'")
        assertThat(validation, Double.POSITIVE_INFINITY)
            .isInvalid()
            .withHint("must be at most '10'")


        assertThat(Validation { maximum(Double.POSITIVE_INFINITY) }, Double.POSITIVE_INFINITY)
            .isValid()
    }

    @Test
    fun exclusiveMaximumConstraint() {
        val validation = Validation { exclusiveMaximum(10) }

        assertThat(validation, Double.NEGATIVE_INFINITY)
            .isValid()
        assertThat(validation, -10)
            .isValid()
        assertThat(validation, 9)
            .isValid()
        assertThat(validation, 9.99999999)
            .isValid()

        assertThat(validation, 10)
            .isInvalid()
            .withHint("must be less than '10'")
        assertThat(validation, 10.0)
            .isInvalid()
            .withHint("must be less than '10'")
        assertThat(validation, 10.00001)
            .isInvalid()
            .withHint("must be less than '10'")
        assertThat(validation, 11)
            .isInvalid()
            .withHint("must be less than '10'")
        assertThat(validation, Double.POSITIVE_INFINITY)
            .isInvalid()
            .withHint("must be less than '10'")
        assertThat(Validation { exclusiveMaximum(Double.POSITIVE_INFINITY) }, Double.POSITIVE_INFINITY)
            .isInvalid()
            .withHint("must be less than 'Infinity'")
    }

    @Test
    fun minimumConstraint() {
        val validation = Validation { minimum(10) }

        assertThat(validation, Double.POSITIVE_INFINITY)
            .isValid()
        assertThat(validation, 20)
            .isValid()
        assertThat(validation, 11)
            .isValid()
        assertThat(validation, 10.1)
            .isValid()
        assertThat(validation, 10.0)
            .isValid()

        assertThat(validation, 9.99999999999)
            .isInvalid()
            .withHint("must be at least '10'")
        assertThat(validation, 8)
            .isInvalid()
            .withHint("must be at least '10'")
        assertThat(validation, Double.NEGATIVE_INFINITY)
            .isInvalid()
            .withHint("must be at least '10'")

        assertThat(Validation { minimum(Double.NEGATIVE_INFINITY) }, Double.NEGATIVE_INFINITY)
            .isValid()
    }

    @Test
    fun minimumExclusiveConstraint() {
        val validation = Validation { exclusiveMinimum(10) }

        assertThat(validation, Double.POSITIVE_INFINITY)
            .isValid()
        assertThat(validation, 20)
            .isValid()
        assertThat(validation, 11)
            .isValid()
        assertThat(validation, 10.1)
            .isValid()

        assertThat(validation, 10)
            .isInvalid()
            .withHint("must be greater than '10'")
        assertThat(validation, 10.0)
            .isInvalid()
            .withHint("must be greater than '10'")
        assertThat(validation, 9.99999999999)
            .isInvalid()
            .withHint("must be greater than '10'")
        assertThat(validation, 8)
            .isInvalid()
            .withHint("must be greater than '10'")
        assertThat(validation, Double.NEGATIVE_INFINITY)
            .isInvalid()
            .withHint("must be greater than '10'")
        assertThat(Validation { exclusiveMinimum(Double.NEGATIVE_INFINITY) }, Double.NEGATIVE_INFINITY)
            .isInvalid()
            .withHint("must be greater than '-Infinity'")
    }
}