package io.github.konfork.predicates.cases

class Mod11Cases {
    val invalidDown72 = listOf(
        "6319421",
        "324324234237",
        "99999999999999999"
    )
    val validDown72 = listOf(
        "6319429",
        "324324234235",
        "99999999999999992"
    )
    val invalidUp210 = listOf(
        "6319421",
        "324324234237",
        "99999999999999990",
        "3928444043",
    )
    val validUp210 = listOf(
        "6319427",
        "324324234233",
        "99999999999999999",
        "3928444049",
    )
}
