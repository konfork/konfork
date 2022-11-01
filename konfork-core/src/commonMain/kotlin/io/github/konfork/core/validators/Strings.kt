package io.github.konfork.core.validators

import io.github.konfork.core.ConstraintBuilder
import io.github.konfork.core.HintBuilder
import io.github.konfork.core.ValidationBuilder
import io.github.konfork.core.stringHint

fun <C, E> ValidationBuilder<C, String, E>.minLength(hint: HintBuilder<C, String, E>, length: Int): ConstraintBuilder<C, String, E> {
    require(length >= 0) { IllegalArgumentException("minLength requires the length to be >= 0") }
    return addConstraint(hint, length) { it.length >= length }
}
fun <C> ValidationBuilder<C, String, String>.minLength(length: Int) =
    minLength(stringHint("must have at least {0} characters"), length)

fun <C, E> ValidationBuilder<C, String, E>.maxLength(hint: HintBuilder<C, String, E>, length: Int): ConstraintBuilder<C, String, E> {
    require(length >= 0) { IllegalArgumentException("maxLength requires the length to be >= 0") }
    return addConstraint(hint, length) { it.length <= length }
}

fun <C> ValidationBuilder<C, String, String>.maxLength(length: Int) =
    maxLength(stringHint("must have at most {0} characters"), length)

fun <C, E> ValidationBuilder<C, String, E>.pattern(hint: HintBuilder<C, String, E>, pattern: Regex) =
    addConstraint(hint, pattern) { it.matches(pattern) }

fun <C> ValidationBuilder<C, String, String>.pattern(pattern: Regex) =
    pattern(stringHint("must match the expected pattern"), pattern)

fun <C, E> ValidationBuilder<C, String, E>.pattern(hint: HintBuilder<C, String, E>, pattern: String) =
    pattern(hint, pattern.toRegex())

fun <C> ValidationBuilder<C, String, String>.pattern(pattern: String) =
    pattern(pattern.toRegex())
