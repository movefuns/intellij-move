package org.sui.lang.core.resolve.ref

import org.sui.lang.core.psi.MvFQModuleRef
import org.sui.lang.core.psi.MvModule
import org.sui.lang.core.psi.MvNamedElement
import org.sui.lang.core.resolve.processFQModuleRef
import org.sui.stdext.wrapWithList

interface MvFQModuleReference : MvPolyVariantReference

class MvFQModuleReferenceImpl(
    element: MvFQModuleRef,
) : MvReferenceCached<MvFQModuleRef>(element), MvFQModuleReference {

    override val cacheDependency: ResolveCacheDependency get() = ResolveCacheDependency.LOCAL_AND_RUST_STRUCTURE

    override fun resolveInner(): List<MvNamedElement> {
        val referenceName = element.referenceName ?: return emptyList()
        var resolved: MvModule? = null
        processFQModuleRef(element, referenceName) {
            if (it.name == referenceName) {
                resolved = it.element
                true
            } else {
                false
            }
        }
        return resolved.wrapWithList()
    }
}
