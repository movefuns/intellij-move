package org.sui.lang.core.resolve.ref

import com.intellij.psi.PsiPolyVariantReference
import org.sui.lang.core.psi.MvElement
import org.sui.lang.core.psi.MvNamedElement
import org.sui.lang.core.psi.MvUseAlias
import org.sui.lang.core.psi.ext.moduleUseSpeck
import org.sui.lang.core.psi.ext.useItem

interface MvPolyVariantReference : PsiPolyVariantReference {

    override fun getElement(): MvElement

    override fun resolve(): MvNamedElement?

    fun resolveWithAliases(): MvNamedElement? {
        val resolved = this.resolve()
        if (resolved is MvUseAlias) {
            val useItem = resolved.useItem
            if (useItem != null) {
                return useItem.reference.resolve()
            }
            return resolved.moduleUseSpeck?.fqModuleRef?.reference?.resolve()
        }
        return resolved
    }

    fun multiResolve(): List<MvNamedElement>
}

interface MvPathReference : MvPolyVariantReference {
//    fun advancedResolve(): BoundElement<MvElement>? =
//        resolveWithAliases()?.let { BoundElement(it) }
}
