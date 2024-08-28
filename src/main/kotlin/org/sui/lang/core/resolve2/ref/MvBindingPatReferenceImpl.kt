package org.sui.lang.core.resolve2.ref

import com.intellij.psi.PsiElement
import org.sui.lang.core.psi.*
import org.sui.lang.core.resolve.collectResolveVariants
import org.sui.lang.core.resolve.ref.MvPolyVariantReferenceCached
import org.sui.lang.core.resolve2.processBindingPatResolveVariants

class MvBindingPatReferenceImpl(
    element: MvBindingPat
) : MvPolyVariantReferenceCached<MvBindingPat>(element) {

    override fun multiResolveInner(): List<MvNamedElement> =
        collectResolveVariants(element.referenceName) {
            processBindingPatResolveVariants(element, false, it)
        }

    override fun handleElementRename(newName: String): PsiElement {
        if (element.parent !is MvFieldPat) return super.handleElementRename(newName)
        val newFieldPat = element.project.psiFactory.fieldPatFull(newName, element.text)
        return element.replace(newFieldPat)
    }
}