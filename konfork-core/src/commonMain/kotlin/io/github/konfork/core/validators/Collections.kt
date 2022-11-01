package io.github.konfork.core.validators

import io.github.konfork.core.HintBuilder
import io.github.konfork.core.ValidationBuilder
import io.github.konfork.core.stringHint
import kotlin.jvm.JvmName

private const val minItemsTemplate = "must have at least {0} items"

@JvmName("minItemsIterable")
fun <C, T : Iterable<*>, E> ValidationBuilder<C, T, E>.minItems(hint: HintBuilder<C, T, E>, minSize: Int) =
    addConstraint(hint, minSize) { it.count() >= minSize }

@JvmName("minItemsIterable")
fun <C, T : Iterable<*>> ValidationBuilder<C, T, String>.minItems(minSize: Int) =
    minItems(stringHint(minItemsTemplate), minSize)

@JvmName("minItemsArray")
fun <C, T, E> ValidationBuilder<C, Array<T>, E>.minItems(hint: HintBuilder<C, Array<T>, E>, minSize: Int) =
    addConstraint(hint, minSize) { it.count() >= minSize }

@JvmName("minItemsArray")
fun <C, T> ValidationBuilder<C, Array<T>, String>.minItems(minSize: Int) =
    minItems(stringHint(minItemsTemplate), minSize)

@JvmName("minItemsMap")
fun <C, T : Map<*, *>, E> ValidationBuilder<C, T, E>.minItems(hint: HintBuilder<C, T, E>, minSize: Int) =
    addConstraint(hint, minSize) { it.count() >= minSize }

@JvmName("minItemsMap")
fun <C, T : Map<*, *>> ValidationBuilder<C, T, String>.minItems(minSize: Int) =
    minItems(stringHint(minItemsTemplate), minSize)

private const val maxItemsTemplate = "must have at most {0} items"

@JvmName("maxItemsIterable")
fun <C, T : Iterable<*>, E> ValidationBuilder<C, T, E>.maxItems(hint: HintBuilder<C, T, E>, maxSize: Int) =
    addConstraint(hint, maxSize) { it.count() <= maxSize }

@JvmName("maxItemsIterable")
fun <C, T : Iterable<*>> ValidationBuilder<C, T, String>.maxItems(maxSize: Int) =
    maxItems(stringHint(maxItemsTemplate), maxSize)

@JvmName("maxItemsArray")
fun <C, T, E> ValidationBuilder<C, Array<T>, E>.maxItems(hint: HintBuilder<C, Array<T>, E>, maxSize: Int) =
    addConstraint(hint, maxSize) { it.count() <= maxSize }

@JvmName("maxItemsArray")
fun <C, T> ValidationBuilder<C, Array<T>, String>.maxItems(maxSize: Int) =
    maxItems(stringHint(maxItemsTemplate), maxSize)

@JvmName("maxItemsMap")
fun <C, T : Map<*, *>, E> ValidationBuilder<C, T, E>.maxItems(hint: HintBuilder<C, T, E>, maxSize: Int) =
    addConstraint(hint, maxSize) { it.count() <= maxSize }

@JvmName("maxItemsMap")
fun <C, T : Map<*, *>> ValidationBuilder<C, T, String>.maxItems(maxSize: Int) =
    maxItems(stringHint(maxItemsTemplate), maxSize)

fun <C, T : Map<*, *>, E> ValidationBuilder<C, T, E>.minProperties(hint: HintBuilder<C, T, E>, minSize: Int) =
    minItems(hint, minSize)

fun <C, T : Map<*, *>> ValidationBuilder<C, T, String>.minProperties(minSize: Int) =
    minProperties(stringHint("must have at least {0} properties"), minSize)

fun <C, T : Map<*, *>, E> ValidationBuilder<C, T, E>.maxProperties(hint: HintBuilder<C, T, E>, maxSize: Int) =
    maxItems(hint, maxSize)

fun <C, T : Map<*, *>> ValidationBuilder<C, T, String>.maxProperties(maxSize: Int) =
    maxProperties(stringHint("must have at most {0} properties"), maxSize)

private const val uniqueItemsTemplate = "all items must be unique"

@JvmName("uniqueItemsIterable")
fun <C, T : Iterable<*>, E> ValidationBuilder<C, T, E>.uniqueItems(hint: HintBuilder<C, T, E>, unique: Boolean) =
    addConstraint(hint, unique) { !unique || it.distinct().count() == it.count() }

@JvmName("uniqueItemsIterable")
fun <C, T : Iterable<*>> ValidationBuilder<C, T, String>.uniqueItems(unique: Boolean) =
    uniqueItems(stringHint(uniqueItemsTemplate), unique)

@JvmName("uniqueItemsArray")
fun <C, T, E> ValidationBuilder<C, Array<T>, E>.uniqueItems(hint: HintBuilder<C, Array<T>, E>, unique: Boolean) =
    addConstraint(hint, unique) { !unique || it.distinct().count() == it.count() }

@JvmName("uniqueItemsArray")
fun <C, T> ValidationBuilder<C, Array<T>, String>.uniqueItems(unique: Boolean) =
    uniqueItems(stringHint(uniqueItemsTemplate), unique)
