package org.sui.lang.core.resolve.ref

import org.sui.lang.core.psi.*
import org.sui.lang.core.psi.ext.isSelf
import org.sui.lang.core.psi.ext.itemUseSpeck
import org.sui.lang.core.resolve.resolveLocalItem
import org.sui.stdext.wrapWithList

class MvModuleReferenceImpl(
    element: MvModuleRef,
) : MvPolyVariantReferenceCached<MvModuleRef>(element) {

    override fun multiResolveInner(): List<MvNamedElement> {
        if (element.isSelf) return element.containingModule.wrapWithList()

        check(element !is MvFQModuleRef) {
            "That element has different reference item"
        }

        val resolved = resolveLocalItem(element, setOf(Namespace.MODULE)).firstOrNull()
        if (resolved is MvUseAlias) {
            return resolved.wrapWithList()
        }
        val moduleRef = when {
            resolved is MvUseItem && resolved.text == "Self" -> resolved.itemUseSpeck.fqModuleRef
            resolved is MvModuleUseSpeck -> resolved.fqModuleRef
            else -> return emptyList()
        }
        return moduleRef?.reference?.resolve().wrapWithList()
    }
}
