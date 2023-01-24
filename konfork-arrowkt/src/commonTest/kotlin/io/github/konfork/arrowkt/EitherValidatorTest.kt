package io.github.konfork.arrowkt

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.github.konfork.core.Specification
import io.github.konfork.core.Validator
import io.github.konfork.core.stringHint
import io.github.konfork.test.assertThat
import kotlin.test.Test

class EitherValidatorTest {

    @Test
    fun eitherValidator() {

        val validateUser = Validator<PersonRepo, Person> {
            niceNumberOfSiblings()
        }

        val user = Person("Kees")

        assertThat(validateUser, brokenPersonRepo, user)
            .isInvalid()
            .withErrorCount(1)
            .withHint("Repository error: Kapoow")

        assertThat(validateUser, noSiblingsRepo, user)
            .isInvalid()
            .withErrorCount(1)
            .withHint("Too few siblings: []")

        assertThat(validateUser, oneSiblingRepo, user)
            .isValid()
    }
}

data class Person(
    val fullName: String,
)
interface PersonRepo {
    fun siblings(name: String): Either<String, List<Person>>
}

private val brokenPersonRepo = object : PersonRepo {
    override fun siblings(name: String): Either<String, List<Person>> = "Kapoow".left()
}

private val noSiblingsRepo = object : PersonRepo {
    override fun siblings(name: String): Either<String, List<Person>> = emptyList<Person>().right()
}

private val oneSiblingRepo = object : PersonRepo {
    override fun siblings(name: String): Either<String, List<Person>> = listOf(Person("Wim")).right()
}

fun Specification<PersonRepo, Person, String>.niceNumberOfSiblings() =
    rightOrElse(
        { this.siblings(it.fullName) },
        stringHint("Repository error: {value}"),
        { it.isNotEmpty() },
        stringHint("Too few siblings: {value}"),
    )
