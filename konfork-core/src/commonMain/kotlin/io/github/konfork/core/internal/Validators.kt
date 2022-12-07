package io.github.konfork.core.internal

import io.github.konfork.core.*

internal class MappedContextValidator<C, S, T, E>(
    private val validator: Validator<S, T, E>,
    private val mapContext: (C) -> S,
) : Validator<C, T, E> {
    override fun validate(context: C, value: T): ValidationResult<E, T> =
        validator(mapContext(context), value)
}

internal class MappedValueValidator<C, T, V, E>(
    private val validator: Validator<C, V, E>,
    private val mapValue: (T) -> V,
) : Validator<C, T, E> {
    override fun validate(context: C, value: T): ValidationResult<E, T> =
        validator(context, mapValue(value))
            .map { value }
}

internal class MappedKeyValidator<C, T, E>(
    private val validator: Validator<C, T, E>,
    private val keyMapFn: (String) -> String,
) : Validator<C, T, E> {
    override fun validate(context: C, value: T): ValidationResult<E, T> =
        validator(context, value)
            .mapErrorKey { keyMapFn(it) }
}

internal class RequiredValidator<C, T: Any, E>(
    private val requiredValidator: Validator<C, T?, E>,
    private val subValidator: Validator<C, T, E>,
) : Validator<C, T?, E> {
    override fun validate(context: C, value: T?): ValidationResult<E, T?> {
        return requiredValidator.validate(context, value)
            .flatMap {
                subValidator(context, it!!)
            }
    }
}

internal open class OnEachValidator<C, T, E>(
    private val validator: Validator<C, T, E>,
    private val keyTransform: (T, Int, String) -> String,
) : Validator<C, Iterable<T>, E> {
    override fun validate(context: C, value: Iterable<T>): ValidationResult<E, Iterable<T>> =
        value.foldIndexed(Valid(value)) { index, acc: ValidationResult<E, Iterable<T>>, propertyValue ->
            val result = validator(context, propertyValue, value) {
                keyTransform(propertyValue, index, it)
            }
            acc.combineWith(result)
        }
}

internal open class ConditionalValidator<C, T, E>(
    private val cond: C.(T) -> Boolean,
    private val validator: Validator<C, T, E>,
) : Validator<C, T, E> {
    override fun validate(context: C, value: T): ValidationResult<E, T> =
        if (context.cond(value)) {
            validator(context, value)
        } else {
            Valid(value)
        }
}

internal data class ConstraintValidator<C, T, E>(
    private val hint: HintBuilder<C, T, E>,
    private val arguments: HintArguments,
    private val test: (C, T) -> Boolean,
) : Validator<C, T, E> {
    override fun validate(context: C, value: T): ValidationResult<E, T> =
        if (test(context, value)) {
            Valid(value)
        } else {
            Invalid(context.hint(value, arguments))
        }
}

internal class LazyValidatorNode<C, T, E>(
    private val subValidators: List<Validator<C, T, E>>
) : Validator<C, T, E> {
    override fun validate(context: C, value: T): ValidationResult<E, T> =
        subValidators
            .asSequence()
            .map { it.validate(context, value) }
            .filterIsInstance<Invalid<E>>()
            .firstOrNull() ?: Valid(value)
}

internal class EagerValidatorNode<C, T, E>(
    private val subValidators: List<Validator<C, T, E>>
) : Validator<C, T, E> {
    override fun validate(context: C, value: T): ValidationResult<E, T> =
        subValidators.fold(Valid(value)) { acc: ValidationResult<E, T>, validator ->
            val result = validator.validate(context, value)
            acc.combineWith(result)
        }
}

private operator fun <C, T, E, S> Validator<C, T, E>.invoke(
    context: C,
    value: T,
    parent: S,
    keyTransform: (String) -> String,
): ValidationResult<E, S> =
    this(context, value)
        .map { parent }
        .mapErrorKey(keyTransform)
