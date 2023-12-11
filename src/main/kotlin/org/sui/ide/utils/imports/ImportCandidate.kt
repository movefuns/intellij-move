package org.sui.ide.utils.imports

import org.sui.ide.inspections.imports.ImportContext
import org.sui.ide.inspections.imports.qualifiedItems
import org.sui.lang.core.psi.MvQualNamedElement
import org.sui.lang.core.resolve.processQualItem
import org.sui.lang.core.types.ItemQualName
import org.sui.lang.index.MvNamedElementIndex
import org.sui.lang.moveProject
import org.sui.openapiext.common.isUnitTestMode

data class ImportCandidate(val element: MvQualNamedElement, val qualName: ItemQualName)

object ImportCandidateCollector {
    fun getImportCandidates(
        context: ImportContext,
        targetName: String,
        itemFilter: (MvQualNamedElement) -> Boolean = { true }
    ): List<ImportCandidate> {
        val (contextElement, itemVis) = context

        val project = contextElement.project
        val moveProject = contextElement.moveProject ?: return emptyList()
        val searchScope = moveProject.searchScope()

        val allItems = mutableListOf<MvQualNamedElement>()
        if (isUnitTestMode) {
            // always add current file in tests
            val currentFile = contextElement.containingFile as? org.sui.lang.MoveFile ?: return emptyList()
            val items = currentFile.qualifiedItems(targetName, itemVis)
            allItems.addAll(items)
        }

        MvNamedElementIndex
            .processElementsByName(project, targetName, searchScope) { element ->
                processQualItem(element, itemVis) {
                    val entryElement = it.element
                    if (entryElement !is MvQualNamedElement) return@processQualItem false
                    if (it.name == targetName) {
                        allItems.add(entryElement)
                    }
                    false
                }
                true
            }

        return allItems
            .filter(itemFilter)
            .mapNotNull { item -> item.qualName?.let { ImportCandidate(item, it) } }
    }
}
