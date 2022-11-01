package io.github.konfork.core

import kotlin.reflect.KProperty1

interface ValidationError<out E> {
    val dataPath: String
    val message: E
}

internal data class PropertyValidationError<E>(
    override val dataPath: String,
    override val message: E,
) : ValidationError<E> {
    override fun toString(): String {
        return "ValidationError(dataPath=$dataPath, message=$message)"
    }
}

interface ValidationErrors<out E> : List<ValidationError<E>>

internal object NoValidationErrors : ValidationErrors<Nothing>, List<ValidationError<Nothing>> by emptyList()
internal class DefaultValidationErrors<E>(private val errors: List<ValidationError<E>>) : ValidationErrors<E>, List<ValidationError<E>> by errors {
    override fun toString(): String {
        return errors.toString()
    }
}

sealed class ValidationResult<out E, out T> {
    abstract val errors: ValidationErrors<E>

    abstract operator fun get(vararg propertyPath: Any): List<E>?

    fun <R> map(transform: (T) -> R): ValidationResult<E, R> =
        flatMap { Valid(transform(it)) }

    internal fun mapErrorKey(keyTransform: (String) -> String): ValidationResult<E, T> =
        when (this) {
            is Valid -> this
            is Invalid -> Invalid(internalErrors.mapKeys { (key, _) -> keyTransform(key) })
        }
}

inline fun <E, T, U> ValidationResult<E, T>.flatMap(f: (T) -> ValidationResult<E, U>): ValidationResult<E, U> =
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

    override fun get(vararg propertyPath: Any): List<E>? =
        internalErrors[propertyPath.joinToString("", transform = ::toPathSegment)]

    private fun toPathSegment(it: Any): String {
        return when (it) {
            is KProperty1<*, *> -> ".${it.name}"
            is Int -> "[$it]"
            else -> ".$it"
        }
    }

    override val errors: ValidationErrors<E> by lazy {
        DefaultValidationErrors(
            internalErrors.flatMap { (path, errors ) ->
                errors.map { PropertyValidationError(path, it) }
            }
        )
    }

    override fun toString(): String {
        return "Invalid(errors=${errors})"
    }
}

data class Valid<out E, out T>(val value: T) : ValidationResult<E, T>() {
    override fun get(vararg propertyPath: Any): List<E>? = null
    override val errors: ValidationErrors<E>
        get() = DefaultValidationErrors(emptyList())
}

private fun <E> combineErrors(left: Map<String, List<E>>, right: Map<String, List<E>>) =
    (left.toList() + right.toList())
        .groupBy({ it.first }, { it.second })
        .mapValues { (_, values) -> values.flatten() }

