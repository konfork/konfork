package io.github.konfork.core.internal

import io.github.konfork.core.*
import kotlin.collections.Map.Entry

internal class ValidatorsBuilder<C, T, E> : Specification<C, T, E>() {
    private val subBuilders = mutableListOf<ValidatorBuilder<C, T, E>>()

    fun build(): List<Validator<C, T, E>> =
        subBuilders.map(ValidatorBuilder<C, T, E>::build)

    override fun addConstraint(hint: HintBuilder<C, T, E>, vararg values: Any, test: (C, T) -> Boolean): ConstraintBuilder<C, T, E> =
        ConstraintValidatorBuilder(hint, values.toList(), test).also(::add)

    override fun <R> property(name: String, mapFn: (T) -> R, init: Specification<C, R, E>.() -> Unit) =
        add(PropertyValidatorBuilder(eagerBuilder(init), name, mapFn))

    override fun eager(init: Specification<C, T, E>.() -> Unit) =
        add(eagerBuilder(init))

    override fun <R> lazy(name: String, mapFn: (T) -> R, init: Specification<C, R, E>.() -> Unit) =
        add(PropertyValidatorBuilder(lazyBuilder(init), name, mapFn))

    override fun lazy(init: Specification<C, T, E>.() -> Unit) =
        add(lazyBuilder(init))

    override fun <R> onEachIterable(name: String, mapFn: (T) -> Iterable<R>, init: Specification<C, R, E>.() -> Unit) =
        add(PropertyValidatorBuilder(IterableValidatorBuilder(eagerBuilder(init)), name, mapFn))

    override fun <R> onEachArray(name: String, mapFn: (T) -> Array<R>, init: Specification<C, R, E>.() -> Unit) =
        add(PropertyValidatorBuilder(ArrayValidatorBuilder(eagerBuilder(init)), name, mapFn))

    override fun <K, V> onEachMap(name: String, mapFn: (T) -> Map<K, V>, init: Specification<C, Entry<K, V>, E>.() -> Unit) =
        add(PropertyValidatorBuilder(MapValidatorBuilder(eagerBuilder(init)), name, mapFn))

    override fun <K, V> onEachMapValue(name: String, mapFn: (T) -> Map<K, V>, init: Specification<C, V, E>.() -> Unit) =
        add(PropertyValidatorBuilder(MapValueValidatorBuilder(eagerBuilder(init)), name, mapFn))

    override fun <K, V> onEachMapKey(name: String, mapFn: (T) -> Map<K, V>, init: Specification<C, K, E>.() -> Unit) =
        add(PropertyValidatorBuilder(MapKeyValidatorBuilder(eagerBuilder(init)), name, mapFn))

    override fun <R : Any> ifPresent(name: String, mapFn: (T) -> R?, init: Specification<C, R, E>.() -> Unit) =
        add(PropertyValidatorBuilder(OptionalValidatorBuilder(eagerBuilder(init)), name, mapFn))

    override fun <R : Any> required(name: String, hint: HintBuilder<C, R?, E>, mapFn: (T) -> R?, init: Specification<C, R, E>.() -> Unit): ConstraintBuilder<C, R?, E> =
        RequiredValidatorBuilder(hint, eagerBuilder(init))
            .also { add(PropertyValidatorBuilder(it, name, mapFn)) }
            .constraintBuilder

    override fun <C, R, E> with(hint: HintBuilder<C, R?, E>, init: Specification<C, R, E>.() -> Unit): HintedSpecification<C, R, E> =
        HintedSpecification(hint, init)

    override fun <C, R> with(init: Specification<C, R, String>.() -> Unit): HintedSpecification<C, R, String> =
        HintedSpecification(stringHint("is required"), init)

    override fun <R, S> apply(name: String, validator: Validator<S, R, E>, mapFn: (T) -> R, mapContext: (C) -> S) =
        add(PropertyValidatorBuilder(PrebuildValidatorBuilder(validator, mapContext), name, mapFn))

    override fun <S> apply(validator: Validator<S, T, E>, mapContext: (C) -> S) =
        add(PrebuildValidatorBuilder(validator, mapContext))

    override fun <R> has(name: String, mapFn: (T) -> R): Specification<C, R, E> =
        ValidatorsBuilder<C, R, E>()
            .also { add(PropertyValidatorBuilder(EagerValidatorNodeBuilder(it), name, mapFn)) }

    private fun <D, S> eagerBuilder(init: Specification<D, S, E>.() -> Unit) =
        EagerValidatorNodeBuilder(ValidatorsBuilder<D, S, E>().also(init))

    private fun <D, S> lazyBuilder(init: Specification<D, S, E>.() -> Unit) =
        LazyValidatorNodeBuilder(ValidatorsBuilder<D, S, E>().also(init))

    override fun add(builder: ValidatorBuilder<C, T, E>) {
        subBuilders.add(builder)
    }
}

internal fun <A> identity(a: A): A = a
