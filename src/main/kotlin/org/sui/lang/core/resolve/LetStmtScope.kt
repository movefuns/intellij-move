package org.sui.lang.core.resolve

import org.sui.lang.core.psi.MvElement
import org.sui.lang.core.psi.MvLetStmt
import org.sui.lang.core.psi.ext.ancestorOrSelf
import org.sui.lang.core.psi.ext.isMsl
import org.sui.lang.core.psi.ext.post

enum class LetStmtScope {
    NOT_MSL, EXPR_STMT, LET_STMT, LET_POST_STMT;
}

val MvElement.letStmtScope: LetStmtScope
    get() {
        if (!this.isMsl()) return LetStmtScope.NOT_MSL
        val letStmt = this.ancestorOrSelf<MvLetStmt>()
        return when {
            letStmt == null -> LetStmtScope.EXPR_STMT
            letStmt.post -> LetStmtScope.LET_POST_STMT
            else -> LetStmtScope.LET_STMT
        }
    }
