package io.github.konfork.nbvcxz

import io.github.konfork.core.Validator
import io.github.konfork.nbvcxz.PasswordStrength.MEDIUM
import io.github.konfork.test.assertThat
import me.gosimple.nbvcxz.Nbvcxz
import org.junit.jupiter.api.Test

class ValidatorTest {

    private val mediumValidator = Validator {
        passwordStrength(MEDIUM)
    }

    @Test
    fun tooWeakPasswordShouldFail() {
        assertThat(mediumValidator, "Passw0rd!")
            .isInvalid()
            .withHint("is not a strong enough password")
    }

    @Test
    fun strongPasswordShouldPass() {
        assertThat(mediumValidator, "5fa83b7e1r39xfa8hmiz0")
            .isValid()
    }

    @Test
    fun forbiddenWordsPasswordShouldFail() {
        val validator = Validator {
            passwordStrength(PasswordStrength.STRONG, "wim", "kees")
        }

        assertThat(validator, "wimjankees")
            .isInvalid()
            .withHint("is not a strong enough password")
    }

    @Test
    fun validatorWithHomeMadeNbvcxzShouldWork() {
        val validator = Validator {
            passwordStrength(Nbvcxz(), MEDIUM)
        }

        assertThat(validator, "Passw0rd!")
            .isInvalid()
            .withHint("is not a strong enough password")
    }

    @Test
    fun validatorWithDoubleEntropy() {
        val validator = Validator {
            passwordStrength(Nbvcxz(), 19.932)
        }

        assertThat(validator, "Passw0rd!")
            .isInvalid()
            .withHint("is not a strong enough password")
    }
}
