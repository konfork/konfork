package io.github.konfork.predicates

import io.github.konfork.predicates.cases.Mod10Cases
import io.github.konfork.predicates.util.assert
import kotlin.test.Test

class CheckDigitsTests {
    @Test
    fun isMod10Test() {
        val cases = Mod10Cases()
        assert(cases.valid13, cases.invalid13, isMod10(1, 3))
    }
}
