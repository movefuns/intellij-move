package org.sui.lang.core.psi.ext

import com.intellij.lang.ASTNode
import org.sui.lang.core.psi.MvElementImpl
import org.sui.lang.core.psi.MvItemSpecRef
import org.sui.lang.core.resolve.ref.MvItemSpecRefReferenceImpl
import org.sui.lang.core.resolve.ref.MvPolyVariantReference


abstract class MvItemSpecRefMixin(node: ASTNode) : MvElementImpl(node), MvItemSpecRef {

    override fun getReference(): MvPolyVariantReference? = MvItemSpecRefReferenceImpl(this)
}
