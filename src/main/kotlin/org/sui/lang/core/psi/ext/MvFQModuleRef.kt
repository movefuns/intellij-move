package org.sui.lang.core.psi.ext

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.sui.lang.MvElementTypes
import org.sui.lang.core.psi.MvElementImpl
import org.sui.lang.core.psi.MvFQModuleRef
import org.sui.lang.core.resolve.ref.MvFQModuleReference
import org.sui.lang.core.resolve.ref.MvFQModuleReferenceImpl

abstract class MvFQModuleRefMixin(node: ASTNode) : MvElementImpl(node),
                                                   MvFQModuleRef {
    override val identifier: PsiElement?
        get() = findChildByType(MvElementTypes.IDENTIFIER)

    override fun getReference(): MvFQModuleReference {
        return MvFQModuleReferenceImpl(this)
    }

    override fun getMul(): PsiElement? = null
}
