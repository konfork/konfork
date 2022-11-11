package io.github.konfork.core

import kotlin.jvm.JvmName

interface Validator<C, T, E> {

    companion object {
        operator fun <C, T, E> invoke(init: ValidationBuilder<C, T, E>.() -> Unit): Validator<C, T, E> =
            eagerBuilder(init).build()

        @JvmName("contextInvoke")
        operator fun <C, T> invoke(init: ValidationBuilder<C, T, String>.() -> Unit): Validator<C, T, String> =
            eagerBuilder(init).build()

        @JvmName("simpleInvoke")
        operator fun <T> invoke(init: ValidationBuilder<Unit, T, String>.() -> Unit): Validator<Unit, T, String> =
            eagerBuilder(init).build()
    }

    fun validate(context: C, value: T): ValidationResult<E, T>
    operator fun invoke(context: C, value: T) = validate(context, value)
}

operator fun <T, E> Validator<Unit, T, E>.invoke(value: T) = validate(Unit, value)
