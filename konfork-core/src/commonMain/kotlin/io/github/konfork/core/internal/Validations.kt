package io.github.konfork.core.internal

import io.github.konfork.core.*

internal class MappedContextValidation<C, S, T, E>(
    private val validation: Validation<S, T, E>,
    private val map: (C) -> S,
) : Validation<C, T, E> {
    override fun validate(context: C, value: T): ValidationResult<E, T> =
        validation(map(context), value)
}

internal class MappedValidation<C, T, V, E>(
    private val validation: Validation<C, V, E>,
    private val mapFn: (T) -> V,
    private val keyMapFn: (String) -> String,
) : Validation<C, T, E> {
    override fun validate(context: C, value: T): ValidationResult<E, T> =
        validation(context, mapFn(value), value, keyMapFn)
}

internal class OptionalValidation<C, T : Any, E>(
    private val validation: Validation<C, T, E>
) : Validation<C, T?, E> {
    override fun validate(context: C, value: T?): ValidationResult<E, T?> {
        val nonNullValue = value ?: return Valid(value)
        return validation(context, nonNullValue)
    }
}

internal class RequiredValidation<C, T: Any, E>(
    private val requiredValidation: Validation<C, T?, E>,
    private val subValidation: Validation<C, T, E>,
) : Validation<C, T?, E> {
    override fun validate(context: C, value: T?): ValidationResult<E, T?> {
        return requiredValidation.validate(context, value)
            .flatMap {
                subValidation(context, it!!)
            }
    }
}

internal open class OnEachValidation<C, T, E>(
    private val validation: Validation<C, T, E>,
    private val keyTransform: (T, Int, String) -> String,
) : Validation<C, Iterable<T>, E> {
    override fun validate(context: C, value: Iterable<T>): ValidationResult<E, Iterable<T>> =
        value.foldIndexed(Valid(value)) { index, acc: ValidationResult<E, Iterable<T>>, propertyValue ->
            val result = validation(context, propertyValue, value) {
                keyTransform(propertyValue, index, it)
            }
            acc.combineWith(result)
        }
}

internal data class ConstraintValidation<C, T, E>(
    private val hint: HintBuilder<C, T, E>,
    private val arguments: HintArguments,
    private val test: (C, T) -> Boolean,
) : Validation<C, T, E> {
    override fun validate(context: C, value: T): ValidationResult<E, T> =
        if (test(context, value)) {
            Valid(value)
        } else {
            Invalid(context.hint(value, arguments))
        }
}

internal class LazyValidationNode<C, T, E>(
    private val subValidations: List<Validation<C, T, E>>
) : Validation<C, T, E> {
    override fun validate(context: C, value: T): ValidationResult<E, T> =
        subValidations
            .asSequence()
            .map { it.validate(context, value) }
            .filterIsInstance<Invalid<E>>()
            .firstOrNull() ?: Valid(value)
}

internal class EagerValidationNode<C, T, E>(
    private val subValidations: List<Validation<C, T, E>>
) : Validation<C, T, E> {
    override fun validate(context: C, value: T): ValidationResult<E, T> =
        subValidations.fold(Valid(value)) { acc: ValidationResult<E, T>, validation ->
            val result = validation.validate(context, value)
            acc.combineWith(result)
        }
}

private operator fun <C, T, E, S> Validation<C, T, E>.invoke(
    context: C,
    value: T,
    parent: S,
    keyTransform: (String) -> String,
): ValidationResult<E, S> =
    this(context, value)
        .map { parent }
        .mapErrorKey(keyTransform)
