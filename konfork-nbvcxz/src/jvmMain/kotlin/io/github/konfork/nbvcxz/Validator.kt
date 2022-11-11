package io.github.konfork.nbvcxz

import io.github.konfork.core.ConstraintBuilder
import io.github.konfork.core.HintBuilder
import io.github.konfork.core.Specification
import io.github.konfork.core.stringHint
import io.github.konfork.nbvcxz.PasswordStrength.STRONG
import me.gosimple.nbvcxz.Nbvcxz
import me.gosimple.nbvcxz.resources.ConfigurationBuilder
import me.gosimple.nbvcxz.resources.DictionaryBuilder
import kotlin.math.log2

enum class PasswordStrength(
    val guesses: Long,
) {
    VERY_WEAK(0),
    WEAK(1e3.toLong()),
    MEDIUM(1e6.toLong()),
    STRONG(1e8.toLong()),
    VERY_STRONG(1e10.toLong());

    val entropy: Double by lazy { log2(guesses.toDouble()) }
}

fun <C, E> Specification<C, String, E>.passwordStrength(
    hintBuilder: HintBuilder<C, String, E>,
    nbvcxz: Nbvcxz,
    minEntropy: Double,
): ConstraintBuilder<C, String, E> =
    addConstraint(hintBuilder) {
        nbvcxz.estimate(it).entropy >= minEntropy
    }

fun <C> Specification<C, String, String>.passwordStrength(
    nbvcxz: Nbvcxz,
    minEntropy: Double,
): ConstraintBuilder<C, String, String> =
    passwordStrength(passwordHint(), nbvcxz, minEntropy)

fun <C, E> Specification<C, String, E>.passwordStrength(
    hintBuilder: HintBuilder<C, String, E>,
    nbvcxz: Nbvcxz,
    minStrength: PasswordStrength,
): ConstraintBuilder<C, String, E> =
    passwordStrength(hintBuilder, nbvcxz, minStrength.entropy)

fun <C> Specification<C, String, String>.passwordStrength(
    nbvcxz: Nbvcxz,
    minStrength: PasswordStrength,
): ConstraintBuilder<C, String, String> =
    passwordStrength(passwordHint(), nbvcxz, minStrength)

fun <C, E> Specification<C, String, E>.passwordStrength(
    hintBuilder: HintBuilder<C, String, E>,
    minStrength: PasswordStrength = STRONG,
    vararg forbiddenWords: String,
): ConstraintBuilder<C, String, E> =
    passwordStrength(hintBuilder, createNbvcxz(forbiddenWords), minStrength.entropy)

fun <C> Specification<C, String, String>.passwordStrength(
    minStrength: PasswordStrength = STRONG,
    vararg forbiddenWords: String,
): ConstraintBuilder<C, String, String> =
    passwordStrength(passwordHint(), minStrength, *forbiddenWords)

private fun <C> passwordHint(): HintBuilder<C, String, String> =
    stringHint("is not a strong enough password")

private fun createNbvcxz(forbiddenWords: Array<out String>): Nbvcxz {
    val dictionary = DictionaryBuilder()
        .setDictionaryName("exclude")
        .setExclusion(true)
        .addWords(forbiddenWords.toList(), 0)
        .createDictionary()

    val configuration = ConfigurationBuilder()
        .setDictionaries(ConfigurationBuilder.getDefaultDictionaries() + dictionary)
        .createConfiguration()

    return Nbvcxz(configuration)
}
