package org.sui.lang.core.resolve2.ref

import org.sui.lang.core.psi.MvNamedElement
import org.sui.lang.core.psi.MvStructLitField
import org.sui.lang.core.resolve.collectResolveVariants
import org.sui.lang.core.resolve.ref.MvPolyVariantReferenceCached
import org.sui.lang.core.resolve.ref.ResolveCacheDependency
import org.sui.lang.core.resolve2.processStructLitFieldResolveVariants

class MvStructLitFieldReferenceImpl(
    field: MvStructLitField
) : MvPolyVariantReferenceCached<MvStructLitField>(field) {

    override val cacheDependency: ResolveCacheDependency get() = ResolveCacheDependency.LOCAL_AND_RUST_STRUCTURE

    override fun multiResolveInner(): List<MvNamedElement> =
        collectResolveVariants(element.referenceName) {
            processStructLitFieldResolveVariants(element, false, it)
        }
}