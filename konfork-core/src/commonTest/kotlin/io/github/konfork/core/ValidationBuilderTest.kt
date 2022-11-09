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
    private fun ValidationBuilder<Unit, String, String>.minLength(minValue: Int) =
        addConstraint("must have at least {0} characters", minValue) { it.length >= minValue }

    private fun ValidationBuilder<Unit, String, String>.maxLength(minValue: Int) =
        addConstraint("must have at most {0} characters", minValue) { it.length <= minValue }

    private fun ValidationBuilder<Unit, String, String>.matches(regex: Regex) =
        addConstraint("must have correct format") { it.contains(regex) }

    private fun ValidationBuilder<Unit, String, String>.containsANumber() =
        matches("[0-9]".toRegex()) hint stringHint("must have at least one number")

    @Test
    fun singleValidation() {
        val validation = Validation {
            Register::password {
                minLength(1)
            }
        }

        assertThat(validation, Register(password = "a"))
            .isValid()

        assertThat(validation, Register(password = ""))
            .isInvalid()
            .withErrorCount(1)
    }

    @Test
    fun singleValidationWithContext() {
        val validation = Validation<Set<String>, String> {
            addConstraint("This value is not allowed!") { value -> this.contains(value) }
        }

        assertThat(validation, setOf("a", "b"), "a")
            .isValid()

        assertThat(validation, setOf("a", "b"), "c")
            .isInvalid()
            .withErrorCount(1)
    }

    @Test
    fun disjunctValidations() {
        val validation = Validation {
            Register::password {
                minLength(1)
            }
            Register::password {
                maxLength(10)
            }
        }

        assertThat(validation, Register(password = "a"))
            .isValid()

        assertThat(validation, Register(password = ""))
            .isInvalid()
            .withErrorCount(1)
        assertThat(validation, Register(password = "aaaaaaaaaaa"))
            .isInvalid()
            .withErrorCount(1)
    }

    @Test
    fun overlappingValidations() {
        val validation = Validation {
            Register::password {
                minLength(8)
                containsANumber()
            }
        }

        assertThat(validation, Register(password = "verysecure1"))
            .isValid()

        assertThat(validation, Register(password = "9"))
            .isInvalid()
            .withErrorCount(1)
        assertThat(validation, Register(password = "insecure"))
            .isInvalid()
            .withErrorCount(1)
        assertThat(validation, Register(password = "pass"))
            .isInvalid()
            .withErrorCount(2)
    }

    @Test
    fun lazySubValidation() {
        val validation = Validation {
            Register::password {
                minLength(8)
                lazy {
                    minLength(8)
                    containsANumber()
                }
            }
        }

        assertThat(validation, Register(password = "pass"))
            .isInvalid()
            .withErrorCount(2)
    }

    @Test
    fun lazyPropertyValidation() {
        val validation = Validation {
            Register::password lazy {
                minLength(8)
                containsANumber()
            }
        }

        assertThat(validation, Register(password = "pass"))
            .isInvalid()
            .withErrorCount(1)
    }

    @Test
    fun eagerSubValidation() {
        val validation = Validation {
            Register::password {
                minLength(8)
                eager {
                    minLength(8)
                    containsANumber()
                }
            }
        }

        assertThat(validation, Register(password = "pass"))
            .isInvalid()
            .withErrorCount(3)
    }

    @Test
    fun eagerPropertyValidation() {
        val validation = Validation {
            Register::password eager {
                minLength(8)
                containsANumber()
            }
        }

        assertThat(validation, Register(password = "pass"))
            .isInvalid()
            .withErrorCount(2)
    }

    @Test
    fun validatingMultipleFields() {
        val validation = Validation {
            Register::password {
                minLength(8)
                containsANumber()
            }

            Register::email {
                matches(".+@.+".toRegex())
            }
        }

        assertThat(validation, Register(email = "tester@test.com", password = "verysecure1"))
            .isValid()

        assertThat(validation, Register(email = "tester@test.com"))
            .isInvalid()
            .withErrorCount(2)
            .withErrorCount(2, Register::password)
        assertThat(validation, Register(password = "verysecure1"))
            .isInvalid()
            .withErrorCount(1, Register::email)
        assertThat(validation, Register())
            .isInvalid()
            .withErrorCount(3)
            .withErrorCount(1, Register::email)
            .withErrorCount(2, Register::password)
    }

    @Test
    fun validatingNullableFields() {
        val validation = Validation {
            Register::referredBy ifPresent {
                matches(".+@.+".toRegex())
            }
        }

        assertThat(validation, Register(referredBy = null))
            .isValid()
        assertThat(validation, Register(referredBy = "poweruser@test.com"))
            .isValid()

        assertThat(validation, Register(referredBy = "poweruser@"))
            .isInvalid()
            .withErrorCount(1)
    }

    @Test
    fun validatingRequiredFields() {
        val validation = Validation<Register> {
            Register::referredBy required with {
                matches(".+@.+".toRegex())
            }
        }

        assertThat(validation, Register(referredBy = "poweruser@test.com"))
            .isValid()

        assertThat(validation, Register(referredBy = null))
            .isInvalid()
            .withErrorCount(1)
        assertThat(validation, Register(referredBy = "poweruser@"))
            .isInvalid()
            .withErrorCount(1)
    }

    @Test
    fun validatingRequiredFieldsWithCustomErrorType() {
        val validation = Validation<Unit, Register, Errors> {
            Register::referredBy required with(staticHint(ONE)) {
                pattern(staticHint(TWO), ".+@.+")
            } hint staticHint(TWO)
        }

        assertThat(validation, Register(referredBy = "poweruser@test.com"))
            .isValid()

        assertThat(validation, Register(referredBy = null))
            .isInvalid()
            .withErrorCount(1, Register::referredBy)
            .withHint(TWO, Register::referredBy)
        assertThat(validation, Register(referredBy = "poweruser@"))
            .isInvalid()
            .withErrorCount(1, Register::referredBy)
            .withHint(TWO, Register::referredBy)
    }

    @Test
    fun validatingNestedTypesDirectly() {
        val validation = Validation {
            Register::home ifPresent {
                Address::address {
                    minLength(1)
                }
            }
        }

        assertThat(validation, Register(home = Address("Home")))
            .isValid()

        assertThat(validation, Register(home = Address("")))
            .isInvalid()
            .withErrorCount(1, Register::home, Address::address)
    }

    @Test
    fun validatingOptionalNullableValues() {
        val validation = Validation<String?> {
            ifPresent {
                matches(".+@.+".toRegex())
            }
        }

        assertThat(validation, null)
            .isValid()
        assertThat(validation, "poweruser@test.com")
            .isValid()

        assertThat(validation, "poweruser@")
            .isInvalid()
            .withErrorCount(1)
    }

    @Test
    fun validatingRequiredNullableValues() {
        val validation = Validation<String?> {
            required(stringHint("Whhoops!")) {
                matches(".+@.+".toRegex())
            }
        }

        assertThat(validation, "poweruser@test.com")
            .isValid()

        assertThat(validation, null)
            .isInvalid()
            .withErrorCount(1)
        assertThat(validation, "poweruser@")
            .isInvalid()
            .withErrorCount(1)
    }

    @Test
    fun alternativeSyntax() {
        val validation = Validation {
            Register::password.has.minLength(1)
            Register::password.has.maxLength(10)
            Register::email.has.matches(".+@.+".toRegex())
        }

        assertThat(validation, Register(email = "tester@test.com", password = "a"))
            .isValid()

        assertThat(validation, Register(email = "tester@test.com", password = ""))
            .isInvalid()
            .withErrorCount(1, Register::password)
        assertThat(validation, Register(email = "tester@test.com", password = "aaaaaaaaaaa"))
            .isInvalid()
            .withErrorCount(1, Register::password)
        assertThat(validation, Register(email = "tester@"))
            .isInvalid()
            .withErrorCount(2)
    }

    @Test
    fun validateLists() {

        data class Data(val registrations: List<Register> = emptyList())

        val validation = Validation {
            Data::registrations onEach {
                Register::email {
                    minLength(3)
                }
            }
        }

        assertThat(validation, Data())
            .isValid()

        assertThat(validation, Data(registrations = listOf(Register(email = "valid"), Register(email = "a"))))
            .isInvalid()
            .withErrorCount(1, Data::registrations, 1, Register::email)
        assertThat(validation, Data(registrations = listOf(Register(email = "a"), Register(email = "ab"))))
            .isInvalid()
            .withErrorCount(2)
            .withErrorCount(1, Data::registrations, 1, Register::email)
    }

    @Test
    fun validateNullableLists() {

        data class Data(val registrations: List<Register>?)

        val validation = Validation {
            Data::registrations ifPresent {
                minItems(1)
                onEach {
                    Register::email {
                        minLength(3)
                    }
                }
            }
        }

        assertThat(validation, Data(null))
            .isValid()

        assertThat(validation, Data(emptyList()))
            .isInvalid()
            .withErrorCount(1, Data::registrations)
        assertThat(validation, Data(registrations = listOf(Register(email = "valid"), Register(email = "a"))))
            .isInvalid()
            .withErrorCount(1, Data::registrations, 1, Register::email)
        assertThat(validation, Data(registrations = listOf(Register(email = "a"), Register(email = "ab"))))
            .isInvalid()
            .withErrorCount(2)
            .withErrorCount(1, Data::registrations, 0, Register::email)
            .withErrorCount(1, Data::registrations, 1, Register::email)
    }

    @Test
    fun validateArrays() {

        data class Data(val registrations: Array<Register> = emptyArray())

        val validation = Validation {
            Data::registrations onEach {
                Register::email {
                    minLength(3)
                }
            }
        }

        assertThat(validation, Data())
            .isValid()

        assertThat(validation, Data(registrations = arrayOf(Register(email = "valid"), Register(email = "a"))))
            .isInvalid()
            .withErrorCount(1, Data::registrations, 1, Register::email)
        assertThat(validation, Data(registrations = arrayOf(Register(email = "a"), Register(email = "ab"))))
            .isInvalid()
            .withErrorCount(2)
            .withErrorCount(1, Data::registrations, 0, Register::email)
            .withErrorCount(1, Data::registrations, 1, Register::email)
    }

    @Test
    fun validateNullableArrays() {

        data class Data(val registrations: Array<Register>?)

        val validation = Validation {
            Data::registrations ifPresent {
                minItems(1)
                onEach {
                    Register::email {
                        minLength(3)
                    }
                }
            }
        }

        assertThat(validation, Data(null))
            .isValid()

        assertThat(validation, Data(emptyArray()))
            .isInvalid()
            .withErrorCount(1, Data::registrations)
        assertThat(validation, Data(registrations = arrayOf(Register(email = "valid"), Register(email = "a"))))
            .isInvalid()
            .withErrorCount(1, Data::registrations, 1, Register::email)
        assertThat(validation, Data(registrations = arrayOf(Register(email = "a"), Register(email = "ab"))))
            .isInvalid()
            .withErrorCount(2)
            .withErrorCount(1, Data::registrations, 0, Register::email)
            .withErrorCount(1, Data::registrations, 1, Register::email)
    }

    @Test
    fun validateMaps() {
        val validation = Validation {
            MapData::registrations onEach {
                Map.Entry<String, Register>::value {
                    Register::email {
                        minLength(2)
                    }
                }
            }
        }

        assertThat(validation, MapData())
            .isValid()

        val data = MapData(mapOf("user1" to Register(email = "valid"), "user2" to Register(email = "a")))
        assertThat(validation, data)
            .isInvalid()
            .withErrorCount(0, MapData::registrations, "user1", Register::email)
            .withErrorCount(1, MapData::registrations, "user2", Register::email)
    }

    @Test
    fun validateNullableMaps() {

        data class Data(val registrations: Map<String, Register>? = null)

        val validation = Validation {
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

        assertThat(validation, Data(null))
            .isValid()

        assertThat(validation, Data(emptyMap()))
            .isValid()
        val data = Data(registrations = mapOf("user1" to Register(email = "valid"), "user2" to Register(email = "a")))
        assertThat(validation, data)
            .isInvalid()
            .withErrorCount(0, Data::registrations, "user1", Register::email)
            .withErrorCount(1, Data::registrations, "user2", Register::email)
    }

    @Test
    fun validateMapValue() {
        val validation = Validation {
            MapData::registrations onEachValue {
                Register::email {
                    minLength(2)
                }
            }
        }

        assertThat(validation, MapData())
            .isValid()

        val data = MapData(mapOf("user1" to Register(email = "valid"), "user2" to Register(email = "a")))
        assertThat(validation, data)
            .isInvalid()
            .withErrorCount(0, MapData::registrations, "user1", Register::email)
            .withErrorCount(1, MapData::registrations, "user2", Register::email)
    }

    @Test
    fun validateMapKey() {
        val validation = Validation {
            MapData::registrations onEachKey {
                minLength(2)
            }
        }

        assertThat(validation, MapData())
            .isValid()

        val data = MapData(mapOf("user1" to Register(email = "valid"), "u" to Register(email = "a")))
        assertThat(validation, data)
            .isInvalid()
            .withErrorCount(0, MapData::registrations, "user1")
            .withErrorCount(1, MapData::registrations, "u#key")
    }

    @Test
    fun composeValidations() {
        val addressValidation = Validation {
            Address::address {
                minLength(1)
            }
        }

        val validation = Validation {
            Register::home ifPresent {
                run(addressValidation)
            }
        }

        assertThat(validation, Register(home = Address()))
            .isInvalid()
            .withErrorCount(1)
    }

    @Test
    fun composeValidationsWithContext() {
        val addressValidation = Validation<AddressContext, Address> {
            Address::address.has.minLength(1)
            Address::country {
                addConstraint("Country is not allowed") {
                    this.validCountries.contains(it)
                }
            }
        }

        val validation = Validation<RegisterContext, Register> {
            Register::home ifPresent {
                run(addressValidation, RegisterContext::subContext)
            }
        }

        assertThat(validation, RegisterContext(), Register(home = Address()))
            .isInvalid()
            .withErrorCount(1)
    }

    @Test
    fun replacePlaceholderInString() {
        val validation = Validation<Register> {
            Register::password {
                minLength(8)
            }
        }

        assertThat(validation, Register(password = ""))
            .isInvalid()
            .withErrorCount(1, Register::password)
            .withHintMatches(".*8.*", Register::password)
    }

    private data class MapData(val registrations: Map<String, Register> = emptyMap())

    enum class Errors { ONE, TWO, }
    private data class Register(val password: String = "", val email: String = "", val referredBy: String? = null, val home: Address? = null)
    private data class Address(val address: String = "", val country: String = "DE")
    private data class RegisterContext(val subContext: AddressContext = AddressContext())
    private data class AddressContext(val validCountries: Set<String> = setOf("DE", "NL", "BE"))
}
