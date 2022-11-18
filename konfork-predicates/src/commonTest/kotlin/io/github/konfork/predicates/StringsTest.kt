package io.github.konfork.predicates

import io.github.konfork.predicates.cases.AllDigitCases
import io.github.konfork.predicates.cases.EmailTestCases
import io.github.konfork.predicates.cases.UuidTestCases
import io.github.konfork.predicates.util.assert
import kotlin.test.Test

class StringsTest {

    @Test
    fun isEmail() {
        val cases = EmailTestCases()
        assert(cases.valid, cases.invalid, ::isEmail)
    }

    @Test
    fun isUuid() {
        val cases = UuidTestCases()
        assert(cases.validUuid, cases.invalidUuid, ::isUuid)
        assert(cases.nilUuid, cases.invalidNill, ::isNilUuid)
        assert(cases.validV1, cases.invalidV1, isUuidVersion(1))
        assert(cases.validV2, cases.invalidV2, isUuidVersion(2))
        assert(cases.validV3, cases.invalidV3, isUuidVersion(3))
        assert(cases.validV4, cases.invalidV4, isUuidVersion(4))
        assert(cases.validV5, cases.invalidV5, isUuidVersion(5))
    }

    @Test
    fun isAllDigits() {
        val cases = AllDigitCases()
        assert(cases.valid, cases.invalid, ::isAllDigits)
    }
}
