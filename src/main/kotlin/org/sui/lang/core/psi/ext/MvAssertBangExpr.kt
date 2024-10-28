package org.sui.lang.core.psi.ext

import com.intellij.psi.PsiElement
import org.sui.lang.MvElementTypes.IDENTIFIER
import org.sui.lang.core.psi.MvAssertMacroExpr

val MvAssertMacroExpr.identifier: PsiElement get() = this.findFirstChildByType(IDENTIFIER)!!