package io.github.konfork.core.internal

import io.github.konfork.core.*
import kotlin.collections.Map.Entry
import kotlin.reflect.KFunction1
import kotlin.reflect.KProperty1

internal class ValidationNodeBuilder<C, T, E> : ValidationBuilder<C, T, E>(), ComposableBuilder<C, T, E> {
    private val subBuilders = mutableListOf<ComposableBuilder<C, T, E>>()

    override fun build(): Validation<C, T, E> =
        ValidationNode(subBuilders.map(ComposableBuilder<C, T, E>::build))

    override fun addConstraint(hint: HintBuilder<C, T, E>, vararg values: Any, test: (C, T) -> Boolean): ConstraintBuilder<C, T, E> =
        ConstraintValidationBuilder(hint, values.toList(), test).also(::add)

    override fun <R> KProperty1<T, R>.invoke(init: ValidationBuilder<C, R, E>.() -> Unit) =
        add(PropertyValidationBuilder(createBuilder(init), name, this))

    override fun <R> KFunction1<T, R>.invoke(init: ValidationBuilder<C, R, E>.() -> Unit) =
        add(PropertyValidationBuilder(createBuilder(init), name, this))

    override fun <R> onEachIterable(name: String, mapFn: (T) -> Iterable<R>, init: ValidationBuilder<C, R, E>.() -> Unit) =
        add(PropertyValidationBuilder(IterableValidationBuilder(createBuilder(init)), name, mapFn))

    override fun <R> onEachArray(name: String, mapFn: (T) -> Array<R>, init: ValidationBuilder<C, R, E>.() -> Unit) =
        add(PropertyValidationBuilder(ArrayValidationBuilder(createBuilder(init)), name, mapFn))

    override fun <K, V> onEachMap(name: String, mapFn: (T) -> Map<K, V>, init: ValidationBuilder<C, Entry<K, V>, E>.() -> Unit) =
        add(PropertyValidationBuilder(MapValidationBuilder(createBuilder(init)), name, mapFn))

    override fun <K, V> onEachMapValue(name: String, mapFn: (T) -> Map<K, V>, init: ValidationBuilder<C, V, E>.() -> Unit) =
        add(PropertyValidationBuilder(MapValueValidationBuilder(createBuilder(init)), name, mapFn))

    override fun <K, V> onEachMapKey(name: String, mapFn: (T) -> Map<K, V>, init: ValidationBuilder<C, K, E>.() -> Unit) =
        add(PropertyValidationBuilder(MapKeyValidationBuilder(createBuilder(init)), name, mapFn))

    override fun <R : Any> ifPresent(name: String, mapFn: (T) -> R?, init: ValidationBuilder<C, R, E>.() -> Unit) =
        add(PropertyValidationBuilder(OptionalValidationBuilder(createBuilder(init)), name, mapFn))

    override fun <R : Any> required(name: String, hint: HintBuilder<C, R?, E>, mapFn: (T) -> R?, init: ValidationBuilder<C, R, E>.() -> Unit): ConstraintBuilder<C, R?, E> =
        RequiredValidationBuilder(hint, createBuilder(init))
            .also { add(PropertyValidationBuilder(it, name, mapFn)) }
            .constraintBuilder

    override fun <C, R, E> with(hint: HintBuilder<C, R?, E>, init: ValidationBuilder<C, R, E>.() -> Unit): HintedValidationBuilder<C, R, E> =
        HintedValidationBuilder(hint, init)

    override fun <C, R> with(init: ValidationBuilder<C, R, String>.() -> Unit): HintedValidationBuilder<C, R, String> =
        HintedValidationBuilder(stringHint("is required"), init)

    override fun <S> run(validation: Validation<S, T, E>, map: (C) -> S) =
        add(PrebuildValidationBuilder(validation, map))

    override val <R> KProperty1<T, R>.has: ValidationBuilder<C, R, E>
        get() = ValidationNodeBuilder<C, R, E>()
            .also { add(PropertyValidationBuilder(it, this.name,this)) }

    private fun <D, S> createBuilder(init: ValidationBuilder<D, S, E>.() -> Unit) =
        ValidationNodeBuilder<D, S, E>().also(init)

    override fun add(builder: ComposableBuilder<C, T, E>) {
        subBuilders.add(builder)
    }
}

internal fun <A> identity(a: A): A = a
