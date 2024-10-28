package org.sui.lang.core.resolve.ref

import com.intellij.psi.PsiPolyVariantReference
import org.sui.lang.core.psi.MvElement
import org.sui.lang.core.psi.MvNamedElement
import org.sui.lang.core.psi.MvUseAlias
import org.sui.lang.core.psi.ext.parentUseSpeck
import org.sui.lang.core.resolve2.ref.resolveAliases

interface MvPolyVariantReference : PsiPolyVariantReference {

    override fun getElement(): MvElement

    override fun resolve(): MvNamedElement?

    fun multiResolve(): List<MvNamedElement>

    fun resolveFollowingAliases(): MvNamedElement? = this.resolve()?.let { resolveAliases(it) }
}

//interface MvPathReference : MvPolyVariantReference {

//    fun multiResolveIfVisible(): List<MvElement> = multiResolve()
//
//    fun rawMultiResolve(): List<RsPathResolveResult<MvElement>> =
//        multiResolve().map { RsPathResolveResult(it, isVisible = true) }
//}

interface MvPath2Reference: MvPolyVariantReference {
//    fun multiResolveIfVisible(): List<MvElement> = multiResolve()

//    fun rawMultiResolve(): List<RsPathResolveResult<MvElement>>
//        multiResolve().map { RsPathResolveResult(it, isVisible = true) }
    
}
