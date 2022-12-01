package io.github.konfork.predicates

fun isMod10(evenWeight: Int, oddWeight: Int): (String) -> Boolean {
    val weightSequence = alternatingSequence(evenWeight, oddWeight)
    return { isMod10(it, weightSequence, Int::times) }
}

fun isEan(length: Int): (String) -> Boolean {
    val mod10Fn = isMod10(1, 3)
    return { it.length == length && mod10Fn(it) }
}

fun isLuhn(s: String): Boolean =
    isAllDigits(s) && validateMod10Checksum(s, alternatingSequence(1, 2)) { l, r ->
        (l * r).let { (it / 10) + (it % 10) }
    }

private fun isAllDigits(s: String): Boolean = s.all(Char::isDigit)

private val MOD11_DIGITS = Regex("[0-9]+[xX]?")
fun isMod11(weightSequence: Sequence<Int>): (String) -> Boolean = {
    it.matches(MOD11_DIGITS) && validateMod11Checksum(it.map(Char::toMod11Int), weightSequence)
}

fun isMod11(startWeight: Int, endWeight: Int): (String) -> Boolean =
    isMod11(repeatingCounterSequence(startWeight, endWeight))

private val isMod11Isbn10 = isMod11(9, 1)
fun isIsbn10(s: String): Boolean =
    s.replace(Regex("[ -]"), "")
        .let { it.length == 10 && isMod11Isbn10(it) }

private val isEan13 = isEan(13)
fun isIsbn13(s: String): Boolean = isEan13(s.replace(Regex("[ -]"), ""))

fun isIsbn(s: String): Boolean = isIsbn10(s) || isIsbn13(s)

private fun isMod10(s: String, weightSequence: Sequence<Int>, map: (Int, Int) -> Int): Boolean =
    isAllDigits(s) && validateMod10Checksum(s, weightSequence, map)

private fun validateMod10Checksum(s: String, weightSequence: Sequence<Int>, map: (Int, Int) -> Int): Boolean =
    weightedSum(s.map(Char::digitToInt).reversed(), weightSequence, map)
        .let { it % 10 == 0 }

private fun alternatingSequence(even: Int, odd: Int): Sequence<Int> =
    generateIndexedSequence { it % 2 == 0 }
        .map { if (it) even else odd }

private fun <T : Any> generateIndexedSequence(generator: (Int) -> T): Sequence<T> =
    generateSequence(0) { index -> index + 1 }
        .map(generator)

private fun repeatingCounterSequence(start: Int, end: Int): Sequence<Int> =
    if (start <= end) {
        generateIndexedSequence { index -> start + index % (end - start + 1) }
    } else {
        generateIndexedSequence { index -> start - index % (start - end + 1) }
    }

private fun validateMod11Checksum(s: List<Int>, weightSequence: Sequence<Int>): Boolean =
    weightedSum(s.dropLast(1).reversed(), weightSequence, Int::times)
        .let { it % 11 == s.last() }

private fun weightedSum(list: List<Int>, weightSequence: Sequence<Int>, map: (Int, Int) -> Int): Int =
    list.asSequence()
        .zip(weightSequence, map)
        .sum()

private fun Char.toMod11Int(): Int =
    when (this) {
        'x' -> 10
        'X' -> 10
        else -> this.digitToInt()
    }
