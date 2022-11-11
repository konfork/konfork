package io.github.konfork.core.validators

import io.github.konfork.core.Validator
import io.github.konfork.core.validators.EnumsTest.TCPPacket.*
import io.github.konfork.test.assertThat
import kotlin.test.Test

class EnumsTest {

    @Test
    fun stringEnumConstraint() {
        val validator = Validator { enum("OK", "CANCEL") }

        assertThat(validator, "OK")
            .isValid()

        assertThat(validator, "CANCEL")
            .isValid()

        assertThat(validator, "??")
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be one of: 'OK', 'CANCEL'")

        assertThat(validator, "")
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be one of: 'OK', 'CANCEL'")
    }

    enum class TCPPacket {
        SYN, ACK, SYNACK
    }

    @Test
    fun kotlinEnumConstraint() {
        val validator = Validator { enum(SYN, ACK) }

        assertThat(validator, SYN)
            .isValid()

        assertThat(validator, ACK)
            .isValid()

        assertThat(validator, SYNACK)
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be one of: 'SYN', 'ACK'")
    }

    @Test
    fun kotlinEnumStringConstraint() {
        val validator = Validator { enum<TCPPacket>() }

        assertThat(validator, "SYN")
            .isValid()

        assertThat(validator, "ACK")
            .isValid()

        assertThat(validator, "SYNACK")
            .isValid()

        assertThat(validator, "ASDF")
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be one of: 'SYN', 'ACK', 'SYNACK'")
    }

    @Test
    fun kotlinEnumStringWithContextConstraint() {
        val validator = Validator<Int, String> { enum<Int, TCPPacket, String>(enumHintBuilder()) }

        assertThat(validator, 1, "SYN")
            .isValid()

        assertThat(validator, 2, "ACK")
            .isValid()

        assertThat(validator, 3, "SYNACK")
            .isValid()

        assertThat(validator,4, "ASDF")
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be one of: 'SYN', 'ACK', 'SYNACK'")
    }
}
