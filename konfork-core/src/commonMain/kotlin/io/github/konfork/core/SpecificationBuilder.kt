package io.github.konfork.core

import io.github.konfork.core.internal.ValidatorsBuilder

class SpecificationBuilder<C, T, E> internal constructor(
    private val builder: ValidatorsBuilder<C, T, E>,
) {
    infix fun with(init: Specification<C, T, E>.() -> Unit) {
        builder.init()
    }
}
