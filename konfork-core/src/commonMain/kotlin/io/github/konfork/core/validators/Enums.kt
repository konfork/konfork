package io.github.konfork.core.validators

import io.github.konfork.core.ConstraintBuilder
import io.github.konfork.core.HintBuilder
import io.github.konfork.core.Specification
import kotlin.jvm.JvmName

fun <C, T> enumHintBuilder(): HintBuilder<C, T, String> = { _, arguments ->
    @Suppress("UNCHECKED_CAST")
    val names = (arguments[0] as List<String>).joinToString("', '", "'", "'")
    "must be one of: $names"
}

fun <C, T, E> Specification<C, T, E>.enum(hint: HintBuilder<C, T, E>, vararg allowed: T) =
    addConstraint(hint, allowed.toList()) { it in allowed }

fun <C, T> Specification<C, T, String>.enum(vararg allowed: T): ConstraintBuilder<C, T, String> =
    enum(enumHintBuilder(), *allowed)

inline fun <C, reified T : Enum<T>, E> Specification<C, String, E>.enum(noinline hint: HintBuilder<C, String, E>) =
    enumValues<T>()
        .map { it.name }
        .let { enumNames -> addConstraint(hint, enumNames) { it in enumNames } }

inline fun <C, reified T : Enum<T>> Specification<C, String, String>.enum() =
    enum<C, T, String>(enumHintBuilder())

@JvmName("simpleEnum")
inline fun <reified T : Enum<T>> Specification<Unit, String, String>.enum() =
    enum<Unit, T>()
