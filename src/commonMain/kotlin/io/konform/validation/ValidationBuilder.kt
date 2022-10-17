package io.konform.validation

import io.konform.validation.internal.*
import kotlin.jvm.JvmName
import kotlin.reflect.KFunction1
import kotlin.reflect.KProperty1

@DslMarker
private annotation class ValidationScope

@ValidationScope
abstract class ValidationBuilder<C, T, E> : ComposableBuilder<C, T, E> {
    abstract val requiredError: E
    abstract override fun build(): Validation<C, T, E>

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

    internal abstract fun <R : Any> ifPresent(name: String, mapFn: (T) -> R?, init: ValidationBuilder<C, R, E>.() -> Unit)
    infix fun <R : Any> KProperty1<T, R?>.ifPresent(init: ValidationBuilder<C, R, E>.() -> Unit) = ifPresent(this.name, this, init)
    infix fun <R : Any> KFunction1<T, R?>.ifPresent(init: ValidationBuilder<C, R, E>.() -> Unit) = ifPresent(this.name, this, init)

    internal abstract fun <R : Any> required(name: String, hint: HintBuilder<C, R?, E>, mapFn: (T) -> R?, init: ValidationBuilder<C, R, E>.() -> Unit): ConstraintBuilder<C, R?, E>
    infix fun <R : Any> KProperty1<T, R?>.required(init: ValidationBuilder<C, R, E>.() -> Unit): ConstraintBuilder<C, R?, E> = required(this.name, { _, _ -> requiredError }, this, init)
    infix fun <R : Any> KFunction1<T, R?>.required(init: ValidationBuilder<C, R, E>.() -> Unit): ConstraintBuilder<C, R?, E> = required(this.name, { _, _ -> requiredError }, this, init)

    abstract fun <S> run(validation: Validation<S, T, E>, map: (C) -> S)
    fun run(validation: Validation<C, T, E>) = run(validation, ::identity)

    abstract val <R> KProperty1<T, R>.has: ValidationBuilder<C, R, E>
}

interface ConstraintBuilder<C, T, E> {
    infix fun hint(hint: HintBuilder<C, T, E>) : ConstraintBuilder<C, T, E>
}

fun <C, T : Any, E> ValidationBuilder<C, T?, E>.ifPresent(init: ValidationBuilder<C, T, E>.() -> Unit) {
    val builder = ValidationNodeBuilder<C, T, E>(this.requiredError)
    init(builder)
    run(OptionalValidation(builder.build()))
}

fun <C, T : Any, E> ValidationBuilder<C, T?, E>.required(hint: HintBuilder<C, T?, E>, init: ValidationBuilder<C, T, E>.() -> Unit): ConstraintBuilder<C, T?, E> {
    val builder = ValidationNodeBuilder<C, T, E>(requiredError).also(init)
    val requiredValidationBuilder = RequiredValidationBuilder(hint, builder, ::identity, "")
    run(requiredValidationBuilder.build())
    return requiredValidationBuilder.requiredConstraintBuilder
}

@JvmName("onEachIterable")
fun <C, S, T : Iterable<S>, E> ValidationBuilder<C, T, E>.onEach(init: ValidationBuilder<C, S, E>.() -> Unit) {
    val builder = ValidationNodeBuilder<C, S, E>(requiredError)
    init(builder)
    @Suppress("UNCHECKED_CAST")
    run(IterableValidation(builder.build()) as Validation<C, T, E>)
}

@JvmName("onEachArray")
fun <C, T, E> ValidationBuilder<C, Array<T>, E>.onEach(init: ValidationBuilder<C, T, E>.() -> Unit) {
    val builder = ValidationNodeBuilder<C, T, E>(requiredError)
    init(builder)
    @Suppress("UNCHECKED_CAST")
    run(ArrayValidation(builder.build()) as Validation<C, Array<T>, E>)
}

@JvmName("onEachMap")
fun <C, K, V, T : Map<K, V>, E> ValidationBuilder<C, T, E>.onEach(init: ValidationBuilder<C, Map.Entry<K, V>, E>.() -> Unit) {
    val builder = ValidationNodeBuilder<C, Map.Entry<K, V>, E>(requiredError)
    init(builder)
    @Suppress("UNCHECKED_CAST")
    run(MapValidation(builder.build()) as Validation<C, T, E>)
}

typealias HintArguments = List<Any>
typealias HintBuilder<C, T, E> = C.(T, HintArguments) -> E

fun <C, T> StringHintBuilder(template: String): HintBuilder<C, T, String> = { value, args ->
    args
        .map(Any::toString)
        .foldIndexed(template.replace("{value}", value.toString())) { index, acc, arg ->
            acc.replace("{$index}", arg)
        }
}
