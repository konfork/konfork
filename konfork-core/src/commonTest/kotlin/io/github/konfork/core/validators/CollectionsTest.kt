package io.github.konfork.core.validators

import io.github.konfork.core.Validation
import io.github.konfork.test.assertThat
import kotlin.test.Test

class CollectionsTest {

    @Test
    fun minItemsIterableConstraint() {
        val validation = Validation<List<String>> { minItems(1) }

        assertThat(validation, listOf("a", "b"))
            .isValid()

        assertThat(validation, listOf("a"))
            .isValid()

        assertThat(validation, emptyList())
            .isInvalid()
            .withErrorCount(1)
            .withHint("must have at least 1 items")
    }

    @Test
    fun minItemsArrayConstraint() {
        val validation = Validation<Array<String>> { minItems(1) }

        assertThat(validation, arrayOf("a", "b"))
            .isValid()

        assertThat(validation, arrayOf("a"))
            .isValid()

        assertThat(validation, emptyArray())
            .isInvalid()
            .withErrorCount(1)
            .withHint("must have at least 1 items")
    }

    @Test
    fun minItemsMapConstraint() {
        val validation = Validation<Map<String, Int>> { minItems(1) }

        assertThat(validation, mapOf("a" to 0, "b" to 1))
            .isValid()

        assertThat(validation, mapOf("a" to 0))
            .isValid()

        assertThat(validation, emptyMap())
            .isInvalid()
            .withErrorCount(1)
            .withHint("must have at least 1 items")
    }

    @Test
    fun maxItemsIterableConstraint() {
        val validation = Validation<List<String>> { maxItems(1) }

        assertThat(validation, emptyList())
            .isValid()

        assertThat(validation, listOf("a"))
            .isValid()

        assertThat(validation, listOf("a", "b"))
            .isInvalid()
            .withErrorCount(1)
            .withHint("must have at most 1 items")
    }

    @Test
    fun maxItemsArrayConstraint() {
        val validation = Validation<Array<String>> { maxItems(1) }

        assertThat(validation, emptyArray())
            .isValid()

        assertThat(validation, arrayOf("a"))
            .isValid()

        assertThat(validation, arrayOf("a", "b"))
            .isInvalid()
            .withErrorCount(1)
            .withHint("must have at most 1 items")
    }

    @Test
    fun maxItemsMapConstraint() {
        val validation = Validation<Map<String, Int>> { maxItems(1) }

        assertThat(validation, emptyMap())
            .isValid()

        assertThat(validation, mapOf("a" to 0))
            .isValid()

        assertThat(validation, mapOf("a" to 0, "b" to 1))
            .isInvalid()
            .withErrorCount(1)
            .withHint("must have at most 1 items")
    }

    @Test
    fun minPropertiesConstraint() {
        val validation = Validation<Map<String, Int>> { minProperties(1) }

        assertThat(validation, mapOf("a" to 0, "b" to 1))
            .isValid()

        assertThat(validation, mapOf("a" to 0))
            .isValid()

        assertThat(validation, emptyMap())
            .isInvalid()
            .withErrorCount(1)
            .withHint("must have at least 1 properties")
    }

    @Test
    fun maxPropertiesConstraint() {
        val validation = Validation<Map<String, Int>> { maxProperties(1) }

        assertThat(validation, emptyMap())
            .isValid()

        assertThat(validation, mapOf("a" to 0))
            .isValid()

        assertThat(validation, mapOf("a" to 0, "b" to 1))
            .isInvalid()
            .withErrorCount(1)
            .withHint("must have at most 1 properties")
    }

    @Test
    fun uniqueItemsIterableConstraint() {
        val validation = Validation<List<String>> { uniqueItems(true) }

        assertThat(validation, emptyList())
            .isValid()

        assertThat(validation, listOf("a"))
            .isValid()

        assertThat(validation, listOf("a", "b"))
            .isValid()

        assertThat(validation, listOf("a", "a"))
            .isInvalid()
            .withErrorCount(1)
            .withHint("all items must be unique")
    }

    @Test
    fun uniqueItemsArrayConstraint() {
        val validation = Validation<Array<String>> { uniqueItems(true) }

        assertThat(validation, emptyArray())
            .isValid()

        assertThat(validation, arrayOf("a"))
            .isValid()

        assertThat(validation, arrayOf("a", "b"))
            .isValid()

        assertThat(validation, arrayOf("a", "a"))
            .isInvalid()
            .withErrorCount(1)
            .withHint("all items must be unique")
    }
}
