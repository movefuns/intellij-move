package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvElseBlock
import org.sui.lang.core.psi.MvExpr
import org.sui.lang.core.psi.MvIfExpr

val MvIfExpr.tailExpr: MvExpr?
    get() {
        val codeBlock = this.codeBlock
        if (codeBlock != null) return codeBlock.returningExpr
        return this.inlineBlock?.expr
    }

val MvElseBlock.tailExpr: MvExpr?
    get() {
        val elseCodeBlock = this.codeBlock
        if (elseCodeBlock != null) return elseCodeBlock.returningExpr
        return this.inlineBlock?.expr
    }

val MvIfExpr.elseTailExpr: MvExpr?
    get() {
        val elseBlock = this.elseBlock ?: return null
        val elseCodeBlock = elseBlock.codeBlock
        if (elseCodeBlock != null) return elseCodeBlock.returningExpr
        return elseBlock.inlineBlock?.expr
    }
