package io.github.konfork.core.internal

import io.github.konfork.core.*
import kotlin.collections.Map.Entry

internal class ValidatorsBuilder<C, T, E> : Specification<C, T, E>() {
    private val subBuilders = mutableListOf<ComposableBuilder<C, T, E>>()

    fun build(): List<Validator<C, T, E>> =
        subBuilders.map(ComposableBuilder<C, T, E>::build)

    override fun addConstraint(hint: HintBuilder<C, T, E>, vararg values: Any, test: (C, T) -> Boolean): ConstraintBuilder<C, T, E> =
        ConstraintValidationBuilder(hint, values.toList(), test).also(::add)

    override fun <R> property(name: String, mapFn: (T) -> R, init: Specification<C, R, E>.() -> Unit) =
        add(PropertyValidationBuilder(eagerBuilder(init), name, mapFn))

    override fun eager(init: Specification<C, T, E>.() -> Unit) =
        add(eagerBuilder(init))

    override fun <R> lazy(name: String, mapFn: (T) -> R, init: Specification<C, R, E>.() -> Unit) =
        add(PropertyValidationBuilder(lazyBuilder(init), name, mapFn))

    override fun lazy(init: Specification<C, T, E>.() -> Unit) =
        add(lazyBuilder(init))

    override fun <R> onEachIterable(name: String, mapFn: (T) -> Iterable<R>, init: Specification<C, R, E>.() -> Unit) =
        add(PropertyValidationBuilder(IterableValidationBuilder(eagerBuilder(init)), name, mapFn))

    override fun <R> onEachArray(name: String, mapFn: (T) -> Array<R>, init: Specification<C, R, E>.() -> Unit) =
        add(PropertyValidationBuilder(ArrayValidationBuilder(eagerBuilder(init)), name, mapFn))

    override fun <K, V> onEachMap(name: String, mapFn: (T) -> Map<K, V>, init: Specification<C, Entry<K, V>, E>.() -> Unit) =
        add(PropertyValidationBuilder(MapValidationBuilder(eagerBuilder(init)), name, mapFn))

    override fun <K, V> onEachMapValue(name: String, mapFn: (T) -> Map<K, V>, init: Specification<C, V, E>.() -> Unit) =
        add(PropertyValidationBuilder(MapValueValidationBuilder(eagerBuilder(init)), name, mapFn))

    override fun <K, V> onEachMapKey(name: String, mapFn: (T) -> Map<K, V>, init: Specification<C, K, E>.() -> Unit) =
        add(PropertyValidationBuilder(MapKeyValidationBuilder(eagerBuilder(init)), name, mapFn))

    override fun <R : Any> ifPresent(name: String, mapFn: (T) -> R?, init: Specification<C, R, E>.() -> Unit) =
        add(PropertyValidationBuilder(OptionalValidationBuilder(eagerBuilder(init)), name, mapFn))

    override fun <R : Any> required(name: String, hint: HintBuilder<C, R?, E>, mapFn: (T) -> R?, init: Specification<C, R, E>.() -> Unit): ConstraintBuilder<C, R?, E> =
        RequiredValidationBuilder(hint, eagerBuilder(init))
            .also { add(PropertyValidationBuilder(it, name, mapFn)) }
            .constraintBuilder

    override fun <C, R, E> with(hint: HintBuilder<C, R?, E>, init: Specification<C, R, E>.() -> Unit): HintedSpecification<C, R, E> =
        HintedSpecification(hint, init)

    override fun <C, R> with(init: Specification<C, R, String>.() -> Unit): HintedSpecification<C, R, String> =
        HintedSpecification(stringHint("is required"), init)

    override fun <S> run(validator: Validator<S, T, E>, map: (C) -> S) =
        add(PrebuildValidationBuilder(validator, map))

    override fun <R> has(name: String, mapFn: (T) -> R): Specification<C, R, E> =
        ValidatorsBuilder<C, R, E>()
            .also { add(PropertyValidationBuilder(EagerValidationNodeBuilder(it), name, mapFn)) }

    private fun <D, S> eagerBuilder(init: Specification<D, S, E>.() -> Unit) =
        EagerValidationNodeBuilder(ValidatorsBuilder<D, S, E>().also(init))

    private fun <D, S> lazyBuilder(init: Specification<D, S, E>.() -> Unit) =
        LazyValidationNodeBuilder(ValidatorsBuilder<D, S, E>().also(init))

    override fun add(builder: ComposableBuilder<C, T, E>) {
        subBuilders.add(builder)
    }
}

internal fun <A> identity(a: A): A = a
