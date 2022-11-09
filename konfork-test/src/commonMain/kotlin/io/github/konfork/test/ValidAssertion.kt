package io.github.konfork.test

import io.github.konfork.core.Valid

data class ValidAssertion<T>(
    val result: Valid<T>,
    val value: T,
)
