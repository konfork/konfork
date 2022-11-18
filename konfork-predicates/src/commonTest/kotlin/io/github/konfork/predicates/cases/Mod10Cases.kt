package io.github.konfork.predicates.cases

class Mod10Cases {
    val valid13 = listOf(
        "12343553",
        "9459035496",
        "0912890341289",
    )
    val invalid13 = listOf(
        "12343554",
        "9459035498",
        "0912890341280",
    )
    val valid12 = listOf(
        "12343555",
        "9459035494",
        "0912890341281",
    )
    val invalid12 = listOf(
        "12343554",
        "9459035498",
        "0912890341280",
    )
}
