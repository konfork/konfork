package io.github.konfork.core.validators

import io.github.konfork.core.Validator
import io.github.konfork.test.assertThat
import kotlin.test.Test

class CollectionsTest {

    @Test
    fun minItemsIterableConstraint() {
        val validator = Validator<List<String>> { minItems(1) }

        assertThat(validator, listOf("a", "b"))
            .isValid()

        assertThat(validator, listOf("a"))
            .isValid()

        assertThat(validator, emptyList())
            .isInvalid()
            .withErrorCount(1)
            .withHint("must have at least 1 items")
    }

    @Test
    fun minItemsArrayConstraint() {
        val validator = Validator<Array<String>> { minItems(1) }

        assertThat(validator, arrayOf("a", "b"))
            .isValid()

        assertThat(validator, arrayOf("a"))
            .isValid()

        assertThat(validator, emptyArray())
            .isInvalid()
            .withErrorCount(1)
            .withHint("must have at least 1 items")
    }

    @Test
    fun minItemsMapConstraint() {
        val validator = Validator<Map<String, Int>> { minItems(1) }

        assertThat(validator, mapOf("a" to 0, "b" to 1))
            .isValid()

        assertThat(validator, mapOf("a" to 0))
            .isValid()

        assertThat(validator, emptyMap())
            .isInvalid()
            .withErrorCount(1)
            .withHint("must have at least 1 items")
    }

    @Test
    fun maxItemsIterableConstraint() {
        val validator = Validator<List<String>> { maxItems(1) }

        assertThat(validator, emptyList())
            .isValid()

        assertThat(validator, listOf("a"))
            .isValid()

        assertThat(validator, listOf("a", "b"))
            .isInvalid()
            .withErrorCount(1)
            .withHint("must have at most 1 items")
    }

    @Test
    fun maxItemsArrayConstraint() {
        val validator = Validator<Array<String>> { maxItems(1) }

        assertThat(validator, emptyArray())
            .isValid()

        assertThat(validator, arrayOf("a"))
            .isValid()

        assertThat(validator, arrayOf("a", "b"))
            .isInvalid()
            .withErrorCount(1)
            .withHint("must have at most 1 items")
    }

    @Test
    fun maxItemsMapConstraint() {
        val validator = Validator<Map<String, Int>> { maxItems(1) }

        assertThat(validator, emptyMap())
            .isValid()

        assertThat(validator, mapOf("a" to 0))
            .isValid()

        assertThat(validator, mapOf("a" to 0, "b" to 1))
            .isInvalid()
            .withErrorCount(1)
            .withHint("must have at most 1 items")
    }

    @Test
    fun minPropertiesConstraint() {
        val validator = Validator<Map<String, Int>> { minProperties(1) }

        assertThat(validator, mapOf("a" to 0, "b" to 1))
            .isValid()

        assertThat(validator, mapOf("a" to 0))
            .isValid()

        assertThat(validator, emptyMap())
            .isInvalid()
            .withErrorCount(1)
            .withHint("must have at least 1 properties")
    }

    @Test
    fun maxPropertiesConstraint() {
        val validator = Validator<Map<String, Int>> { maxProperties(1) }

        assertThat(validator, emptyMap())
            .isValid()

        assertThat(validator, mapOf("a" to 0))
            .isValid()

        assertThat(validator, mapOf("a" to 0, "b" to 1))
            .isInvalid()
            .withErrorCount(1)
            .withHint("must have at most 1 properties")
    }

    @Test
    fun uniqueItemsIterableConstraint() {
        val validator = Validator<List<String>> { uniqueItems(true) }

        assertThat(validator, emptyList())
            .isValid()

        assertThat(validator, listOf("a"))
            .isValid()

        assertThat(validator, listOf("a", "b"))
            .isValid()

        assertThat(validator, listOf("a", "a"))
            .isInvalid()
            .withErrorCount(1)
            .withHint("all items must be unique")
    }

    @Test
    fun uniqueItemsArrayConstraint() {
        val validator = Validator<Array<String>> { uniqueItems(true) }

        assertThat(validator, emptyArray())
            .isValid()

        assertThat(validator, arrayOf("a"))
            .isValid()

        assertThat(validator, arrayOf("a", "b"))
            .isValid()

        assertThat(validator, arrayOf("a", "a"))
            .isInvalid()
            .withErrorCount(1)
            .withHint("all items must be unique")
    }
}
