package io.github.konfork.core

interface ConstraintBuilder<C, T, E> {
    infix fun hint(hint: HintBuilder<C, T, E>) : ConstraintBuilder<C, T, E>
}

infix fun <C,T> ConstraintBuilder<C, T, String>.hint(hint: String) = hint(stringHint(hint))
