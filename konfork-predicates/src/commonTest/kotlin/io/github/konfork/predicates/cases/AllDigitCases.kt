package io.github.konfork.predicates.cases

class AllDigitCases {
    val valid = listOf(
        "1212445767909",
        "08423612345",
        "09876532940430439127216237384943943",
        "",
        "9",
    )
    val invalid = listOf(
        " 2352345",
        "456456 ",
        "924389243789234X",
        "adsasfas",
        "4596549834587a",
        "85348 5734"
    )
}
