package org.sui.lang.core.psi.ext

import com.intellij.lang.ASTNode
import org.sui.lang.core.psi.MvElementImpl
import org.sui.lang.core.psi.MvFQModuleRef
import org.sui.lang.core.psi.MvModuleRef
import org.sui.lang.core.psi.containingModule
import org.sui.lang.core.resolve.ref.MvModuleReferenceImpl
import org.sui.lang.core.resolve.ref.MvPolyVariantReference

val MvModuleRef.isSelfModuleRef: Boolean
    get() =
        this !is MvFQModuleRef
                && this.referenceName == "Self"
                && this.containingModule != null

abstract class MvModuleRefMixin(node: ASTNode) : MvElementImpl(node), MvModuleRef {

    override fun getReference(): MvPolyVariantReference? = MvModuleReferenceImpl(this)
}

//abstract class MvImportedModuleRefMixin(node: ASTNode) : MvReferenceElementImpl(node),
//                                                           MvImportedModuleRef {
//    override val identifier: PsiElement
//        get() {
//            throw NotImplementedError()
////            if (this is MvImportedModuleRef) return this.identifier
////            if (this is MvFQModuleRef) return this.identifier
//////            if (self is MvImportedModuleRef
//////                || self is MvFQModuleRef) return self.identifier
////            return null
//        }
//
//    override fun getReference(): MvReference {
//        return MvModuleReferenceImpl(this)
//    }
//
//    override val isUnresolved: Boolean
//        get() = super<MvReferenceElementImpl>.isUnresolved
//}
