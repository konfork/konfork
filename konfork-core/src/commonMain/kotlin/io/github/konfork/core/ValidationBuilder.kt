package io.github.konfork.core

import io.github.konfork.core.internal.*
import io.github.konfork.core.internal.ArrayValidationBuilder
import io.github.konfork.core.internal.ComposableBuilder
import io.github.konfork.core.internal.IterableValidationBuilder
import io.github.konfork.core.internal.MapValidationBuilder
import io.github.konfork.core.internal.OptionalValidationBuilder
import io.github.konfork.core.internal.RequiredValidationBuilder
import io.github.konfork.core.internal.NodeValidationsBuilder
import kotlin.jvm.JvmName
import kotlin.reflect.KFunction1
import kotlin.reflect.KProperty1

@DslMarker
private annotation class ValidationScope

@ValidationScope
abstract class ValidationBuilder<C, T, E> {
    abstract fun addConstraint(hint: HintBuilder<C, T, E>, vararg values: Any, test: C.(T) -> Boolean): ConstraintBuilder<C, T, E>

    internal abstract fun <R> property(name: String, mapFn: (T) -> R, init: ValidationBuilder<C, R, E>.() -> Unit)
    operator fun <R> KProperty1<T, R>.invoke(init: ValidationBuilder<C, R, E>.() -> Unit) = property(name, this, init)
    operator fun <R> KFunction1<T, R>.invoke(init: ValidationBuilder<C, R, E>.() -> Unit) = property(name, this, init)

    infix fun <R> KProperty1<T, R>.eager(init: ValidationBuilder<C, R, E>.() -> Unit) = this(init)
    infix fun <R> KFunction1<T, R>.eager(init: ValidationBuilder<C, R, E>.() -> Unit) = this(init)
    abstract fun eager(init: ValidationBuilder<C, T, E>.() -> Unit)

    internal abstract fun <R> lazy(name: String, mapFn: (T) -> R, init: ValidationBuilder<C, R, E>.() -> Unit)
    infix fun <R> KProperty1<T, R>.lazy(init: ValidationBuilder<C, R, E>.() -> Unit) = lazy(name, this, init)
    infix fun <R> KFunction1<T, R>.lazy(init: ValidationBuilder<C, R, E>.() -> Unit) = lazy(name, this, init)
    abstract fun lazy(init: ValidationBuilder<C, T, E>.() -> Unit)

    internal abstract fun <R> onEachIterable(name: String, mapFn: (T) -> Iterable<R>, init: ValidationBuilder<C, R, E>.() -> Unit)
    @JvmName("onEachIterable")
    infix fun <R> KProperty1<T, Iterable<R>>.onEach(init: ValidationBuilder<C, R, E>.() -> Unit) = onEachIterable(this.name, this, init)
    @JvmName("onEachIterable")
    infix fun <R> KFunction1<T, Iterable<R>>.onEach(init: ValidationBuilder<C, R, E>.() -> Unit) = onEachIterable(this.name, this, init)

    internal abstract fun <R> onEachArray(name: String, mapFn: (T) -> Array<R>, init: ValidationBuilder<C, R, E>.() -> Unit)
    @JvmName("onEachArray")
    infix fun <R> KProperty1<T, Array<R>>.onEach(init: ValidationBuilder<C, R, E>.() -> Unit) = onEachArray(this.name, this, init)
    @JvmName("onEachArray")
    infix fun <R> KFunction1<T, Array<R>>.onEach(init: ValidationBuilder<C, R, E>.() -> Unit) = onEachArray(this.name, this, init)

    internal abstract fun <K, V> onEachMap(name: String, mapFn: (T) -> Map<K, V>, init: ValidationBuilder<C, Map.Entry<K, V>, E>.() -> Unit)
    @JvmName("onEachMap")
    infix fun <K, V> KProperty1<T, Map<K, V>>.onEach(init: ValidationBuilder<C, Map.Entry<K, V>, E>.() -> Unit) = onEachMap(this.name, this, init)
    @JvmName("onEachMap")
    infix fun <K, V> KFunction1<T, Map<K, V>>.onEach(init: ValidationBuilder<C, Map.Entry<K, V>, E>.() -> Unit) = onEachMap(this.name, this, init)

    internal abstract fun <K, V> onEachMapValue(name: String, mapFn: (T) -> Map<K, V>, init: ValidationBuilder<C, V, E>.() -> Unit)
    @JvmName("onEachMapValue")
    infix fun <K, V> KProperty1<T, Map<K, V>>.onEachValue(init: ValidationBuilder<C, V, E>.() -> Unit) = onEachMapValue(this.name, this, init)
    @JvmName("onEachMapValue")
    infix fun <K, V> KFunction1<T, Map<K, V>>.onEachValue(init: ValidationBuilder<C, V, E>.() -> Unit) = onEachMapValue(this.name, this, init)

    internal abstract fun <K, V> onEachMapKey(name: String, mapFn: (T) -> Map<K, V>, init: ValidationBuilder<C, K, E>.() -> Unit)
    @JvmName("onEachMapKey")
    infix fun <K, V> KProperty1<T, Map<K, V>>.onEachKey(init: ValidationBuilder<C, K, E>.() -> Unit) = onEachMapKey(this.name, this, init)
    @JvmName("onEachMapKey")
    infix fun <K, V> KFunction1<T, Map<K, V>>.onEachKey(init: ValidationBuilder<C, K, E>.() -> Unit) = onEachMapKey(this.name, this, init)

    internal abstract fun <R : Any> ifPresent(name: String, mapFn: (T) -> R?, init: ValidationBuilder<C, R, E>.() -> Unit)
    infix fun <R : Any> KProperty1<T, R?>.ifPresent(init: ValidationBuilder<C, R, E>.() -> Unit) = ifPresent(this.name, this, init)
    infix fun <R : Any> KFunction1<T, R?>.ifPresent(init: ValidationBuilder<C, R, E>.() -> Unit) = ifPresent(this.name, this, init)

    internal abstract fun <R : Any> required(name: String, hint: HintBuilder<C, R?, E>, mapFn: (T) -> R?, init: ValidationBuilder<C, R, E>.() -> Unit): ConstraintBuilder<C, R?, E>
    infix fun <R : Any> KProperty1<T, R?>.required(hintedBuilder: HintedValidationBuilder<C, R, E>): ConstraintBuilder<C, R?, E> =
        required(this.name, hintedBuilder.hint, this, hintedBuilder.init)
    infix fun <R : Any> KFunction1<T, R?>.required(hintedBuilder: HintedValidationBuilder<C, R, E>): ConstraintBuilder<C, R?, E> =
        required(this.name, hintedBuilder.hint, this, hintedBuilder.init)

    abstract fun <C, R, E> with(hint: HintBuilder<C, R?, E>, init: ValidationBuilder<C, R, E>.() -> Unit): HintedValidationBuilder<C, R, E>
    abstract fun <C, R> with(init: ValidationBuilder<C, R, String>.() -> Unit): HintedValidationBuilder<C, R, String>

    abstract fun <S> run(validation: Validation<S, T, E>, map: (C) -> S)
    fun run(validation: Validation<C, T, E>) = run(validation, ::identity)

    internal abstract fun add(builder: ComposableBuilder<C, T, E>)

    val <R> KProperty1<T, R>.has: ValidationBuilder<C, R, E> get() = has(this.name, this)
    val <R> KFunction1<T, R>.has: ValidationBuilder<C, R, E> get() = has(this.name, this)
    internal abstract fun <R> has(name: String, mapFn: (T) -> R): ValidationBuilder<C, R, E>
}

fun <C, T> ValidationBuilder<C, T, String>.addConstraint(hint: String, vararg values: Any, test: C.(T) -> Boolean): ConstraintBuilder<C, T, String> =
    addConstraint(stringHint(hint), *values) { test(it) }

fun <C, T : Any, E> ValidationBuilder<C, T?, E>.ifPresent(init: ValidationBuilder<C, T, E>.() -> Unit) =
    add(OptionalValidationBuilder(eagerBuilder(init)))

fun <C, T : Any, E> ValidationBuilder<C, T?, E>.required(hint: HintBuilder<C, T?, E>, init: ValidationBuilder<C, T, E>.() -> Unit): ConstraintBuilder<C, T?, E> =
    RequiredValidationBuilder(hint, eagerBuilder(init))
        .also(::add)
        .constraintBuilder

@JvmName("onEachIterable")
@Suppress("UNCHECKED_CAST")
fun <C, S, T : Iterable<S>, E> ValidationBuilder<C, T, E>.onEach(init: ValidationBuilder<C, S, E>.() -> Unit) =
    add(IterableValidationBuilder(eagerBuilder(init)) as ComposableBuilder<C, T, E>)

@JvmName("onEachArray")
fun <C, T, E> ValidationBuilder<C, Array<T>, E>.onEach(init: ValidationBuilder<C, T, E>.() -> Unit) =
    add(ArrayValidationBuilder(eagerBuilder(init)))

@JvmName("onEachMap")
@Suppress("UNCHECKED_CAST")
fun <C, K, V, T : Map<K, V>, E> ValidationBuilder<C, T, E>.onEach(init: ValidationBuilder<C, Map.Entry<K, V>, E>.() -> Unit) =
    add(MapValidationBuilder(eagerBuilder(init)) as ComposableBuilder<C, T, E>)

@JvmName("onEachMapValue")
@Suppress("UNCHECKED_CAST")
fun <C, K, V, T : Map<K, V>, E> ValidationBuilder<C, T, E>.onEachValue(init: ValidationBuilder<C, V, E>.() -> Unit) =
    add(MapValueValidationBuilder<C, K, V, E>(eagerBuilder(init)) as ComposableBuilder<C, T, E>)

@JvmName("onEachMapKey")
@Suppress("UNCHECKED_CAST")
fun <C, K, V, T : Map<K, V>, E> ValidationBuilder<C, T, E>.onEachKey(init: ValidationBuilder<C, K, E>.() -> Unit) =
    add(MapKeyValidationBuilder<C, K, V, E>(eagerBuilder(init)) as ComposableBuilder<C, T, E>)

internal fun <C, T, E> eagerBuilder(init: ValidationBuilder<C, T, E>.() -> Unit): EagerValidationNodeBuilder<C, T, E> =
    EagerValidationNodeBuilder(NodeValidationsBuilder<C, T, E>().also(init))
