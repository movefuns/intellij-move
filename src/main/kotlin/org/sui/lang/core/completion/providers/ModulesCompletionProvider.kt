package org.sui.lang.core.completion.providers

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.patterns.ElementPattern
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import org.sui.ide.inspections.imports.ImportContext
import org.sui.lang.core.MvPsiPatterns
import org.sui.lang.core.completion.CompletionContext
import org.sui.lang.core.completion.IMPORTED_MODULE_PRIORITY
import org.sui.lang.core.completion.UNIMPORTED_ITEM_PRIORITY
import org.sui.lang.core.completion.createLookupElement
import org.sui.lang.core.psi.MvPath
import org.sui.lang.core.psi.containingModule
import org.sui.lang.core.psi.containingModuleSpec
import org.sui.lang.core.psi.ext.equalsTo
import org.sui.lang.core.psi.refItemScopes
import org.sui.lang.core.resolve.ContextScopeInfo
import org.sui.lang.core.resolve.letStmtScope
import org.sui.lang.core.resolve.processItems
import org.sui.lang.core.resolve.ref.Namespace
import org.sui.lang.core.resolve.ref.Visibility

object ModulesCompletionProvider : MvCompletionProvider() {
    override val elementPattern: ElementPattern<PsiElement>
        get() =
            MvPsiPatterns.path()

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val maybePath = parameters.position.parent
        val refElement =
            maybePath as? MvPath ?: maybePath.parent as MvPath

        if (parameters.position !== refElement.referenceNameElement) return
        if (refElement.moduleRef != null) return

        val processedNames = mutableSetOf<String>()
        val namespaces = setOf(Namespace.MODULE)
        val contextScopeInfo =
            ContextScopeInfo(
                letStmtScope = refElement.letStmtScope,
                refItemScopes = refElement.refItemScopes,
            )
        val completionCtx = CompletionContext(refElement, contextScopeInfo)
        processItems(refElement, namespaces, contextScopeInfo) { (name, element) ->
            result.addElement(
                element.createLookupElement(completionCtx, priority = IMPORTED_MODULE_PRIORITY)
            )
            processedNames.add(name)
            false
        }

        // disable auto-import in module specs for now
        if (refElement.containingModuleSpec != null) return

        val path = parameters.originalPosition?.parent as? MvPath ?: return
        val importContext =
            ImportContext.from(
                path,
                namespaces,
                setOf(Visibility.Public),
                contextScopeInfo
            )
        val containingMod = path.containingModule
        val candidates = getImportCandidates(parameters, result, processedNames, importContext,
                                             itemFilter = {
                                                 containingMod != null && !it.equalsTo(
                                                     containingMod
                                                 )
                                             })
        candidates.forEach { candidate ->
            val lookupElement =
                candidate.element.createLookupElement(
                    completionCtx,
                    structAsType = Namespace.TYPE in importContext.namespaces,
                    priority = UNIMPORTED_ITEM_PRIORITY,
                    insertHandler = ImportInsertHandler(parameters, candidate)
                )
            result.addElement(lookupElement)
        }
    }
}
