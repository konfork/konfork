package io.github.konfork.core.validators

import io.github.konfork.core.ConstraintBuilder
import io.github.konfork.core.HintBuilder
import io.github.konfork.core.Specification
import io.github.konfork.core.stringHint
import io.github.konfork.predicates.*

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
    uuid(stringHint("is not a valid uuid version {0}"), version)

fun <C, E> Specification<C, String, E>.nilUuid(hint: HintBuilder<C, String, E>) =
    addConstraint(hint) { isNilUuid(it) }

fun <C> Specification<C, String, String>.nilUuid() =
    nilUuid(stringHint("is not the nil uuid"))

fun <C, E> Specification<C, String, E>.allDigits(hint: HintBuilder<C, String, E>) =
    addConstraint(hint) { isAllDigits(it) }

fun <C> Specification<C, String, String>.allDigits() =
    allDigits(stringHint("is not all digits"))

fun <C, E> Specification<C, String, E>.isbn(hint: HintBuilder<C, String, E>) =
    addConstraint(hint) { isIsbn(it) }

fun <C> Specification<C, String, String>.isbn() =
    isbn(stringHint("is not a valid isbn"))

fun <C, E> Specification<C, String, E>.isbn10(hint: HintBuilder<C, String, E>) =
    addConstraint(hint) { isIsbn10(it) }

fun <C> Specification<C, String, String>.isbn10() =
    isbn10(stringHint("is not a valid isbn10"))

fun <C, E> Specification<C, String, E>.isbn13(hint: HintBuilder<C, String, E>) =
    addConstraint(hint) { isIsbn13(it) }

fun <C> Specification<C, String, String>.isbn13() =
    isbn13(stringHint("is not a valid isbn13"))

fun <C, E> Specification<C, String, E>.mod10(hint: HintBuilder<C, String, E>, evenWeight: Int, oddWeight: Int) {
    val mod10Fn = isMod10(evenWeight, oddWeight)
    addConstraint(hint) { mod10Fn(it) }
}

fun <C> Specification<C, String, String>.mod10(evenWeight: Int, oddWeight: Int) =
    mod10(stringHint("does not have a valid mod10 check digit"), evenWeight, oddWeight)

fun <C, E> Specification<C, String, E>.ean(hint: HintBuilder<C, String, E>, length: Int) {
    val eanFn = isEan(length)
    addConstraint(hint, length) { eanFn(it) }
}

fun <C> Specification<C, String, String>.ean(length: Int) =
    ean(stringHint("is not a valid ean{0}"), length)

fun <C, E> Specification<C, String, E>.luhn(hint: HintBuilder<C, String, E>) =
    addConstraint(hint) { isLuhn(it) }

fun <C> Specification<C, String, String>.luhn() =
    luhn(stringHint("does not have a valid luhn check digit"))

fun <C, E> Specification<C, String, E>.mod11(hint: HintBuilder<C, String, E>, startWeight: Int, endWeight: Int) {
    val mod11Fn = isMod11(startWeight, endWeight)
    addConstraint(hint) { mod11Fn(it) }
}

fun <C> Specification<C, String, String>.mod11(startWeight: Int, endWeight: Int) =
    mod11(stringHint("does not have a valid mod11 check digit"), startWeight, endWeight)
