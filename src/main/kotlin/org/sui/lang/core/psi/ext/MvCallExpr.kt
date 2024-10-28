package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvCallExpr
import org.sui.lang.core.psi.MvElement
import org.sui.lang.core.psi.MvExpr
import org.sui.lang.core.psi.MvPath
import org.sui.lang.core.psi.MvValueArgument
import org.sui.lang.core.psi.MvValueArgumentList

interface MvCallable: MvElement {
    val valueArgumentList: MvValueArgumentList?
}

val MvCallable.valueArguments: List<MvValueArgument>
    get() =
        this.valueArgumentList?.valueArgumentList.orEmpty()

val MvCallable.argumentExprs: List<MvExpr?> get() = this.valueArguments.map { it.expr }

