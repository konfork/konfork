package io.github.konfork.arrowkt

import arrow.core.Either
import io.github.konfork.core.*
import io.github.konfork.core.builders.MappedContextValueBuilder
import io.github.konfork.core.builders.ValidatorBuilder

class EitherValidator<C, L, R, E>(
    private val leftHint: HintBuilder<C, L, E>,
    private val test: C.(R) -> Boolean,
    private val rightHint: HintBuilder<C, R, E>,
) : Validator<C, Either<L, R>, E> {
    override fun validate(context: C, value: Either<L, R>): ValidationResult<E, Either<L, R>> =
        value
            .fold(
                { Invalid(context.leftHint(it, emptyList())) },
                { context.testRight(it).map { value } },
            )

    private fun C.testRight(value: R): ValidationResult<E, R> =
        if (test(value))
            Valid(value)
        else
            Invalid(rightHint(value, emptyList()))
}

class EitherValidatorBuilder<C, L, R, E>(
    private val leftHint: HintBuilder<C, L, E>,
    private val test: C.(R) -> Boolean,
    private val rightHint: HintBuilder<C, R, E>,
) : ValidatorBuilder<C, Either<L, R>, E> {
    override fun build(): Validator<C, Either<L, R>, E> = EitherValidator(leftHint, test, rightHint)
}

fun <C, T, E, L, R> Specification<C, T, E>.rightOrElse(
    mapToEither: C.(T) -> Either<L, R>,
    leftHint: HintBuilder<C, L, E>,
    test: C.(R) -> Boolean,
    rightHint: HintBuilder<C, R, E>,
) =
    MappedContextValueBuilder(EitherValidatorBuilder(leftHint, test, rightHint), mapToEither)
        .also(::add)
