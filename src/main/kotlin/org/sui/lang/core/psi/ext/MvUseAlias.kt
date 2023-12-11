package org.sui.lang.core.psi.ext

import com.intellij.lang.ASTNode
import org.sui.lang.core.psi.MvModuleUseSpeck
import org.sui.lang.core.psi.MvUseAlias
import org.sui.lang.core.psi.MvUseItem
import org.sui.lang.core.psi.impl.MvNameIdentifierOwnerImpl

val MvUseAlias.useItem: MvUseItem? get() = this.parent as? MvUseItem

val MvUseAlias.moduleUseSpeck: MvModuleUseSpeck? get() = this.parent as? MvModuleUseSpeck

abstract class MvUseAliasMixin(node: ASTNode) : MvNameIdentifierOwnerImpl(node),
                                                MvUseAlias
