package io.github.konfork.core.internal

import io.github.konfork.core.*

internal interface ValidatorBuilder<C, T, E> {
    fun build(): Validator<C, T, E>
}

internal class PropertyValidatorBuilder<C, T, V, E>(
    private val subBuilder: ValidatorBuilder<C, V, E>,
    private val name: String,
    private val mapFn: (T) -> V,
) : ValidatorBuilder<C, T, E> {
    override fun build(): Validator<C, T, E> =
        MappedValidator(subBuilder.build(), mapFn) { ".$name$it" }
}

internal class LazyValidatorNodeBuilder<C, T, E>(
    private val subBuilder: ValidatorsBuilder<C, T, E>,
) : ValidatorBuilder<C, T, E> {
    override fun build(): Validator<C, T, E> =
        LazyValidatorNode(subBuilder.build())
}

internal class EagerValidatorNodeBuilder<C, T, E>(
    private val subBuilder: ValidatorsBuilder<C, T, E>,
) : ValidatorBuilder<C, T, E> {
    override fun build(): Validator<C, T, E> =
        EagerValidatorNode(subBuilder.build())
}

internal class IterableValidatorBuilder<C, T, E>(
    private val subBuilder: ValidatorBuilder<C, T, E>,
) : ValidatorBuilder<C, Iterable<T>, E> {
    override fun build(): Validator<C, Iterable<T>, E> =
        OnEachValidator(subBuilder.build()) { _, index, name -> "[$index]$name" }
}

internal class ArrayValidatorBuilder<C, T, E>(
    private val subBuilder: ValidatorBuilder<C, T, E>,
) : ValidatorBuilder<C, Array<T>, E> {
    override fun build(): Validator<C, Array<T>, E> =
        MappedValidator(
            OnEachValidator(subBuilder.build()) { _, index, name -> "[$index]$name" },
            Array<T>::toList,
            ::identity,
        )
}

internal class MapValidatorBuilder<C, K, V, E>(
    private val subBuilder: ValidatorBuilder<C, Map.Entry<K, V>, E>,
) : ValidatorBuilder<C, Map<K, V>, E> {
    override fun build(): Validator<C, Map<K, V>, E> =
        MappedValidator(
            OnEachValidator(subBuilder.build()) { entry, _, name ->
                ".${entry.key.toString()}${name.removePrefix(".value")}"
            },
            Map<K, V>::entries,
            ::identity,
        )
}

internal class MapValueValidatorBuilder<C, K, V, E>(
    private val subBuilder: ValidatorBuilder<C, V, E>,
) : ValidatorBuilder<C, Map<K, V>, E> {
    override fun build(): Validator<C, Map<K, V>, E> =
        MappedValidator(
            OnEachValidator(
                MappedValidator(subBuilder.build(), Map.Entry<K, V>::value, ::identity)
            ) { entry, _, name ->
                ".${entry.key.toString()}$name"
            },
            Map<K, V>::entries,
            ::identity,
        )
}

internal class MapKeyValidatorBuilder<C, K, V, E>(
    private val subBuilder: ValidatorBuilder<C, K, E>,
) : ValidatorBuilder<C, Map<K, V>, E> {
    override fun build(): Validator<C, Map<K, V>, E> =
        MappedValidator(
            OnEachValidator(
                MappedValidator(subBuilder.build(), Map.Entry<K, V>::key) { "#key$it" }
            ) { entry, _, name ->
                ".${entry.key.toString()}$name"
            },
            Map<K, V>::entries,
            ::identity,
        )
}

internal class OptionalValidatorBuilder<C, T : Any, E>(
    private val subBuilder: ValidatorBuilder<C, T, E>,
) : ValidatorBuilder<C, T?, E> {
    override fun build(): Validator<C, T?, E> = OptionalValidator(subBuilder.build())
}

internal class RequiredValidatorBuilder<C, T : Any, E>(
    hint: HintBuilder<C, T?, E>,
    private val subBuilder: ValidatorBuilder<C, T, E>,
) : ValidatorBuilder<C, T?, E> {
    val constraintBuilder: ConstraintValidatorBuilder<C, T?, E> =
        ConstraintValidatorBuilder(hint, emptyList()) { _, value -> value != null }
    override fun build(): Validator<C, T?, E> =
        RequiredValidator(
            constraintBuilder.build(),
            subBuilder.build(),
        )
}

internal class PrebuildValidatorBuilder<C, T, S, E>(
    private val validator: Validator<S, T, E>,
    private val mapFn: (C) -> S,
) : ValidatorBuilder<C, T, E> {
    override fun build(): Validator<C, T, E> = MappedContextValidator(validator, mapFn)
}

internal class ConstraintValidatorBuilder<C, T, E>(
    private var hint: HintBuilder<C, T, E>,
    private val arguments: HintArguments,
    private val test: (C, T) -> Boolean,
) : ValidatorBuilder<C, T, E>, ConstraintBuilder<C, T, E> {
    override fun build(): Validator<C, T, E> = ConstraintValidator(hint, arguments, test)
    override infix fun hint(hint: HintBuilder<C, T, E>): ConstraintValidatorBuilder<C, T, E> {
        this.hint = hint
        return this
    }
}

internal class ConditionalValidatorBuilder<C, T, E>(
    private val cond: C.(T) -> Boolean,
    private val subBuilder: ValidatorBuilder<C, T, E>,
): ValidatorBuilder<C, T, E> {
    override fun build(): Validator<C, T, E> = ConditionalValidator(cond, subBuilder.build())
}
