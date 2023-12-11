package org.sui.lang.core.psi.ext

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.sui.lang.MvElementTypes.R_BRACE
import org.sui.lang.core.psi.MvCodeBlock
import org.sui.lang.core.psi.MvElementImpl
import org.sui.lang.core.psi.MvExpr
import org.sui.lang.core.psi.MvLetStmt

val MvCodeBlock.returningExpr: MvExpr? get() = this.expr

val MvCodeBlock.rightBrace: PsiElement? get() = this.findLastChildByType(R_BRACE)

val MvCodeBlock.letStmts: List<MvLetStmt> get() = stmtList.filterIsInstance<MvLetStmt>()

abstract class MvCodeBlockMixin(node: ASTNode) : MvElementImpl(node), MvCodeBlock {

//    override val useStmts: List<MvUseStmt> get() = useStmtList
}
