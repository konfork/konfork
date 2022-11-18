package io.github.konfork.predicates

import io.github.konfork.predicates.cases.EanCases
import io.github.konfork.predicates.cases.Mod10Cases
import io.github.konfork.predicates.util.assert
import kotlin.test.Test

class CheckDigitsTests {
    @Test
    fun isMod10Test() {
        val cases = Mod10Cases()
        assert(cases.valid13, cases.invalid13, isMod10(1, 3))
        assert(cases.valid12, cases.invalid12, isMod10(1, 2))
    }

    @Test
    fun isEanTest() {
        val cases = EanCases()
        assert(cases.valid8, cases.invalid8, isEan(8))
        assert(cases.valid12, cases.invalid12, isEan(12))
        assert(cases.valid13, cases.invalid13, isEan(13))
        assert(cases.valid14, cases.invalid14, isEan(14))
        assert(cases.valid18, cases.invalid18, isEan(18))
    }
}
