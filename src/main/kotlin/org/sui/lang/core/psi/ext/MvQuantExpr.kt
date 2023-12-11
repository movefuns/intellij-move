package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.*

interface MvQuantBindingsOwner: MvElement {
    val quantBindings: MvQuantBindings?
}

interface MvQuantExpr : MvQuantBindingsOwner {
    val expr: MvExpr?
    val quantWhere: MvQuantWhere?
}

val MvQuantBindingsOwner.bindings: List<MvBindingPat>
    get() = quantBindings?.quantBindingList.orEmpty().mapNotNull { it.bindingPat }

val MvQuantBinding.bindingPat: MvBindingPat
    get() = when (this) {
        is MvTypeQuantBinding -> this.bindingPat
        is MvRangeQuantBinding -> this.bindingPat
        else -> error("unreachable")
    }
