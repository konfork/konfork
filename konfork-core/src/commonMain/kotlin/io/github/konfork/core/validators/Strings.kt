package io.github.konfork.core.validators

import io.github.konfork.core.*
import io.github.konfork.predicates.*

fun <C, E> Specification<C, String, E>.all(hint: HintBuilder<C, String, E>, predicate: (Char) -> Boolean): ConstraintBuilder<C, String, E> =
    addConstraint(hint) { it.all(predicate) }

fun <C> Specification<C, String, String>.all(predicate: (Char) -> Boolean): ConstraintBuilder<C, String, String> =
    all(staticHint("not all characters comply"), predicate)

fun <C, E> Specification<C, String, E>.allDigits(hint: HintBuilder<C, String, E>): ConstraintBuilder<C, String, E> =
    all(hint, Char::isDigit)

fun <C> Specification<C, String, String>.allDigits(): ConstraintBuilder<C, String, String> =
    allDigits(staticHint("is not all digits"))

fun <C, E> Specification<C, String, E>.any(hint: HintBuilder<C, String, E>, predicate: (Char) -> Boolean): ConstraintBuilder<C, String, E> =
    addConstraint(hint) { it.any(predicate) }

fun <C> Specification<C, String, String>.any(predicate: (Char) -> Boolean): ConstraintBuilder<C, String, String> =
    any(staticHint("none of the characters comply"), predicate)


fun <C, E> Specification<C, String, E>.contains(
    hint: HintBuilder<C, String, E>,
    char: Char,
    ignoreCase: Boolean = false
): ConstraintBuilder<C, String, E> =
    addConstraint(hint, char, ignoreCase) { it.contains(char, ignoreCase) }

fun <C> Specification<C, String, String>.contains(char: Char, ignoreCase: Boolean = false): ConstraintBuilder<C, String, String> {
    val message = if (ignoreCase) {
        " when ignoring case"
    } else {
        ""
    }
    return contains(stringHint("does not contain character '{0}'$message"), char, ignoreCase)
}

fun <C, E> Specification<C, String, E>.contentEquals(hint: HintBuilder<C, String, E>, other: String): ConstraintBuilder<C, String, E> =
    addConstraint(hint) { it.contentEquals(other) }

fun <C> Specification<C, String, String>.contentEquals(other: String): ConstraintBuilder<C, String, String> =
    contentEquals(staticHint("content not equal"), other)

fun <C, E> Specification<C, String, E>.ean(hint: HintBuilder<C, String, E>, length: Int): ConstraintBuilder<C, String, E> {
    val eanFn = isEan(length)
    return addConstraint(hint, length) { eanFn(it) }
}

fun <C> Specification<C, String, String>.ean(length: Int): ConstraintBuilder<C, String, String> =
    ean(stringHint("is not a valid ean{0}"), length)

fun <C, E> Specification<C, String, E>.email(hint: HintBuilder<C, String, E>): ConstraintBuilder<C, String, E> =
    addConstraint(hint) { isEmail(it) }

fun <C> Specification<C, String, String>.email(): ConstraintBuilder<C, String, String> =
    email(staticHint("is not a valid email"))

fun <C, E> Specification<C, String, E>.endsWith(
    hint: HintBuilder<C, String, E>,
    suffix: String,
    ignoreCase: Boolean = false
): ConstraintBuilder<C, String, E> =
    addConstraint(hint, suffix, ignoreCase) { it.endsWith(suffix, ignoreCase) }

fun <C> Specification<C, String, String>.endsWith(suffix: String, ignoreCase: Boolean = false): ConstraintBuilder<C, String, String> {
    val message = if (ignoreCase) {
        " when ignoring case"
    } else {
        ""
    }
    return endsWith(stringHint("does not end with \"{0}\"$message"), suffix, ignoreCase)
}

fun <C, E> Specification<C, String, E>.isBlank(hint: HintBuilder<C, String, E>): ConstraintBuilder<C, String, E> =
    addConstraint(hint) { it.isBlank() }

fun <C> Specification<C, String, String>.isBlank(): ConstraintBuilder<C, String, String> =
    isBlank(staticHint("is not blank"))

fun <C, E> Specification<C, String, E>.isEmpty(hint: HintBuilder<C, String, E>): ConstraintBuilder<C, String, E> =
    addConstraint(hint) { it.isEmpty() }

fun <C> Specification<C, String, String>.isEmpty(): ConstraintBuilder<C, String, String> =
    isEmpty(staticHint("is not empty"))


fun <C, E> Specification<C, String, E>.isNotBlank(hint: HintBuilder<C, String, E>): ConstraintBuilder<C, String, E> =
    addConstraint(hint) { it.isNotBlank() }

fun <C> Specification<C, String, String>.isNotBlank(): ConstraintBuilder<C, String, String> =
    isNotBlank(staticHint("is blank"))

fun <C, E> Specification<C, String, E>.isNotEmpty(hint: HintBuilder<C, String, E>): ConstraintBuilder<C, String, E> =
    addConstraint(hint) { it.isNotEmpty() }

fun <C> Specification<C, String, String>.isNotEmpty(): ConstraintBuilder<C, String, String> =
    isNotEmpty(staticHint("is empty"))

fun <C, E> Specification<C, String, E>.isbn(hint: HintBuilder<C, String, E>): ConstraintBuilder<C, String, E> =
    addConstraint(hint) { isIsbn(it) }

fun <C> Specification<C, String, String>.isbn(): ConstraintBuilder<C, String, String> =
    isbn(staticHint("is not a valid isbn"))

fun <C, E> Specification<C, String, E>.isbn10(hint: HintBuilder<C, String, E>): ConstraintBuilder<C, String, E> =
    addConstraint(hint) { isIsbn10(it) }

fun <C> Specification<C, String, String>.isbn10(): ConstraintBuilder<C, String, String> =
    isbn10(staticHint("is not a valid isbn10"))

fun <C, E> Specification<C, String, E>.isbn13(hint: HintBuilder<C, String, E>): ConstraintBuilder<C, String, E> =
    addConstraint(hint) { isIsbn13(it) }

fun <C> Specification<C, String, String>.isbn13(): ConstraintBuilder<C, String, String> =
    isbn13(staticHint("is not a valid isbn13"))

fun <C, E> Specification<C, String, E>.lengthIn(
    hint: HintBuilder<C, String, E>,
    range: IntRange
): ConstraintBuilder<C, String, E> {
    require(range.first >= 0 && range.last >= 0) { IllegalArgumentException("lengthIn requires the length to be >= 0") }
    return addConstraint(hint, range.first, range.last) { it.length in range }
}

fun <C> Specification<C, String, String>.lengthIn(range: IntRange): ConstraintBuilder<C, String, String> =
    lengthIn(stringHint("must have at least {0} and at most {1} characters"), range)

fun <C, E> Specification<C, String, E>.luhn(hint: HintBuilder<C, String, E>): ConstraintBuilder<C, String, E> =
    addConstraint(hint) { isLuhn(it) }

fun <C> Specification<C, String, String>.luhn(): ConstraintBuilder<C, String, String> =
    luhn(staticHint("does not have a valid luhn check digit"))

fun <C, E> Specification<C, String, E>.maxLength(
    hint: HintBuilder<C, String, E>,
    length: Int
): ConstraintBuilder<C, String, E> {
    require(length >= 0) { IllegalArgumentException("maxLength requires the length to be >= 0") }
    return addConstraint(hint, length) { it.length <= length }
}

fun <C> Specification<C, String, String>.maxLength(length: Int): ConstraintBuilder<C, String, String> =
    maxLength(stringHint("must have at most {0} characters"), length)

fun <C, E> Specification<C, String, E>.minLength(
    hint: HintBuilder<C, String, E>,
    length: Int
): ConstraintBuilder<C, String, E> {
    require(length >= 0) { IllegalArgumentException("minLength requires the length to be >= 0") }
    return addConstraint(hint, length) { it.length >= length }
}

fun <C> Specification<C, String, String>.minLength(length: Int): ConstraintBuilder<C, String, String> =
    minLength(stringHint("must have at least {0} characters"), length)

fun <C, E> Specification<C, String, E>.mod10(hint: HintBuilder<C, String, E>, evenWeight: Int, oddWeight: Int): ConstraintBuilder<C, String, E> {
    val mod10Fn = isMod10(evenWeight, oddWeight)
    return addConstraint(hint) { mod10Fn(it) }
}

fun <C> Specification<C, String, String>.mod10(evenWeight: Int, oddWeight: Int): ConstraintBuilder<C, String, String> =
    mod10(staticHint("does not have a valid mod10 check digit"), evenWeight, oddWeight)


fun <C, E> Specification<C, String, E>.mod11(hint: HintBuilder<C, String, E>, startWeight: Int, endWeight: Int): ConstraintBuilder<C, String, E> {
    val mod11Fn = isMod11(startWeight, endWeight)
    return addConstraint(hint) { mod11Fn(it) }
}

fun <C> Specification<C, String, String>.mod11(startWeight: Int, endWeight: Int): ConstraintBuilder<C, String, String> =
    mod11(staticHint("does not have a valid mod11 check digit"), startWeight, endWeight)

fun <C, E> Specification<C, String, E>.nilUuid(hint: HintBuilder<C, String, E>): ConstraintBuilder<C, String, E> =
    addConstraint(hint) { isNilUuid(it) }

fun <C> Specification<C, String, String>.nilUuid(): ConstraintBuilder<C, String, String> =
    nilUuid(staticHint("is not the nil uuid"))

fun <C, E> Specification<C, String, E>.none(hint: HintBuilder<C, String, E>, predicate: (Char) -> Boolean): ConstraintBuilder<C, String, E> =
    addConstraint(hint) { it.none(predicate) }

fun <C> Specification<C, String, String>.none(predicate: (Char) -> Boolean): ConstraintBuilder<C, String, String> =
    none(staticHint("some character does comply"), predicate)

fun <C, E> Specification<C, String, E>.pattern(hint: HintBuilder<C, String, E>, pattern: Regex): ConstraintBuilder<C, String, E> =
    addConstraint(hint, pattern) { it.matches(pattern) }

fun <C> Specification<C, String, String>.pattern(pattern: Regex): ConstraintBuilder<C, String, String> =
    pattern(staticHint("must match the expected pattern"), pattern)

fun <C, E> Specification<C, String, E>.pattern(hint: HintBuilder<C, String, E>, pattern: String): ConstraintBuilder<C, String, E> =
    pattern(hint, pattern.toRegex())

fun <C> Specification<C, String, String>.pattern(pattern: String): ConstraintBuilder<C, String, String> =
    pattern(pattern.toRegex())

fun <C, E> Specification<C, String, E>.startsWith(
    hint: HintBuilder<C, String, E>,
    prefix: String,
    ignoreCase: Boolean = false
): ConstraintBuilder<C, String, E> =
    addConstraint(hint, prefix, ignoreCase) { it.startsWith(prefix, ignoreCase) }

fun <C> Specification<C, String, String>.startsWith(prefix: String, ignoreCase: Boolean = false): ConstraintBuilder<C, String, String> {
    val message = if (ignoreCase) {
        " when ignoring case"
    } else {
        ""
    }
    return startsWith(stringHint("does not start with \"{0}\"$message"), prefix, ignoreCase)
}

fun <C, E> Specification<C, String, E>.uuid(hint: HintBuilder<C, String, E>): ConstraintBuilder<C, String, E> =
    addConstraint(hint) { isUuid(it) }

fun <C> Specification<C, String, String>.uuid(): ConstraintBuilder<C, String, String> =
    uuid(staticHint("is not a valid uuid"))

fun <C, E> Specification<C, String, E>.uuid(hint: HintBuilder<C, String, E>, version: Int): ConstraintBuilder<C, String, E> =
    addConstraint(hint, version) { isUuidVersion(1)(it) }

fun <C> Specification<C, String, String>.uuid(version: Int): ConstraintBuilder<C, String, String> =
    uuid(stringHint("is not a valid uuid version {0}"), version)
