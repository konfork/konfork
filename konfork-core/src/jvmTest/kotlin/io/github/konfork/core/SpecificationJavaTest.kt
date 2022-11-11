package io.github.konfork.core

import io.github.konfork.core.validators.maxLength
import io.github.konfork.test.assertThat
import org.junit.jupiter.api.Test

class SpecificationJavaTest {

    @Test
    fun validateArrays() {
        val validator = Validator {
            TestSubject::stringArray onEach {
                maxLength(6)
            }
        }

        assertThat(validator, TestSubject())
            .isInvalid()
            .withErrorCount(2)
            .withHint("must have at most 6 characters", TestSubject::stringArray, 8)
            .withHint("must have at most 6 characters", TestSubject::stringArray, 12)
    }

    @Test
    fun validateIterables() {
        val validator = Validator {
            TestSubject::stringIterable onEach {
                maxLength(6)
            }
        }

        assertThat(validator, TestSubject())
            .isInvalid()
            .withErrorCount(2)
            .withHint("must have at most 6 characters", TestSubject::stringIterable, 8)
            .withHint("must have at most 6 characters", TestSubject::stringIterable, 12)
    }

    @Test
    fun validateMaps() {
        val validator = Validator {
            TestSubject::stringMap onEach {
                Map.Entry<String, String>::value {
                    maxLength(6)
                }
            }
        }

        assertThat(validator, TestSubject())
            .isInvalid()
            .withErrorCount(2)
            .withHint("must have at most 6 characters", TestSubject::stringMap, "imagination")
            .withHint("must have at most 6 characters", TestSubject::stringMap, "everywhere")
    }

    @Test
    fun validateMapValue() {
        val validator = Validator {
            TestSubject::stringMap onEachValue {
                maxLength(6)
            }
        }

        assertThat(validator, TestSubject())
            .isInvalid()
            .withErrorCount(2)
            .withHint("must have at most 6 characters", TestSubject::stringMap, "imagination")
            .withHint("must have at most 6 characters", TestSubject::stringMap, "everywhere")
    }

    @Test
    fun validateMapKey() {
        val validator = Validator {
            TestSubject::stringMap onEachKey {
                maxLength(6)
            }
        }

        assertThat(validator, TestSubject())
            .isInvalid()
            .withErrorCount(2)
            .withHint("must have at most 6 characters", TestSubject::stringMap, "imagination#key")
            .withHint("must have at most 6 characters", TestSubject::stringMap, "everywhere#key")
    }

    @Test
    fun validateIfPresent() {
        val validatorNull = Validator {
            TestSubject::nullString ifPresent {
                maxLength(2)
            }
        }
        assertThat(validatorNull, TestSubject())
            .isValid()

        val validatorNonNull = Validator {
            TestSubject::notNullString ifPresent {
                maxLength(2)
            }
        }
        assertThat(validatorNonNull, TestSubject())
            .isInvalid()
            .withErrorCount(1)
            .withHint("must have at most 2 characters", TestSubject::notNullString)
    }

    @Test
    fun validateRequired() {
        val validatorNull = Validator {
            TestSubject::nullString required with {
                maxLength(2)
            }
        }
        assertThat(validatorNull, TestSubject())
            .isInvalid()
            .withErrorCount(1)
            .withHint("is required", TestSubject::nullString)

        val validatorNonNull = Validator {
            TestSubject::notNullString required with {
                maxLength(2)
            }
        }
        assertThat(validatorNonNull, TestSubject())
            .isInvalid()
            .withErrorCount(1)
            .withHint("must have at most 2 characters", TestSubject::notNullString)
    }
}
