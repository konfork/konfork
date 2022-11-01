package io.github.konfork.core

data class HintedValidationBuilder<C, T, E> (
    val hint: HintBuilder<C, T?, E>,
    val init: ValidationBuilder<C, T, E>.() -> Unit,
)
