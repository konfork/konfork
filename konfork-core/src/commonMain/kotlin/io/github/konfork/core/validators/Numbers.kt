package io.github.konfork.core.validators

import io.github.konfork.core.ConstraintBuilder
import io.github.konfork.core.HintBuilder
import io.github.konfork.core.ValidationBuilder
import io.github.konfork.core.stringHint
import kotlin.math.abs
import kotlin.math.roundToLong

fun <C, T : Number, E> ValidationBuilder<C, T, E>.multipleOf(hint: HintBuilder<C, T, E>, factor: Number, epsilon: Double): ConstraintBuilder<C, T, E> {
    val factorAsDouble = factor.toDouble()
    require(factorAsDouble > 0) { "multipleOf requires the factor to be strictly larger than 0" }
    require(epsilon >= 0) { "multipleOf requires the epsilon to be 0 or larger than 0" }
    return addConstraint(hint, factor) {
        val division = it.toDouble() / factorAsDouble
        val diff = division - division.roundToLong()
        abs(diff) <= epsilon
    }
}

fun <C, T : Number> ValidationBuilder<C, T, String>.multipleOf(factor: Number, epsilon: Double): ConstraintBuilder<C, T, String> =
    multipleOf(stringHint("must be a multiple of '{0}'"), factor, epsilon)

fun <C, T : Number, E> ValidationBuilder<C, T, E>.maximum(hint: HintBuilder<C, T, E>, maximumInclusive: Number) =
    addConstraint(hint, maximumInclusive) { it.toDouble() <= maximumInclusive.toDouble() }

fun <C, T : Number> ValidationBuilder<C, T, String>.maximum(maximumInclusive: Number) =
    maximum(stringHint("must be at most '{0}'"), maximumInclusive)

fun <C, T : Number, E> ValidationBuilder<C, T, E>.exclusiveMaximum(hint: HintBuilder<C, T, E>, maximumExclusive: Number) =
    addConstraint(hint, maximumExclusive) { it.toDouble() < maximumExclusive.toDouble() }

fun <C, T : Number> ValidationBuilder<C, T, String>.exclusiveMaximum(maximumExclusive: Number) =
    exclusiveMaximum(stringHint("must be less than '{0}'"), maximumExclusive)

fun <C, T : Number, E> ValidationBuilder<C, T, E>.minimum(hint: HintBuilder<C, T, E>, minimumInclusive: Number) =
    addConstraint(hint, minimumInclusive) { it.toDouble() >= minimumInclusive.toDouble() }

fun <C, T : Number> ValidationBuilder<C, T, String>.minimum(minimumInclusive: Number) =
    minimum(stringHint("must be at least '{0}'"), minimumInclusive)

fun <C, T : Number, E> ValidationBuilder<C, T, E>.exclusiveMinimum(hint: HintBuilder<C, T, E>, minimumExclusive: Number) =
    addConstraint(hint, minimumExclusive) { it.toDouble() > minimumExclusive.toDouble() }

fun <C, T : Number> ValidationBuilder<C, T, String>.exclusiveMinimum(minimumExclusive: Number) =
    exclusiveMinimum(stringHint("must be greater than '{0}'"), minimumExclusive)
