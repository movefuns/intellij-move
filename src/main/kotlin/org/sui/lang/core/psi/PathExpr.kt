package org.sui.lang.core.psi

interface PathExpr : MvElement {
    val path: MvPath get() = error("unreachable")
}
