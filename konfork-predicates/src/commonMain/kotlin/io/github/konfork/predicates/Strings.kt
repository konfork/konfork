package io.github.konfork.predicates

import kotlin.text.RegexOption.IGNORE_CASE

private val emailRegex = Regex(
    "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|" +
            "\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\" +
            "[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9]" +
            "(?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}" +
            "(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:" +
            "[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
)
fun isEmail(s: String): Boolean = s.matches(emailRegex)

private fun uuidRegexPattern(version: String): String =
    "[0-9A-F]{8}-[0-9A-F]{4}-[$version][0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}"
private fun uuidRegex(version: Int): Regex = Regex(uuidRegexPattern(version.toString()), IGNORE_CASE)

private const val nilUuidRegex = "00000000-0000-0000-0000-000000000000"
private val uuidRegex = Regex(uuidRegexPattern("1-5") + "|^" + nilUuidRegex + "$", IGNORE_CASE)
private val uuid1Regex = uuidRegex(1)
private val uuid2Regex = uuidRegex(2)
private val uuid3Regex = uuidRegex(3)
private val uuid4Regex = uuidRegex(4)
private val uuid5Regex = uuidRegex(5)

fun isUuid(s: String): Boolean = s.matches(uuidRegex)
fun isNilUuid(s: String): Boolean = s == nilUuidRegex
fun isUuidVersion(version: Int): (String) -> Boolean {
    val regex = when(version) {
        1 -> uuid1Regex
        2 -> uuid2Regex
        3 -> uuid3Regex
        4 -> uuid4Regex
        5 -> uuid5Regex
        else -> throw IllegalArgumentException("Only uuid versions 1-5 are supported")
    }
    return { it.matches(regex) }
}

private val ALL_DIGITS = Regex("[0-9]*")
fun isAllDigits(s: String): Boolean = s.matches(ALL_DIGITS)
