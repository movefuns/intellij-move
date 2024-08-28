package org.sui.lang.core.psi.ext

import com.intellij.lang.ASTNode
import org.sui.lang.core.psi.MvUseAlias
import org.sui.lang.core.psi.MvUseSpeck
import org.sui.lang.core.psi.impl.MvNameIdentifierOwnerImpl

val MvUseAlias.parentUseSpeck: MvUseSpeck get() = this.parent as MvUseSpeck

abstract class MvUseAliasMixin(node: ASTNode) : MvNameIdentifierOwnerImpl(node),
                                                MvUseAlias
