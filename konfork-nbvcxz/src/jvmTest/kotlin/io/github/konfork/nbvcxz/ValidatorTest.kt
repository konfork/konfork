package io.github.konfork.nbvcxz

import io.github.konfork.core.Validation
import io.github.konfork.nbvcxz.PasswordStrength.MEDIUM
import io.github.konfork.test.assertThat
import me.gosimple.nbvcxz.Nbvcxz
import org.junit.jupiter.api.Test

class ValidatorTest {

    private val mediumValidation = Validation {
        passwordStrength(MEDIUM)
    }

    @Test
    fun tooWeakPasswordShouldFail() {
        assertThat(mediumValidation, "Passw0rd!")
            .isInvalid()
            .withHint("is not a strong enough password")
    }

    @Test
    fun strongPasswordShouldPass() {
        assertThat(mediumValidation, "5fa83b7e1r39xfa8hmiz0")
            .isValid()
    }

    @Test
    fun forbiddenWordsPasswordShouldFail() {
        val validation = Validation {
            passwordStrength(PasswordStrength.STRONG, "wim", "kees")
        }

        assertThat(validation, "wimjankees")
            .isInvalid()
            .withHint("is not a strong enough password")
    }

    @Test
    fun validationWithHomeMadeNbvcxzShouldWork() {
        val validation = Validation {
            passwordStrength(Nbvcxz(), MEDIUM)
        }

        assertThat(validation, "Passw0rd!")
            .isInvalid()
            .withHint("is not a strong enough password")
    }

    @Test
    fun validationWithDoubleEntropy() {
        val validation = Validation {
            passwordStrength(Nbvcxz(), 19.932)
        }

        assertThat(validation, "Passw0rd!")
            .isInvalid()
            .withHint("is not a strong enough password")
    }
}
