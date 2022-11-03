package io.github.konfork.core

import io.github.konfork.core.validators.maxLength
import org.junit.jupiter.api.Test

class ValidationBuilderJavaTest {

    @Test
    fun validateArrays() {
        val validation = Validation {
            TestSubject::stringArray onEach {
                maxLength(6)
            }
        }

        assertThat(validation, TestSubject())
            .isInvalid()
            .withErrorCount(2)
            .withHint("must have at most 6 characters", TestSubject::stringArray, 8)
            .withHint("must have at most 6 characters", TestSubject::stringArray, 12)
    }

    @Test
    fun validateIterables() {
        val validation = Validation {
            TestSubject::stringIterable onEach {
                maxLength(6)
            }
        }

        assertThat(validation, TestSubject())
            .isInvalid()
            .withErrorCount(2)
            .withHint("must have at most 6 characters", TestSubject::stringIterable, 8)
            .withHint("must have at most 6 characters", TestSubject::stringIterable, 12)
    }

    @Test
    fun validateMaps() {
        val validation = Validation {
            TestSubject::stringMap onEach {
                Map.Entry<String, String>::value {
                    maxLength(6)
                }
            }
        }

        assertThat(validation, TestSubject())
            .isInvalid()
            .withErrorCount(2)
            .withHint("must have at most 6 characters", TestSubject::stringMap, "imagination")
            .withHint("must have at most 6 characters", TestSubject::stringMap, "everywhere")
    }

    @Test
    fun validateMapValue() {
        val validation = Validation {
            TestSubject::stringMap onEachValue {
                maxLength(6)
            }
        }

        assertThat(validation, TestSubject())
            .isInvalid()
            .withErrorCount(2)
            .withHint("must have at most 6 characters", TestSubject::stringMap, "imagination")
            .withHint("must have at most 6 characters", TestSubject::stringMap, "everywhere")
    }

    @Test
    fun validateMapKey() {
        val validation = Validation {
            TestSubject::stringMap onEachKey {
                maxLength(6)
            }
        }

        assertThat(validation, TestSubject())
            .isInvalid()
            .withErrorCount(2)
            .withHint("must have at most 6 characters", TestSubject::stringMap, "imagination#key")
            .withHint("must have at most 6 characters", TestSubject::stringMap, "everywhere#key")
    }

    @Test
    fun validateIfPresent() {
        val validationNull = Validation {
            TestSubject::nullString ifPresent {
                maxLength(2)
            }
        }
        assertThat(validationNull, TestSubject())
            .isValid()

        val validationNonNull = Validation {
            TestSubject::notNullString ifPresent {
                maxLength(2)
            }
        }
        assertThat(validationNonNull, TestSubject())
            .isInvalid()
            .withErrorCount(1)
            .withHint("must have at most 2 characters", TestSubject::notNullString)
    }

    @Test
    fun validateRequired() {
        val validationNull = Validation {
            TestSubject::nullString required with {
                maxLength(2)
            }
        }
        assertThat(validationNull, TestSubject())
            .isInvalid()
            .withErrorCount(1)
            .withHint("is required", TestSubject::nullString)

        val validationNonNull = Validation {
            TestSubject::notNullString required with {
                maxLength(2)
            }
        }
        assertThat(validationNonNull, TestSubject())
            .isInvalid()
            .withErrorCount(1)
            .withHint("must have at most 2 characters", TestSubject::notNullString)
    }
}
