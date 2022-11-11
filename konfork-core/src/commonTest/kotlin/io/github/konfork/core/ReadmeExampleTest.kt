package io.github.konfork.core

import io.github.konfork.core.validators.*
import io.github.konfork.test.assertThat
import kotlin.collections.Map.Entry
import kotlin.test.Test

class ReadmeExampleTest {

    @Test
    fun simpleValidator() {
        data class UserProfile(
            val fullName: String,
            val age: Int?
        )

        val validateUser = Validator {
            UserProfile::fullName {
                minLength(2)
                maxLength(100)
            }

            UserProfile::age ifPresent {
                minimum(0)
                maximum(150)
            }
        }

        val invalidUser = UserProfile("A", -1)

        assertThat(validateUser, invalidUser)
            .isInvalid()
            .withErrorCount(2)
            .withHint("must have at least 2 characters", UserProfile::fullName)
            .withHint("must be at least '0'", UserProfile::age)
    }

    @Test
    fun complexValidator() {
        data class Person(val name: String, val email: String?, val age: Int)

        fun <C> Specification<C, Person, String>.attendeeValidator() {
            Person::name {
                minLength(2)
            }
            Person::age {
                minimum(18) hint "Attendees must be 18 years or older"
            }
            // Email is optional but if it is set it must be valid
            Person::email ifPresent {
                pattern("\\w+@\\w+\\.\\w+") hint "Please provide a valid email address (optional)"
            }
        }

        data class Event(
            val organizer: Person,
            val attendees: List<Person>,
            val ticketPrices: Map<String, Double?>
        )

        val validateEvent = Validator {
            Event::organizer {
                // even though the email is nullable you can force it to be set in the validation
                Person::email required with {
                    pattern("\\w+@bigcorp.com") hint "Organizers must have a BigCorp email address"
                }
            }

            // validation on the attendees list
            Event::attendees {
                maxItems(100)
            }

            // validation on individual attendees
            Event::attendees onEach { attendeeValidator() }

            // validation on the ticketPrices Map as a whole
            Event::ticketPrices {
                minItems(1) hint "Provide at least one ticket price"
            }

            // validations for the individual entries
            Event::ticketPrices onEach {
                // Tickets may be free
                Entry<String, Double?>::value ifPresent {
                    minimum(0.01)
                }
            }
        }

        val validEvent = Event(
            organizer = Person("Organizer", "organizer@bigcorp.com", 30),
            attendees = listOf(
                Person("Visitor", null, 18),
                Person("Journalist", "hello@world.com", 35)
            ),
            ticketPrices = mapOf(
                "diversity-ticket" to null,
                "early-bird" to 200.0,
                "regular" to 400.0
            )
        )

        assertThat(validateEvent, validEvent)
            .isValid()


        val invalidEvent = Event(
            organizer = Person("Organizer", "organizer@smallcorp.com", 30),
            attendees = listOf(
                Person("Youngster", null, 17)
            ),
            ticketPrices = mapOf(
                "we-pay-you" to -100.0
            )
        )

        assertThat(validateEvent, invalidEvent)
            .isInvalid()
            .withErrorCount(3)
            .withHint("Organizers must have a BigCorp email address", Event::organizer, Person::email)
            .withHint("Attendees must be 18 years or older", Event::attendees, 0, Person::age)
            .withHint("must be at least '0.01'", Event::ticketPrices, "we-pay-you")
    }
}
