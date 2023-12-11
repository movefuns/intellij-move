package org.sui.lang.core.resolve

import org.sui.lang.core.psi.MvElement
import org.sui.lang.core.psi.MvLetStmt
import org.sui.lang.core.psi.ext.ancestorOrSelf
import org.sui.lang.core.psi.ext.isMsl
import org.sui.lang.core.psi.ext.post

enum class MslLetScope {
    NONE, EXPR_STMT, LET_STMT, LET_POST_STMT;
}

val MvElement.mslLetScope: MslLetScope
    get() {
        if (!this.isMsl()) return MslLetScope.NONE
        val letStmt = this.ancestorOrSelf<MvLetStmt>()
        return when {
            letStmt == null -> MslLetScope.EXPR_STMT
            letStmt.post -> MslLetScope.LET_POST_STMT
            else -> MslLetScope.LET_STMT
        }
    }
