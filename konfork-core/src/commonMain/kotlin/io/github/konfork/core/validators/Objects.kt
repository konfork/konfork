package io.github.konfork.core.validators

import io.github.konfork.core.HintBuilder
import io.github.konfork.core.Specification
import io.github.konfork.core.stringHint
import kotlin.jvm.JvmName

inline fun <C, reified T, E> Specification<C, *, E>.type(noinline hint: HintBuilder<C, Any?, E>) =
    addConstraint(hint, T::class.toString()) { it is T }

@JvmName("simpleType")
inline fun <reified T> Specification<Unit, *, String>.type() =
    type<Unit, T, String>(stringHint("must be of the correct type"))

fun <C, T, E> Specification<C, T, E>.const(hint: HintBuilder<C, T, E>, expected: T) =
    addConstraint(hint, expected?.let { "'$it'" } ?: "null") { expected == it }

fun <C, T> Specification<C, T, String>.const(expected: T) =
    const(stringHint("must be {0}"), expected)
