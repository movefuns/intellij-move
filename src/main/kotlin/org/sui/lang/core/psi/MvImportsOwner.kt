package org.sui.lang.core.psi

import com.intellij.psi.util.CachedValuesManager.getProjectPsiDependentCache
import org.sui.lang.core.psi.ext.addressRef
import org.sui.lang.core.psi.ext.isSelf

interface MvImportsOwner : MvElement {
    val useStmtList: List<MvUseStmt>
}

fun MvImportsOwner.items(): Sequence<MvElement> {
    return generateSequence(firstChild) { it.nextSibling }
        .filterIsInstance<MvElement>()
        .filter { it !is MvAttr }
}

fun MvImportsOwner.moduleUseItems(): List<MvNamedElement> =
    listOf(
        moduleUseSpecksNoAliases(),
        moduleUseSpecksAliases(),
        selfModuleUseItemNoAliases(),
        selfModuleUseItemAliases(),
//        mixUseSpecksNoAliases(),
    ).flatten()

//fun MvImportsOwner.mixUseSpecksNoAliases(): List<MvMixUseItem> =
//    mixUseSpecks().mapNotNull { it.mixUseItemGroup?.mixUseItemList}.flatten()

fun MvImportsOwner.moduleUseSpecksNoAliases(): List<MvModuleUseSpeck> =
    moduleUseSpecks()
        .filter { it.useAlias == null }

fun MvImportsOwner.moduleUseSpecksAliases(): List<MvUseAlias> =
    moduleUseSpecks().mapNotNull { it.useAlias }


private fun MvImportsOwner.moduleUseSpecks(): List<MvModuleUseSpeck> {
    return getProjectPsiDependentCache(this) {
        useStmtList.mapNotNull { it.moduleUseSpeck }
    }
}

//private fun MvImportsOwner.mixUseSpecks(): List<MvMixUseSpeck> {
//    return getProjectPsiDependentCache(this) {
//        useStmtList.mapNotNull { it.mixUseSpeck }
//    }
//}

fun MvImportsOwner.psiUseItems(): List<MvUseItem> {
    return getProjectPsiDependentCache(this) { importsOwner ->
//        val mixItems = importsOwner.useStmtList.mapNotNull { it.mixUseSpeck }.flatMap {
//            it.mixUseItemGroup?.mixUseItemList?.mapNotNull { it.useItem } ?: emptyList()
//        }

        importsOwner
            .useStmtList
            .mapNotNull { it.itemUseSpeck }
            .flatMap {
                val item = it.useItem
                if (item != null) {
                    listOf(item)
                } else
                    it.useItemGroup?.useItemList.orEmpty()
            }
//            .plus(mixItems)

    }
}

fun MvImportsOwner.allUseItems(): List<MvNamedElement> =
    listOf(
        useItemsNoAliases(),
        useItemsAliases(),
    ).flatten()

fun MvImportsOwner.useItemsNoAliases(): List<MvUseItem> =
    psiUseItems()
        .filter { !it.isSelf }
        .filter { it.useAlias == null }

fun MvImportsOwner.useItemsAliases(): List<MvUseAlias> =
    psiUseItems()
        .filter { !it.isSelf }
        .mapNotNull { it.useAlias }

fun MvImportsOwner.selfModuleUseItemNoAliases(): List<MvUseItem> =
    psiUseItems()
        .filter { it.isSelf && it.useAlias == null }

fun MvImportsOwner.selfModuleUseItemAliases(): List<MvUseAlias> =
    psiUseItems()
        .filter { it.isSelf }
        .mapNotNull { it.useAlias }

fun MvImportsOwner.shortestPathText(item: MvNamedElement): String? {
    val itemName = item.name ?: return null
    // local
    if (this == item.containingImportsOwner) return itemName

    for (useItem in this.useItemsNoAliases()) {
        val importedItem = useItem.reference.resolve() ?: continue
        if (importedItem == item) {
            return itemName
        }
    }
    val module = item.containingModule ?: return null
    val moduleName = module.name ?: return null
    for (moduleImport in this.moduleUseSpecksNoAliases()) {
        val importedModule = moduleImport.fqModuleRef?.reference?.resolve() ?: continue
        if (importedModule == module) {
            return "$moduleName::$itemName"
        }
    }
    val addressName = module.addressRef()?.text ?: return null
    return "$addressName::$moduleName::$itemName"
}
