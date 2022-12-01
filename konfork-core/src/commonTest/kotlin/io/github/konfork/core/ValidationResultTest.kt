package io.github.konfork.core

import io.github.konfork.core.validators.matches
import io.github.konfork.core.validators.maxLength
import io.github.konfork.core.validators.minLength
import io.github.konfork.test.assertThat
import kotlin.test.Test
import kotlin.test.assertEquals

class ValidationResultTest {

    @Test
    fun singleValidator() {
        val validator = Validator {
            Person::name {
                minLength(1)
            }

            Person::addresses onEach {
                Address::city {
                    City::postalCode {
                        minLength(4)
                        maxLength(5)
                        matches("\\d{4,5}") hint stringHint("must be a four or five digit number")
                    }
                }
            }
        }

        val person = Person("", addresses = listOf(Address(City("", ""))))
        val result = assertThat(validator, person)
            .isInvalid()
            .withErrorCount(3)
            .subject

        val (first, second) = result.errors

        assertEquals(".name", first.propertyPath)
        assertEquals(listOf("must have at least 1 characters"), first.errors)

        assertEquals(".addresses[0].city.postalCode", second.propertyPath)
        assertEquals(listOf("must have at least 4 characters", "must be a four or five digit number"), second.errors)
    }

    private data class Person(val name: String, val addresses: List<Address>)
    private data class Address(val city: City)
    private data class City(val postalCode: String, val cityName: String)
}
