package io.github.konfork.core.validators

import io.github.konfork.core.ConstraintBuilder
import io.github.konfork.core.HintBuilder
import io.github.konfork.core.Specification
import io.github.konfork.core.stringHint
import io.github.konfork.predicates.isAllDigits
import io.github.konfork.predicates.isNilUuid
import io.github.konfork.predicates.isUuid
import io.github.konfork.predicates.isUuidVersion

fun <C, E> Specification<C, String, E>.minLength(hint: HintBuilder<C, String, E>, length: Int): ConstraintBuilder<C, String, E> {
    require(length >= 0) { IllegalArgumentException("minLength requires the length to be >= 0") }
    return addConstraint(hint, length) { it.length >= length }
}
fun <C> Specification<C, String, String>.minLength(length: Int) =
    minLength(stringHint("must have at least {0} characters"), length)

fun <C, E> Specification<C, String, E>.maxLength(hint: HintBuilder<C, String, E>, length: Int): ConstraintBuilder<C, String, E> {
    require(length >= 0) { IllegalArgumentException("maxLength requires the length to be >= 0") }
    return addConstraint(hint, length) { it.length <= length }
}

fun <C> Specification<C, String, String>.maxLength(length: Int) =
    maxLength(stringHint("must have at most {0} characters"), length)

fun <C, E> Specification<C, String, E>.pattern(hint: HintBuilder<C, String, E>, pattern: Regex) =
    addConstraint(hint, pattern) { it.matches(pattern) }

fun <C> Specification<C, String, String>.pattern(pattern: Regex) =
    pattern(stringHint("must match the expected pattern"), pattern)

fun <C, E> Specification<C, String, E>.pattern(hint: HintBuilder<C, String, E>, pattern: String) =
    pattern(hint, pattern.toRegex())

fun <C> Specification<C, String, String>.pattern(pattern: String) =
    pattern(pattern.toRegex())

fun <C, E> Specification<C, String, E>.uuid(hint: HintBuilder<C, String, E>) =
    addConstraint(hint) { isUuid(it) }

fun <C> Specification<C, String, String>.uuid() =
    uuid(stringHint("is not a valid uuid"))

fun <C, E> Specification<C, String, E>.uuid(hint: HintBuilder<C, String, E>, version: Int) =
    addConstraint(hint, version) { isUuidVersion(1)(it) }

fun <C> Specification<C, String, String>.uuid(version: Int) =
    uuid(stringHint("is not a valid uuid version {1}"), version)

fun <C, E> Specification<C, String, E>.nilUuid(hint: HintBuilder<C, String, E>) =
    addConstraint(hint) { isNilUuid(it) }

fun <C> Specification<C, String, String>.nilUuid() =
    nilUuid(stringHint("is not the nil uuid"))

fun <C, E> Specification<C, String, E>.allDigits(hint: HintBuilder<C, String, E>) =
    addConstraint(hint) { isAllDigits(it) }

fun <C> Specification<C, String, String>.allDigits() =
    allDigits(stringHint("is not all digits"))
