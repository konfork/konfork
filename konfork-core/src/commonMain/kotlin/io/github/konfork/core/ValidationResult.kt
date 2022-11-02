package io.github.konfork.core

import kotlin.reflect.KFunction1
import kotlin.reflect.KProperty1

data class ValidationErrors<out E>(
    val propertyPath: String,
    val errors: List<E>,
) {
    override fun toString(): String = "ValidationError(dataPath=$propertyPath, errors=$errors)"
}

sealed class ValidationResult<out E, out T> {
    internal fun <R> map(transform: (T) -> R): ValidationResult<E, R> =
        flatMap { Valid(transform(it)) }

    internal fun mapErrorKey(keyTransform: (String) -> String): ValidationResult<E, T> =
        when (this) {
            is Valid -> this
            is Invalid -> Invalid(internalErrors.mapKeys { (key, _) -> keyTransform(key) })
        }
}

internal inline fun <E, T, U> ValidationResult<E, T>.flatMap(f: (T) -> ValidationResult<E, U>): ValidationResult<E, U> =
    when (this) {
        is Invalid -> this
        is Valid -> f(value)
    }

internal fun  <E, T> ValidationResult<E, T>.combineWith(other: ValidationResult<E, T>): ValidationResult<E, T> =
    when (this) {
        is Valid -> other
        is Invalid -> when (other) {
            is Valid -> this
            is Invalid ->
                Invalid(combineErrors(internalErrors, other.internalErrors))
        }
    }

data class Invalid<out E>(
    internal val internalErrors: Map<String, List<E>>,
) : ValidationResult<E, Nothing>() {

    internal constructor(e: E) : this(mapOf("" to listOf(e)))

    fun get(vararg path: Any): ValidationErrors<E> =
        propertyPath(path)
            .let { ValidationErrors(it, internalErrors[it].orEmpty()) }

    private fun propertyPath(path: Array<out Any>) =
        path.joinToString("", transform = ::toPathSegment)

    private fun toPathSegment(it: Any): String =
        when (it) {
            is KFunction1<*, *> -> ".${it.name}"
            is KProperty1<*, *> -> ".${it.name}"
            is Int -> "[$it]"
            else -> ".$it"
        }

    val errors: List<ValidationErrors<E>> by lazy {
        internalErrors.map { (path, errors ) ->
            ValidationErrors(path, errors)
        }
    }

    override fun toString(): String {
        return "Invalid(errors=${errors})"
    }
}

data class Valid<out T>(val value: T) : ValidationResult<Nothing, T>()

private fun <E> combineErrors(left: Map<String, List<E>>, right: Map<String, List<E>>) =
    (left.toList() + right.toList())
        .groupBy({ it.first }, { it.second })
        .mapValues { (_, values) -> values.flatten() }
