package io.github.konfork.core.internal

import io.github.konfork.core.*

internal interface ComposableBuilder<C, T, E> {
    fun build(): Validator<C, T, E>
}

internal class PropertyValidationBuilder<C, T, V, E>(
    private val subBuilder: ComposableBuilder<C, V, E>,
    private val name: String,
    private val mapFn: (T) -> V,
) : ComposableBuilder<C, T, E> {
    override fun build(): Validator<C, T, E> =
        MappedValidator(subBuilder.build(), mapFn) { ".$name$it" }
}

internal class LazyValidationNodeBuilder<C, T, E>(
    private val subBuilder: NodeValidationsBuilder<C, T, E>,
) : ComposableBuilder<C, T, E> {
    override fun build(): Validator<C, T, E> =
        LazyValidatorNode(subBuilder.build())
}

internal class EagerValidationNodeBuilder<C, T, E>(
    private val subBuilder: NodeValidationsBuilder<C, T, E>,
) : ComposableBuilder<C, T, E> {
    override fun build(): Validator<C, T, E> =
        EagerValidatorNode(subBuilder.build())
}

internal class IterableValidationBuilder<C, T, E>(
    private val subBuilder: ComposableBuilder<C, T, E>,
) : ComposableBuilder<C, Iterable<T>, E> {
    override fun build(): Validator<C, Iterable<T>, E> =
        OnEachValidator(subBuilder.build()) { _, index, name -> "[$index]$name" }
}

internal class ArrayValidationBuilder<C, T, E>(
    private val subBuilder: ComposableBuilder<C, T, E>,
) : ComposableBuilder<C, Array<T>, E> {
    override fun build(): Validator<C, Array<T>, E> =
        MappedValidator(
            OnEachValidator(subBuilder.build()) { _, index, name -> "[$index]$name" },
            Array<T>::toList,
            ::identity,
        )
}

internal class MapValidationBuilder<C, K, V, E>(
    private val subBuilder: ComposableBuilder<C, Map.Entry<K, V>, E>,
) : ComposableBuilder<C, Map<K, V>, E> {
    override fun build(): Validator<C, Map<K, V>, E> =
        MappedValidator(
            OnEachValidator(subBuilder.build()) { entry, _, name ->
                ".${entry.key.toString()}${name.removePrefix(".value")}"
            },
            Map<K, V>::entries,
            ::identity,
        )
}

internal class MapValueValidationBuilder<C, K, V, E>(
    private val subBuilder: ComposableBuilder<C, V, E>,
) : ComposableBuilder<C, Map<K, V>, E> {
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

internal class MapKeyValidationBuilder<C, K, V, E>(
    private val subBuilder: ComposableBuilder<C, K, E>,
) : ComposableBuilder<C, Map<K, V>, E> {
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

internal class OptionalValidationBuilder<C, T : Any, E>(
    private val subBuilder: ComposableBuilder<C, T, E>,
) : ComposableBuilder<C, T?, E> {
    override fun build(): Validator<C, T?, E> = OptionalValidator(subBuilder.build())
}

internal class RequiredValidationBuilder<C, T : Any, E>(
    hint: HintBuilder<C, T?, E>,
    private val subBuilder: ComposableBuilder<C, T, E>,
) : ComposableBuilder<C, T?, E> {
    val constraintBuilder: ConstraintValidationBuilder<C, T?, E> =
        ConstraintValidationBuilder(hint, emptyList()) { _, value -> value != null }
    override fun build(): Validator<C, T?, E> =
        RequiredValidator(
            constraintBuilder.build(),
            subBuilder.build(),
        )
}

internal class PrebuildValidationBuilder<C, T, S, E>(
    private val validator: Validator<S, T, E>,
    private val mapFn: (C) -> S,
) : ComposableBuilder<C, T, E> {
    override fun build(): Validator<C, T, E> = MappedContextValidator(validator, mapFn)
}

internal class ConstraintValidationBuilder<C, T, E>(
    private var hint: HintBuilder<C, T, E>,
    private val arguments: HintArguments,
    private val test: (C, T) -> Boolean,
) : ComposableBuilder<C, T, E>, ConstraintBuilder<C, T, E> {
    override fun build(): Validator<C, T, E> = ConstraintValidator(hint, arguments, test)
    override infix fun hint(hint: HintBuilder<C, T, E>): ConstraintValidationBuilder<C, T, E> {
        this.hint = hint
        return this
    }
}
