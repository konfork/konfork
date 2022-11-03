package io.github.konfork.core

import io.github.konfork.core.internal.*
import io.github.konfork.core.internal.ArrayValidationBuilder
import io.github.konfork.core.internal.ComposableBuilder
import io.github.konfork.core.internal.IterableValidationBuilder
import io.github.konfork.core.internal.MapValidationBuilder
import io.github.konfork.core.internal.OptionalValidationBuilder
import io.github.konfork.core.internal.RequiredValidationBuilder
import io.github.konfork.core.internal.ValidationNodeBuilder
import kotlin.jvm.JvmName
import kotlin.reflect.KFunction1
import kotlin.reflect.KProperty1

@DslMarker
private annotation class ValidationScope

@ValidationScope
abstract class ValidationBuilder<C, T, E> {
    abstract fun build(): Validation<C, T, E>

    abstract fun addConstraint(hint: HintBuilder<C, T, E>, vararg values: Any, test: C.(T) -> Boolean): ConstraintBuilder<C, T, E>

    abstract operator fun <R> KProperty1<T, R>.invoke(init: ValidationBuilder<C, R, E>.() -> Unit)
    abstract operator fun <R> KFunction1<T, R>.invoke(init: ValidationBuilder<C, R, E>.() -> Unit)

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

    abstract val <R> KProperty1<T, R>.has: ValidationBuilder<C, R, E>
}

fun <C, T> ValidationBuilder<C, T, String>.addConstraint(hint: String, vararg values: Any, test: C.(T) -> Boolean): ConstraintBuilder<C, T, String> =
    addConstraint(stringHint(hint), *values) { test(it) }

fun <C, T : Any, E> ValidationBuilder<C, T?, E>.ifPresent(init: ValidationBuilder<C, T, E>.() -> Unit) {
    val builder = ValidationNodeBuilder<C, T, E>().also(init)
    add(OptionalValidationBuilder(builder))
}

fun <C, T : Any, E> ValidationBuilder<C, T?, E>.required(hint: HintBuilder<C, T?, E>, init: ValidationBuilder<C, T, E>.() -> Unit): ConstraintBuilder<C, T?, E> {
    val builder = ValidationNodeBuilder<C, T, E>().also(init)
    val requiredValidationBuilder = RequiredValidationBuilder(hint, builder)
    add(requiredValidationBuilder)
    return requiredValidationBuilder.constraintBuilder
}

@JvmName("onEachIterable")
fun <C, S, T : Iterable<S>, E> ValidationBuilder<C, T, E>.onEach(init: ValidationBuilder<C, S, E>.() -> Unit) {
    val builder = ValidationNodeBuilder<C, S, E>().also(init)
    @Suppress("UNCHECKED_CAST")
    add(IterableValidationBuilder(builder) as ComposableBuilder<C, T, E>)
}

@JvmName("onEachArray")
fun <C, T, E> ValidationBuilder<C, Array<T>, E>.onEach(init: ValidationBuilder<C, T, E>.() -> Unit) {
    val builder = ValidationNodeBuilder<C, T, E>().also(init)
    add(ArrayValidationBuilder(builder))
}

@JvmName("onEachMap")
fun <C, K, V, T : Map<K, V>, E> ValidationBuilder<C, T, E>.onEach(init: ValidationBuilder<C, Map.Entry<K, V>, E>.() -> Unit) {
    val builder = ValidationNodeBuilder<C, Map.Entry<K, V>, E>().also(init)
    @Suppress("UNCHECKED_CAST")
    add(MapValidationBuilder(builder) as ComposableBuilder<C, T, E>)
}
