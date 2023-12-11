package org.sui.lang.core.resolve.ref

import org.sui.lang.core.psi.MvNamedElement
import org.sui.lang.core.psi.MvStructLitField
import org.sui.lang.core.psi.MvStructPatField
import org.sui.lang.core.resolve.resolveLocalItem

class MvStructFieldReferenceImpl(
    element: MvMandatoryReferenceElement
) : MvReferenceCached<MvMandatoryReferenceElement>(element) {

    override fun resolveInner() = resolveLocalItem(element, setOf(Namespace.STRUCT_FIELD))
}

class MvStructLitShorthandFieldReferenceImpl(
    element: MvStructLitField,
) : MvReferenceCached<MvStructLitField>(element) {

    override fun resolveInner(): List<MvNamedElement> {
        return listOf(
            resolveLocalItem(element, setOf(Namespace.STRUCT_FIELD)),
            resolveLocalItem(element, setOf(Namespace.NAME))
        ).flatten()
    }
}

class MvStructPatShorthandFieldReferenceImpl(
    element: MvStructPatField
) : MvReferenceCached<MvStructPatField>(element) {

    override fun resolveInner() = resolveLocalItem(element, setOf(Namespace.STRUCT_FIELD))
}
