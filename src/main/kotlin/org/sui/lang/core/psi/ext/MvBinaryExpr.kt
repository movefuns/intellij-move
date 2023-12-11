package org.sui.lang.core.psi.ext

import com.intellij.lang.ASTNode
import org.sui.lang.MvElementTypes.BINARY_OP
import org.sui.lang.core.psi.MvBinaryExpr
import org.sui.lang.core.psi.MvElementImpl

abstract class MvBinaryExprMixin(node: ASTNode): MvElementImpl(node),
                                                 MvBinaryExpr {
    override fun toString(): String {
        val op = node.findChildByType(BINARY_OP)?.text ?: ""
        return "${javaClass.simpleName}(${node.elementType}[$op])"
    }
}
