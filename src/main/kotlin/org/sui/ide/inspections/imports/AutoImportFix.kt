package org.sui.ide.inspections.imports

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.sui.ide.inspections.DiagnosticFix
import org.sui.ide.utils.imports.ImportCandidate
import org.sui.ide.utils.imports.ImportCandidateCollector
import org.sui.ide.utils.imports.import
import org.sui.lang.core.psi.*
import org.sui.lang.core.psi.ext.ancestorStrict
import org.sui.lang.core.psi.ext.asSmartPointer
import org.sui.lang.core.psi.ext.hasAncestor
import org.sui.lang.core.psi.ext.importCandidateNamespaces
import org.sui.lang.core.resolve.ContextScopeInfo
import org.sui.lang.core.resolve.letStmtScope
import org.sui.lang.core.resolve.ref.MvReferenceElement
import org.sui.lang.core.resolve.ref.Namespace
import org.sui.lang.core.resolve.ref.Visibility
import org.sui.openapiext.runWriteCommandAction


class AutoImportFix(element: MvReferenceElement) : DiagnosticFix<MvReferenceElement>(element),
    HighPriorityAction {

    private var isConsumed: Boolean = false

    override fun getFamilyName() = NAME
    override fun getText() = familyName

    override fun stillApplicable(
        project: Project,
        file: PsiFile,
        element: MvReferenceElement
    ): Boolean =
        !isConsumed

    override fun invoke(project: Project, file: PsiFile, element: MvReferenceElement) {
        if (element.reference == null) return
        if (element.hasAncestor<MvUseStmt>()) return

        val name = element.referenceName ?: return
        val candidates =
            ImportCandidateCollector.getImportCandidates(ImportContext.from(element), name)
        if (candidates.isEmpty()) return

        if (candidates.size == 1) {
            project.runWriteCommandAction {
                candidates.first().import(element)
            }
        } else {
            DataManager.getInstance().dataContextFromFocusAsync.onSuccess {
                chooseItemAndImport(project, it, candidates, element)
            }
        }
        isConsumed = true
    }

    data class Context(val candidates: List<ImportCandidate>)

    private fun chooseItemAndImport(
        project: Project,
        dataContext: DataContext,
        items: List<ImportCandidate>,
        context: MvElement
    ) {
        showItemsToImportChooser(project, dataContext, items) { selectedValue ->
            project.runWriteCommandAction {
                selectedValue.import(context)
            }
        }
    }

    companion object {
        const val NAME = "Import"

        fun findApplicableContext(refElement: MvReferenceElement): Context? {
            if (refElement.reference == null) return null
            if (refElement.resolvable) return null
            if (refElement.ancestorStrict<MvUseStmt>() != null) return null
            if (refElement is MvPath && refElement.moduleRef != null) return null

            // TODO: no auto-import if name in scope, but cannot be resolved

            val refName = refElement.referenceName ?: return null
            val importContext = ImportContext.from(refElement)
            val candidates =
                ImportCandidateCollector.getImportCandidates(importContext, refName)
            return Context(candidates)
        }
    }
}

@Suppress("DataClassPrivateConstructor")
data class ImportContext private constructor(
    val pathElement: MvReferenceElement,
    val namespaces: Set<Namespace>,
    val visibilities: Set<Visibility>,
    val contextScopeInfo: ContextScopeInfo,
) {
    companion object {
        fun from(
            contextElement: MvReferenceElement,
            namespaces: Set<Namespace>,
            visibilities: Set<Visibility>,
            contextScopeInfo: ContextScopeInfo
        ): ImportContext {
            return ImportContext(contextElement, namespaces, visibilities, contextScopeInfo)
        }

        fun from(refElement: MvReferenceElement): ImportContext {
            val ns = refElement.importCandidateNamespaces()
            val vs = if (refElement.containingScript != null) {
                setOf(Visibility.Public, Visibility.PublicScript)
            } else {
                val module = refElement.containingModule
                if (module != null) {
                    setOf(Visibility.Public, Visibility.PublicFriend(module.asSmartPointer()))
                } else {
                    setOf(Visibility.Public)
                }
            }
            val contextScopeInfo = ContextScopeInfo(
                letStmtScope = refElement.letStmtScope,
                refItemScopes = refElement.refItemScopes,
            )
            return ImportContext(refElement, ns, vs, contextScopeInfo)
        }
    }
}

//fun MoveFile.qualifiedItems(
//    targetName: String,
//    namespaces: Set<Namespace>,
//    visibilities: Set<Visibility>,
//    itemVis: ItemVis
//): List<MvQualNamedElement> {
//    checkUnitTestMode()
//    val elements = mutableListOf<MvQualNamedElement>()
//    processFileItems(this, namespaces, visibilities, itemVis) {
//        if (it.element is MvQualNamedElement && it.name == targetName) {
//            elements.add(it.element)
//        }
//        false
//    }
//    return elements
//}
