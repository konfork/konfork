package io.github.konfork.predicates

import io.github.konfork.predicates.cases.*
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

    @Test
    fun isLuhnTest() {
        val cases = LuhnCases()
        assert(cases.valid, cases.invalid, ::isLuhn)
    }

    @Test
    fun isMod11Test() {
        val cases = Mod11Cases()
        assert(cases.validDown72, cases.invalidDown72, isMod11(7, 2))
        assert(cases.validUp210, cases.invalidUp210, isMod11(2, 10))
    }

    @Test
    fun isIsbnTest() {
        val cases = IsbnCases()
        assert(cases.validIsbn10, cases.invalidIsbn10, ::isIsbn10)
        assert(cases.validIsbn13, cases.invalidIsbn13, ::isIsbn13)
        assert(cases.validIsbn, cases.invalidIsbn, ::isIsbn)
    }
}
