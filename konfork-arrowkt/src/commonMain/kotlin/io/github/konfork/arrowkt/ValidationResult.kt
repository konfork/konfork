package io.github.konfork.arrowkt

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.github.konfork.core.Invalid
import io.github.konfork.core.Valid
import io.github.konfork.core.PropertyValidationErrors
import io.github.konfork.core.ValidationResult

fun <E, T> ValidationResult<E, T>.toEither(): Either<List<PropertyValidationErrors<E>>, T> =
    when (this) {
        is Valid -> this.value.right()
        is Invalid -> this.errors.left()
    }

fun <E, T> ValidationResult<E, T>.toEitherFlatten(): Either<List<E>, T> =
    when (this) {
        is Valid -> this.value.right()
        is Invalid -> this.errors.flatMap(PropertyValidationErrors<E>::errors).left()
    }
