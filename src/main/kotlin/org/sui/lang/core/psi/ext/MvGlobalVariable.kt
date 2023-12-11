package org.sui.lang.core.psi.ext

import com.intellij.lang.ASTNode
import org.sui.ide.MoveIcons
import org.sui.lang.core.psi.MvGlobalVariableStmt
import org.sui.lang.core.psi.impl.MvNameIdentifierOwnerImpl
import javax.swing.Icon

abstract class MvGlobalVariableMixin(node: ASTNode): MvNameIdentifierOwnerImpl(node),
                                                     MvGlobalVariableStmt {

    override fun getIcon(flags: Int): Icon? = MoveIcons.BINDING
                                                     }