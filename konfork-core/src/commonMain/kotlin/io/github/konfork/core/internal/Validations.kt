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
    private val name: String,
    private val mapFn: (T) -> V,
) : Validation<C, T, E> {
    override fun validate(context: C, value: T): ValidationResult<E, T> =
        validation(context, mapFn(value), value) { ".$name$it" }
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

internal class IterableValidation<C, T, E>(
    private val validation: Validation<C, T, E>
) : Validation<C, Iterable<T>, E> {
    override fun validate(context: C, value: Iterable<T>): ValidationResult<E, Iterable<T>> =
        value.foldIndexed(Valid(value)) { index, acc: ValidationResult<E, Iterable<T>>, propertyValue ->
            val result = validation(context, propertyValue, value) { "[$index]$it" }
            acc.combineWith(result)
        }
}

internal class ArrayValidation<C, T, E>(
    private val validation: Validation<C, T, E>
) : Validation<C, Array<T>, E> {
    override fun validate(context: C, value: Array<T>): ValidationResult<E, Array<T>> =
        value.foldIndexed(Valid(value)) { index, acc: ValidationResult<E, Array<T>>, propertyValue ->
            val result = validation(context, propertyValue, value) { "[$index]$it" }
            acc.combineWith(result)
        }
}

internal class MapValidation<C, K, V, E>(
    private val validation: Validation<C, Map.Entry<K, V>, E>
) : Validation<C, Map<K, V>, E> {
    override fun validate(context: C, value: Map<K, V>): ValidationResult<E, Map<K, V>> =
        value.asIterable().fold(Valid(value)) { acc: ValidationResult<E, Map<K, V>>, entry ->
            val result = validation(context, entry, value) {
                ".${entry.key.toString()}${it.removePrefix(".value")}"  // TODO: Add onEachKey and onEachValue
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

internal class ValidationNode<C, T, E>(
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
