package org.sui.lang.core.resolve.ref

import org.sui.lang.core.psi.*
import org.sui.lang.core.psi.ext.isSelfModuleRef
import org.sui.lang.core.psi.ext.itemUseSpeck
import org.sui.lang.core.resolve.processModuleRef
import org.sui.lang.core.resolve.resolveLocalItem
import org.sui.stdext.wrapWithList

class MvModuleReferenceImpl(
    element: MvModuleRef,
) : MvPolyVariantReferenceCached<MvModuleRef>(element) {

    override fun multiResolveInner(): List<MvNamedElement> {
        if (element.isSelfModuleRef) return element.containingModule.wrapWithList()

        check(element !is MvFQModuleRef) {
            "That element has different reference item"
        }

        // process preload module
        val referenceName = element.referenceName
        var module: MvModule? = null
        processModuleRef(element) {
            if (it.name == referenceName) {
                module = it.element
                true
            } else {
                false
            }
        }
        if (null != module) return module.wrapWithList()

        val resolved = resolveLocalItem(element, setOf(Namespace.MODULE)).firstOrNull()
        if (resolved is MvUseAlias) {
            return resolved.wrapWithList()
        }
        val moduleRef = when {
            resolved is MvUseItem && resolved.text == "Self" -> resolved.itemUseSpeck.fqModuleRef
            resolved is MvModuleUseSpeck -> resolved.fqModuleRef
//            resolved is MvMixUseItem -> {
//                if (resolved.mixPathItem.moduleRef.text == element.text) {
//                    val elementList = MvNamedElementIndex.getElementsByName(
//                        element.project, element.text, GlobalSearchScope.allScope(element.project)
//                    )
//                    val filter = elementList.filterIsInstance<MvModule>()
//                        .filter { it.addressRef?.text == resolved.ancestorStrict<MvMixUseSpeck>()?.addressRef?.text }
//                    return listOf(filter.first())
//                }
//                return emptyList()
//            }
            else -> return emptyList()
        }
        return moduleRef?.reference?.resolve().wrapWithList()
    }
}
