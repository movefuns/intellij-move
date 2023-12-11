package org.sui.lang.core.psi.impl

import com.intellij.lang.ASTNode
import org.sui.lang.core.psi.MvElementImpl
import org.sui.lang.core.resolve.ref.MvPolyVariantReference
import org.sui.lang.core.resolve.ref.MvReferenceElement

abstract class MvReferenceElementImpl(node: ASTNode) : MvElementImpl(node),
                                                       MvReferenceElement {
    abstract override fun getReference(): MvPolyVariantReference
}
