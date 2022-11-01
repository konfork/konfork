package io.github.konfork.core

typealias HintArguments = List<Any>
typealias HintBuilder<C, T, E> = C.(T, HintArguments) -> E

fun <C, T> stringHint(template: String): HintBuilder<C, T, String> = { value, args ->
    args
        .map(Any::toString)
        .foldIndexed(template.replace("{value}", value.toString())) { index, acc, arg ->
            acc.replace("{$index}", arg)
        }
}

fun <C, T, E> staticHint(e: E): HintBuilder<C, T, E> = { _, _ -> e }
