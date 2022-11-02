package io.github.konfork.core.validators

import io.github.konfork.core.Validation
import io.github.konfork.core.assertThat
import io.github.konfork.core.validators.EnumsTest.TCPPacket.*
import kotlin.test.Test

class EnumsTest {

    @Test
    fun stringEnumConstraint() {
        val validation = Validation { enum("OK", "CANCEL") }

        assertThat(validation, "OK")
            .isValid()

        assertThat(validation, "CANCEL")
            .isValid()

        assertThat(validation, "??")
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be one of: 'OK', 'CANCEL'")

        assertThat(validation, "")
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be one of: 'OK', 'CANCEL'")
    }

    enum class TCPPacket {
        SYN, ACK, SYNACK
    }

    @Test
    fun kotlinEnumConstraint() {
        val validation = Validation { enum(SYN, ACK) }

        assertThat(validation, SYN)
            .isValid()

        assertThat(validation, ACK)
            .isValid()

        assertThat(validation, SYNACK)
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be one of: 'SYN', 'ACK'")
    }

    @Test
    fun kotlinEnumStringConstraint() {
        val validation = Validation { enum<TCPPacket>() }

        assertThat(validation, "SYN")
            .isValid()

        assertThat(validation, "ACK")
            .isValid()

        assertThat(validation, "SYNACK")
            .isValid()

        assertThat(validation, "ASDF")
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be one of: 'SYN', 'ACK', 'SYNACK'")
    }

    @Test
    fun kotlinEnumStringWithContextConstraint() {
        val validation = Validation<Int, String> { enum<Int, TCPPacket, String>(enumHintBuilder()) }

        assertThat(validation, 1, "SYN")
            .isValid()

        assertThat(validation, 2, "ACK")
            .isValid()

        assertThat(validation, 3, "SYNACK")
            .isValid()

        assertThat(validation,4, "ASDF")
            .isInvalid()
            .withErrorCount(1)
            .withHint("must be one of: 'SYN', 'ACK', 'SYNACK'")
    }
}
