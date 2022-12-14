package io.github.konfork.core

data class HintedSpecificationBuilder<C, T, E> (
    val hint: HintBuilder<C, T?, E>,
    val init: Specification<C, T, E>.() -> Unit,
)
