package org.sui.lang.core.resolve.ref

import org.sui.lang.core.psi.MvItemSpecRef
import org.sui.lang.core.psi.MvNamedElement
import org.sui.lang.core.resolve.resolveLocalItem

class MvItemSpecRefReferenceImpl(element: MvItemSpecRef) : MvReferenceCached<MvItemSpecRef>(element) {

    override fun resolveInner(): List<MvNamedElement> {
        return resolveLocalItem(element, setOf(Namespace.SPEC_ITEM))
    }

}
