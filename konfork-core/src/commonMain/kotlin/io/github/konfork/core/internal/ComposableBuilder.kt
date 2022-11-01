package io.github.konfork.core.internal

import io.github.konfork.core.*

internal interface ComposableBuilder<C, T, E> {
    fun build(): Validation<C, T, E>
}

internal class MappedValidationBuilder<C, T, V, E>(
    private val subBuilder: ComposableBuilder<C, V, E>,
    private val name: String,
    private val mapFn: (T) -> V,
) : ComposableBuilder<C, T, E> {
    override fun build(): Validation<C, T, E> = MappedValidation(subBuilder.build(), name, mapFn)
}

internal class IterableValidationBuilder<C, T, E>(
    private val subBuilder: ComposableBuilder<C, T, E>,
) : ComposableBuilder<C, Iterable<T>, E> {
    override fun build(): Validation<C, Iterable<T>, E> = IterableValidation(subBuilder.build())
}

internal class ArrayValidationBuilder<C, T, E>(
    private val subBuilder: ComposableBuilder<C, T, E>,
) : ComposableBuilder<C, Array<T>, E> {
    override fun build(): Validation<C, Array<T>, E> = ArrayValidation(subBuilder.build())
}

internal class MapValidationBuilder<C, K, V, E>(
    private val subBuilder: ComposableBuilder<C, Map.Entry<K, V>, E>,
) : ComposableBuilder<C, Map<K, V>, E> {
    override fun build(): Validation<C, Map<K, V>, E> = MapValidation(subBuilder.build())
}

internal class OptionalValidationBuilder<C, T : Any, E>(
    private val subBuilder: ComposableBuilder<C, T, E>,
) : ComposableBuilder<C, T?, E> {
    override fun build(): Validation<C, T?, E> = OptionalValidation(subBuilder.build())
}

internal class RequiredValidationBuilder<C, T : Any, E>(
    hint: HintBuilder<C, T?, E>,
    private val subBuilder: ComposableBuilder<C, T, E>,
) : ComposableBuilder<C, T?, E> {
    val constraintBuilder: ConstraintValidationBuilder<C, T?, E> =
        ConstraintValidationBuilder(hint, emptyList()) { _, value -> value != null }
    override fun build(): Validation<C, T?, E> =
        RequiredValidation(
            constraintBuilder.build(),
            subBuilder.build(),
        )
}

internal class PrebuildValidationBuilder<C, T, S, E>(
    private val validation: Validation<S, T, E>,
    private val mapFn: (C) -> S,
) : ComposableBuilder<C, T, E> {
    override fun build(): Validation<C, T, E> = MappedContextValidation(validation, mapFn)
}

internal class ConstraintValidationBuilder<C, T, E>(
    private var hint: HintBuilder<C, T, E>,
    private val arguments: HintArguments,
    private val test: (C, T) -> Boolean,
) : ComposableBuilder<C, T, E>, ConstraintBuilder<C, T, E> {
    override fun build(): Validation<C, T, E> = ConstraintValidation(hint, arguments, test)
    override infix fun hint(hint: HintBuilder<C, T, E>): ConstraintValidationBuilder<C, T, E> {
        this.hint = hint
        return this
    }
}
