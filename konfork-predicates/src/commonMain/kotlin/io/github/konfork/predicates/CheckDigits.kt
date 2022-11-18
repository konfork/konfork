package io.github.konfork.predicates

fun isMod10(evenWeight: Int, oddWeight: Int) : (String) -> Boolean = {
    isMod10(it, evenWeight, oddWeight, Int::times)
}

fun isEan(length: Int): (String) -> Boolean {
    val mod10Fn = isMod10(1, 3)
    return {
        it.length == length && mod10Fn(it)
    }
}

private fun isMod10(s: String, evenWeight: Int, oddWeight: Int, map: (Int, Int) -> Int): Boolean =
    isAllDigits(s) && validateMod10Checksum(s, evenWeight, oddWeight, map)

private fun validateMod10Checksum(s: String, evenWeight: Int, oddWeight: Int, map: (Int, Int) -> Int): Boolean =
    s.reversed()
        .map(Char::digitToInt)
        .asSequence()
        .zip(alternatingSequence(evenWeight, oddWeight), map)
        .sum() % 10 == 0

private fun alternatingSequence(even: Int, odd: Int): Sequence<Int> =
    generateIndexedSequence { it % 2 == 0 }
        .map { if (it) even else odd }

private fun <T : Any> generateIndexedSequence(generator: (Int) -> T): Sequence<T> =
    generateSequence(0) { index -> index + 1 }
        .map(generator)
