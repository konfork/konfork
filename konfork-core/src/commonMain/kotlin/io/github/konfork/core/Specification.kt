package io.github.konfork.core

import io.github.konfork.core.internal.*
import io.github.konfork.core.internal.ValidatorBuilder
import io.github.konfork.core.internal.ValidatorsBuilder
import kotlin.jvm.JvmName
import kotlin.reflect.KFunction1
import kotlin.reflect.KProperty1

@DslMarker
private annotation class SpecificationScope

@SpecificationScope
abstract class Specification<C, T, E> {
    abstract fun addConstraint(hint: HintBuilder<C, T, E>, vararg values: Any, test: C.(T) -> Boolean): ConstraintBuilder<C, T, E>

    internal abstract fun <R> property(name: String, mapFn: (T) -> R, init: Specification<C, R, E>.() -> Unit)
    operator fun <R> KProperty1<T, R>.invoke(init: Specification<C, R, E>.() -> Unit) = property(name, this, init)
    operator fun <R> KFunction1<T, R>.invoke(init: Specification<C, R, E>.() -> Unit) = property(name, this, init)

    infix fun <R> KProperty1<T, R>.eager(init: Specification<C, R, E>.() -> Unit) = this(init)
    infix fun <R> KFunction1<T, R>.eager(init: Specification<C, R, E>.() -> Unit) = this(init)
    abstract fun eager(init: Specification<C, T, E>.() -> Unit)

    internal abstract fun <R> lazy(name: String, mapFn: (T) -> R, init: Specification<C, R, E>.() -> Unit)
    infix fun <R> KProperty1<T, R>.lazy(init: Specification<C, R, E>.() -> Unit) = lazy(name, this, init)
    infix fun <R> KFunction1<T, R>.lazy(init: Specification<C, R, E>.() -> Unit) = lazy(name, this, init)
    abstract fun lazy(init: Specification<C, T, E>.() -> Unit)

    internal abstract fun <R> onEachIterable(name: String, mapFn: (T) -> Iterable<R>, init: Specification<C, R, E>.() -> Unit)
    @JvmName("onEachIterable")
    infix fun <R> KProperty1<T, Iterable<R>>.onEach(init: Specification<C, R, E>.() -> Unit) = onEachIterable(this.name, this, init)
    @JvmName("onEachIterable")
    infix fun <R> KFunction1<T, Iterable<R>>.onEach(init: Specification<C, R, E>.() -> Unit) = onEachIterable(this.name, this, init)

    internal abstract fun <R> onEachArray(name: String, mapFn: (T) -> Array<R>, init: Specification<C, R, E>.() -> Unit)
    @JvmName("onEachArray")
    infix fun <R> KProperty1<T, Array<R>>.onEach(init: Specification<C, R, E>.() -> Unit) = onEachArray(this.name, this, init)
    @JvmName("onEachArray")
    infix fun <R> KFunction1<T, Array<R>>.onEach(init: Specification<C, R, E>.() -> Unit) = onEachArray(this.name, this, init)

    internal abstract fun <K, V> onEachMap(name: String, mapFn: (T) -> Map<K, V>, init: Specification<C, Map.Entry<K, V>, E>.() -> Unit)
    @JvmName("onEachMap")
    infix fun <K, V> KProperty1<T, Map<K, V>>.onEach(init: Specification<C, Map.Entry<K, V>, E>.() -> Unit) = onEachMap(this.name, this, init)
    @JvmName("onEachMap")
    infix fun <K, V> KFunction1<T, Map<K, V>>.onEach(init: Specification<C, Map.Entry<K, V>, E>.() -> Unit) = onEachMap(this.name, this, init)

    internal abstract fun <K, V> onEachMapValue(name: String, mapFn: (T) -> Map<K, V>, init: Specification<C, V, E>.() -> Unit)
    @JvmName("onEachMapValue")
    infix fun <K, V> KProperty1<T, Map<K, V>>.onEachValue(init: Specification<C, V, E>.() -> Unit) = onEachMapValue(this.name, this, init)
    @JvmName("onEachMapValue")
    infix fun <K, V> KFunction1<T, Map<K, V>>.onEachValue(init: Specification<C, V, E>.() -> Unit) = onEachMapValue(this.name, this, init)

    internal abstract fun <K, V> onEachMapKey(name: String, mapFn: (T) -> Map<K, V>, init: Specification<C, K, E>.() -> Unit)
    @JvmName("onEachMapKey")
    infix fun <K, V> KProperty1<T, Map<K, V>>.onEachKey(init: Specification<C, K, E>.() -> Unit) = onEachMapKey(this.name, this, init)
    @JvmName("onEachMapKey")
    infix fun <K, V> KFunction1<T, Map<K, V>>.onEachKey(init: Specification<C, K, E>.() -> Unit) = onEachMapKey(this.name, this, init)

    internal abstract fun <R : Any> ifPresent(name: String, mapFn: (T) -> R?, init: Specification<C, R, E>.() -> Unit)
    infix fun <R : Any> KProperty1<T, R?>.ifPresent(init: Specification<C, R, E>.() -> Unit) = ifPresent(this.name, this, init)
    infix fun <R : Any> KFunction1<T, R?>.ifPresent(init: Specification<C, R, E>.() -> Unit) = ifPresent(this.name, this, init)

    internal abstract fun <R : Any> required(name: String, hint: HintBuilder<C, R?, E>, mapFn: (T) -> R?, init: Specification<C, R, E>.() -> Unit): ConstraintBuilder<C, R?, E>
    infix fun <R : Any> KProperty1<T, R?>.required(hintedBuilder: HintedSpecification<C, R, E>): ConstraintBuilder<C, R?, E> =
        required(this.name, hintedBuilder.hint, this, hintedBuilder.init)
    infix fun <R : Any> KFunction1<T, R?>.required(hintedBuilder: HintedSpecification<C, R, E>): ConstraintBuilder<C, R?, E> =
        required(this.name, hintedBuilder.hint, this, hintedBuilder.init)

    abstract fun <C, R, E> with(hint: HintBuilder<C, R?, E>, init: Specification<C, R, E>.() -> Unit): HintedSpecification<C, R, E>
    abstract fun <C, R> with(init: Specification<C, R, String>.() -> Unit): HintedSpecification<C, R, String>

    abstract fun <S> run(validator: Validator<S, T, E>, map: (C) -> S)
    fun run(validator: Validator<C, T, E>) = run(validator, ::identity)

    internal abstract fun add(builder: ValidatorBuilder<C, T, E>)

    val <R> KProperty1<T, R>.has: Specification<C, R, E> get() = has(this.name, this)
    val <R> KFunction1<T, R>.has: Specification<C, R, E> get() = has(this.name, this)
    internal abstract fun <R> has(name: String, mapFn: (T) -> R): Specification<C, R, E>
}

fun <C, T> Specification<C, T, String>.addConstraint(hint: String, vararg values: Any, test: C.(T) -> Boolean): ConstraintBuilder<C, T, String> =
    addConstraint(stringHint(hint), *values) { test(it) }

fun <C, T : Any, E> Specification<C, T?, E>.ifPresent(init: Specification<C, T, E>.() -> Unit) =
    add(OptionalValidatorBuilder(eagerBuilder(init)))

fun <C, T : Any, E> Specification<C, T?, E>.required(hint: HintBuilder<C, T?, E>, init: Specification<C, T, E>.() -> Unit): ConstraintBuilder<C, T?, E> =
    RequiredValidatorBuilder(hint, eagerBuilder(init))
        .also(::add)
        .constraintBuilder

@JvmName("onEachIterable")
@Suppress("UNCHECKED_CAST")
fun <C, S, T : Iterable<S>, E> Specification<C, T, E>.onEach(init: Specification<C, S, E>.() -> Unit) =
    add(IterableValidatorBuilder(eagerBuilder(init)) as ValidatorBuilder<C, T, E>)

@JvmName("onEachArray")
fun <C, T, E> Specification<C, Array<T>, E>.onEach(init: Specification<C, T, E>.() -> Unit) =
    add(ArrayValidatorBuilder(eagerBuilder(init)))

@JvmName("onEachMap")
@Suppress("UNCHECKED_CAST")
fun <C, K, V, T : Map<K, V>, E> Specification<C, T, E>.onEach(init: Specification<C, Map.Entry<K, V>, E>.() -> Unit) =
    add(MapValidatorBuilder(eagerBuilder(init)) as ValidatorBuilder<C, T, E>)

@JvmName("onEachMapValue")
@Suppress("UNCHECKED_CAST")
fun <C, K, V, T : Map<K, V>, E> Specification<C, T, E>.onEachValue(init: Specification<C, V, E>.() -> Unit) =
    add(MapValueValidatorBuilder<C, K, V, E>(eagerBuilder(init)) as ValidatorBuilder<C, T, E>)

@JvmName("onEachMapKey")
@Suppress("UNCHECKED_CAST")
fun <C, K, V, T : Map<K, V>, E> Specification<C, T, E>.onEachKey(init: Specification<C, K, E>.() -> Unit) =
    add(MapKeyValidatorBuilder<C, K, V, E>(eagerBuilder(init)) as ValidatorBuilder<C, T, E>)

internal fun <C, T, E> eagerBuilder(init: Specification<C, T, E>.() -> Unit): EagerValidatorNodeBuilder<C, T, E> =
    EagerValidatorNodeBuilder(ValidatorsBuilder<C, T, E>().also(init))
