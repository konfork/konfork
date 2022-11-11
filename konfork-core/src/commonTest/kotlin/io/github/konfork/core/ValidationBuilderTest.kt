package io.github.konfork.core

import io.github.konfork.core.ValidationBuilderTest.Errors.ONE
import io.github.konfork.core.ValidationBuilderTest.Errors.TWO
import io.github.konfork.core.validators.minItems
import io.github.konfork.core.validators.minLength
import io.github.konfork.core.validators.pattern
import io.github.konfork.test.assertThat
import io.github.konfork.test.withHintMatches
import kotlin.test.Test

class ValidationBuilderTest {

    // Some example constraints for Testing
    private fun Specification<Unit, String, String>.minLength(minValue: Int) =
        addConstraint("must have at least {0} characters", minValue) { it.length >= minValue }

    private fun Specification<Unit, String, String>.maxLength(minValue: Int) =
        addConstraint("must have at most {0} characters", minValue) { it.length <= minValue }

    private fun Specification<Unit, String, String>.matches(regex: Regex) =
        addConstraint("must have correct format") { it.contains(regex) }

    private fun Specification<Unit, String, String>.containsANumber() =
        matches("[0-9]".toRegex()) hint stringHint("must have at least one number")

    @Test
    fun singleValidator() {
        val validator = Validator {
            Register::password {
                minLength(1)
            }
        }

        assertThat(validator, Register(password = "a"))
            .isValid()

        assertThat(validator, Register(password = ""))
            .isInvalid()
            .withErrorCount(1)
    }

    @Test
    fun singleValidatorWithContext() {
        val validator = Validator<Set<String>, String> {
            addConstraint("This value is not allowed!") { value -> this.contains(value) }
        }

        assertThat(validator, setOf("a", "b"), "a")
            .isValid()

        assertThat(validator, setOf("a", "b"), "c")
            .isInvalid()
            .withErrorCount(1)
    }

    @Test
    fun disjunctValidator() {
        val validator = Validator {
            Register::password {
                minLength(1)
            }
            Register::password {
                maxLength(10)
            }
        }

        assertThat(validator, Register(password = "a"))
            .isValid()

        assertThat(validator, Register(password = ""))
            .isInvalid()
            .withErrorCount(1)
        assertThat(validator, Register(password = "aaaaaaaaaaa"))
            .isInvalid()
            .withErrorCount(1)
    }

    @Test
    fun overlappingValidators() {
        val validator = Validator {
            Register::password {
                minLength(8)
                containsANumber()
            }
        }

        assertThat(validator, Register(password = "verysecure1"))
            .isValid()

        assertThat(validator, Register(password = "9"))
            .isInvalid()
            .withErrorCount(1)
        assertThat(validator, Register(password = "insecure"))
            .isInvalid()
            .withErrorCount(1)
        assertThat(validator, Register(password = "pass"))
            .isInvalid()
            .withErrorCount(2)
    }

    @Test
    fun lazySubValidator() {
        val validator = Validator {
            Register::password {
                minLength(8)
                lazy {
                    minLength(8)
                    containsANumber()
                }
            }
        }

        assertThat(validator, Register(password = "pass"))
            .isInvalid()
            .withErrorCount(2)
    }

    @Test
    fun lazyPropertyValidator() {
        val validator = Validator {
            Register::password lazy {
                minLength(8)
                containsANumber()
            }
        }

        assertThat(validator, Register(password = "pass"))
            .isInvalid()
            .withErrorCount(1)
    }

    @Test
    fun eagerSubValidator() {
        val validator = Validator {
            Register::password {
                minLength(8)
                eager {
                    minLength(8)
                    containsANumber()
                }
            }
        }

        assertThat(validator, Register(password = "pass"))
            .isInvalid()
            .withErrorCount(3)
    }

    @Test
    fun eagerPropertyValidator() {
        val validator = Validator {
            Register::password eager {
                minLength(8)
                containsANumber()
            }
        }

        assertThat(validator, Register(password = "pass"))
            .isInvalid()
            .withErrorCount(2)
    }

    @Test
    fun validatingMultipleFields() {
        val validator = Validator {
            Register::password {
                minLength(8)
                containsANumber()
            }

            Register::email {
                matches(".+@.+".toRegex())
            }
        }

        assertThat(validator, Register(email = "tester@test.com", password = "verysecure1"))
            .isValid()

        assertThat(validator, Register(email = "tester@test.com"))
            .isInvalid()
            .withErrorCount(2)
            .withErrorCount(2, Register::password)
        assertThat(validator, Register(password = "verysecure1"))
            .isInvalid()
            .withErrorCount(1, Register::email)
        assertThat(validator, Register())
            .isInvalid()
            .withErrorCount(3)
            .withErrorCount(1, Register::email)
            .withErrorCount(2, Register::password)
    }

    @Test
    fun validatingNullableFields() {
        val validator = Validator {
            Register::referredBy ifPresent {
                matches(".+@.+".toRegex())
            }
        }

        assertThat(validator, Register(referredBy = null))
            .isValid()
        assertThat(validator, Register(referredBy = "poweruser@test.com"))
            .isValid()

        assertThat(validator, Register(referredBy = "poweruser@"))
            .isInvalid()
            .withErrorCount(1)
    }

    @Test
    fun validatingRequiredFields() {
        val validator = Validator {
            Register::referredBy required with {
                matches(".+@.+".toRegex())
            }
        }

        assertThat(validator, Register(referredBy = "poweruser@test.com"))
            .isValid()

        assertThat(validator, Register(referredBy = null))
            .isInvalid()
            .withErrorCount(1)
        assertThat(validator, Register(referredBy = "poweruser@"))
            .isInvalid()
            .withErrorCount(1)
    }

    @Test
    fun validatingRequiredFieldsWithCustomErrorType() {
        val validator = Validator<Unit, Register, Errors> {
            Register::referredBy required with(staticHint(ONE)) {
                pattern(staticHint(TWO), ".+@.+")
            } hint staticHint(TWO)
        }

        assertThat(validator, Register(referredBy = "poweruser@test.com"))
            .isValid()

        assertThat(validator, Register(referredBy = null))
            .isInvalid()
            .withErrorCount(1, Register::referredBy)
            .withHint(TWO, Register::referredBy)
        assertThat(validator, Register(referredBy = "poweruser@"))
            .isInvalid()
            .withErrorCount(1, Register::referredBy)
            .withHint(TWO, Register::referredBy)
    }

    @Test
    fun validatingNestedTypesDirectly() {
        val validator = Validator {
            Register::home ifPresent {
                Address::address {
                    minLength(1)
                }
            }
        }

        assertThat(validator, Register(home = Address("Home")))
            .isValid()

        assertThat(validator, Register(home = Address("")))
            .isInvalid()
            .withErrorCount(1, Register::home, Address::address)
    }

    @Test
    fun validatingOptionalNullableValues() {
        val validator = Validator<String?> {
            ifPresent {
                matches(".+@.+".toRegex())
            }
        }

        assertThat(validator, null)
            .isValid()
        assertThat(validator, "poweruser@test.com")
            .isValid()

        assertThat(validator, "poweruser@")
            .isInvalid()
            .withErrorCount(1)
    }

    @Test
    fun validatingRequiredNullableValues() {
        val validator = Validator<String?> {
            required(stringHint("Whhoops!")) {
                matches(".+@.+".toRegex())
            }
        }

        assertThat(validator, "poweruser@test.com")
            .isValid()

        assertThat(validator, null)
            .isInvalid()
            .withErrorCount(1)
        assertThat(validator, "poweruser@")
            .isInvalid()
            .withErrorCount(1)
    }

    @Test
    fun alternativeSyntax() {
        val validator = Validator {
            Register::password.has.minLength(1)
            Register::password.has.maxLength(10)
            Register::email.has.matches(".+@.+".toRegex())
        }

        assertThat(validator, Register(email = "tester@test.com", password = "a"))
            .isValid()

        assertThat(validator, Register(email = "tester@test.com", password = ""))
            .isInvalid()
            .withErrorCount(1, Register::password)
        assertThat(validator, Register(email = "tester@test.com", password = "aaaaaaaaaaa"))
            .isInvalid()
            .withErrorCount(1, Register::password)
        assertThat(validator, Register(email = "tester@"))
            .isInvalid()
            .withErrorCount(2)
    }

    @Test
    fun validateLists() {

        data class Data(val registrations: List<Register> = emptyList())

        val validator = Validator {
            Data::registrations onEach {
                Register::email {
                    minLength(3)
                }
            }
        }

        assertThat(validator, Data())
            .isValid()

        assertThat(validator, Data(registrations = listOf(Register(email = "valid"), Register(email = "a"))))
            .isInvalid()
            .withErrorCount(1, Data::registrations, 1, Register::email)
        assertThat(validator, Data(registrations = listOf(Register(email = "a"), Register(email = "ab"))))
            .isInvalid()
            .withErrorCount(2)
            .withErrorCount(1, Data::registrations, 1, Register::email)
    }

    @Test
    fun validateNullableLists() {

        data class Data(val registrations: List<Register>?)

        val validator = Validator {
            Data::registrations ifPresent {
                minItems(1)
                onEach {
                    Register::email {
                        minLength(3)
                    }
                }
            }
        }

        assertThat(validator, Data(null))
            .isValid()

        assertThat(validator, Data(emptyList()))
            .isInvalid()
            .withErrorCount(1, Data::registrations)
        assertThat(validator, Data(registrations = listOf(Register(email = "valid"), Register(email = "a"))))
            .isInvalid()
            .withErrorCount(1, Data::registrations, 1, Register::email)
        assertThat(validator, Data(registrations = listOf(Register(email = "a"), Register(email = "ab"))))
            .isInvalid()
            .withErrorCount(2)
            .withErrorCount(1, Data::registrations, 0, Register::email)
            .withErrorCount(1, Data::registrations, 1, Register::email)
    }

    @Test
    fun validateArrays() {

        data class Data(val registrations: Array<Register> = emptyArray())

        val validator = Validator {
            Data::registrations onEach {
                Register::email {
                    minLength(3)
                }
            }
        }

        assertThat(validator, Data())
            .isValid()

        assertThat(validator, Data(registrations = arrayOf(Register(email = "valid"), Register(email = "a"))))
            .isInvalid()
            .withErrorCount(1, Data::registrations, 1, Register::email)
        assertThat(validator, Data(registrations = arrayOf(Register(email = "a"), Register(email = "ab"))))
            .isInvalid()
            .withErrorCount(2)
            .withErrorCount(1, Data::registrations, 0, Register::email)
            .withErrorCount(1, Data::registrations, 1, Register::email)
    }

    @Test
    fun validateNullableArrays() {

        data class Data(val registrations: Array<Register>?)

        val validator = Validator {
            Data::registrations ifPresent {
                minItems(1)
                onEach {
                    Register::email {
                        minLength(3)
                    }
                }
            }
        }

        assertThat(validator, Data(null))
            .isValid()

        assertThat(validator, Data(emptyArray()))
            .isInvalid()
            .withErrorCount(1, Data::registrations)
        assertThat(validator, Data(registrations = arrayOf(Register(email = "valid"), Register(email = "a"))))
            .isInvalid()
            .withErrorCount(1, Data::registrations, 1, Register::email)
        assertThat(validator, Data(registrations = arrayOf(Register(email = "a"), Register(email = "ab"))))
            .isInvalid()
            .withErrorCount(2)
            .withErrorCount(1, Data::registrations, 0, Register::email)
            .withErrorCount(1, Data::registrations, 1, Register::email)
    }

    @Test
    fun validateMaps() {
        val validator = Validator {
            MapData::registrations onEach {
                Map.Entry<String, Register>::value {
                    Register::email {
                        minLength(2)
                    }
                }
            }
        }

        assertThat(validator, MapData())
            .isValid()

        val data = MapData(mapOf("user1" to Register(email = "valid"), "user2" to Register(email = "a")))
        assertThat(validator, data)
            .isInvalid()
            .withErrorCount(0, MapData::registrations, "user1", Register::email)
            .withErrorCount(1, MapData::registrations, "user2", Register::email)
    }

    @Test
    fun validateNullableMaps() {

        data class Data(val registrations: Map<String, Register>? = null)

        val validator = Validator {
            Data::registrations ifPresent  {
                onEach {
                    Map.Entry<String, Register>::value {
                        Register::email {
                            minLength(2)
                        }
                    }
                }
            }
        }

        assertThat(validator, Data(null))
            .isValid()

        assertThat(validator, Data(emptyMap()))
            .isValid()
        val data = Data(registrations = mapOf("user1" to Register(email = "valid"), "user2" to Register(email = "a")))
        assertThat(validator, data)
            .isInvalid()
            .withErrorCount(0, Data::registrations, "user1", Register::email)
            .withErrorCount(1, Data::registrations, "user2", Register::email)
    }

    @Test
    fun validateMapValue() {
        val validator = Validator {
            MapData::registrations onEachValue {
                Register::email {
                    minLength(2)
                }
            }
        }

        assertThat(validator, MapData())
            .isValid()

        val data = MapData(mapOf("user1" to Register(email = "valid"), "user2" to Register(email = "a")))
        assertThat(validator, data)
            .isInvalid()
            .withErrorCount(0, MapData::registrations, "user1", Register::email)
            .withErrorCount(1, MapData::registrations, "user2", Register::email)
    }

    @Test
    fun validateMapKey() {
        val validator = Validator {
            MapData::registrations onEachKey {
                minLength(2)
            }
        }

        assertThat(validator, MapData())
            .isValid()

        val data = MapData(mapOf("user1" to Register(email = "valid"), "u" to Register(email = "a")))
        assertThat(validator, data)
            .isInvalid()
            .withErrorCount(0, MapData::registrations, "user1")
            .withErrorCount(1, MapData::registrations, "u#key")
    }

    private val addressValidator = Validator {
        Address::address {
            minLength(1)
        }
    }

    private val addressValidatorWithContext = Validator<AddressContext, Address> {
        Address::address.has.minLength(1)
        Address::country {
            addConstraint("Country is not allowed") {
                this.validCountries.contains(it)
            }
        }
    }

    @Test
    fun composeValidators() {
        val validator = Validator {
            Register::secondaryHome ifPresent {
                apply(addressValidator)
            }
        }

        assertThat(validator, Register(secondaryHome = Address()))
            .isInvalid()
            .withErrorCount(1)
    }

    @Test
    fun composeValidatorsInfix() {
        val validator = Validator {
            Register::home apply addressValidator
        }

        assertThat(validator, Register())
            .isInvalid()
            .withErrorCount(1)
    }

    @Test
    fun composeValidatorsWithContext() {
        val validator = Validator<RegisterContext, Register> {
            Register::home ifPresent {
                apply(addressValidatorWithContext, RegisterContext::subContext)
            }
        }

        assertThat(validator, RegisterContext(), Register(home = Address()))
            .isInvalid()
            .withErrorCount(1)
    }

    @Test
    fun replacePlaceholderInString() {
        val validator = Validator {
            Register::password {
                minLength(8)
            }
        }

        assertThat(validator, Register(password = ""))
            .isInvalid()
            .withErrorCount(1, Register::password)
            .withHintMatches(".*8.*", Register::password)
    }

    private data class MapData(val registrations: Map<String, Register> = emptyMap())

    enum class Errors { ONE, TWO, }
    private data class Register(
        val password: String = "",
        val email: String = "",
        val referredBy: String? = null,
        val home: Address = Address(),
        val secondaryHome: Address? = null,
    )
    private data class Address(val address: String = "", val country: String = "DE")
    private data class RegisterContext(val subContext: AddressContext = AddressContext())
    private data class AddressContext(val validCountries: Set<String> = setOf("DE", "NL", "BE"))
}
