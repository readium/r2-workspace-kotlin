package org.readium.r2.navigator

enum class Disjunction {
    A,
    B,
    Both
}

fun count(disjunction: Disjunction) : Int {
    if (disjunction == Disjunction.A || disjunction == Disjunction.B)
        return 1
    return 2
}