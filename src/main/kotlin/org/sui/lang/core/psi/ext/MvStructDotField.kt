package org.sui.lang.core.psi.ext

import com.intellij.lang.ASTNode
import org.sui.lang.core.psi.MvElementImpl
import org.sui.lang.core.psi.MvNamedElement
import org.sui.lang.core.psi.MvStructDotField
import org.sui.lang.core.resolve.ref.MvPolyVariantReference
import org.sui.lang.core.resolve.ref.MvReferenceCached
import org.sui.lang.core.resolve.ref.MvStructDotFieldReferenceElement
import org.sui.lang.core.resolve.ref.Namespace
import org.sui.lang.core.resolve.resolveLocalItem

class MvStructDotFieldReferenceImpl(
    element: MvStructDotFieldReferenceElement
) : MvReferenceCached<MvStructDotFieldReferenceElement>(element) {

    override fun resolveInner(): List<MvNamedElement> {
        return resolveLocalItem(element, setOf(Namespace.DOT_FIELD))
    }
}

abstract class MvStructDotFieldMixin(node: ASTNode) : MvElementImpl(node),
                                                      MvStructDotField {
    override fun getReference(): MvPolyVariantReference {
        return MvStructDotFieldReferenceImpl(this)
    }
}
