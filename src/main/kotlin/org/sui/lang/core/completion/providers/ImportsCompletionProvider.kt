package org.sui.lang.core.completion.providers

import com.intellij.codeInsight.completion.BasicInsertHandler
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import org.sui.lang.core.completion.CompletionContext
import org.sui.lang.core.completion.createLookupElement
import org.sui.lang.core.completion.createSelfLookup
import org.sui.lang.core.psi.MvModule
import org.sui.lang.core.psi.MvUseItem
import org.sui.lang.core.psi.MvUseItemGroup
import org.sui.lang.core.psi.ext.isSelfModuleRef
import org.sui.lang.core.psi.ext.itemUseSpeck
import org.sui.lang.core.psi.ext.names
import org.sui.lang.core.psi.refItemScopes
import org.sui.lang.core.resolve.ContextScopeInfo
import org.sui.lang.core.resolve.letStmtScope
import org.sui.lang.core.resolve.processModuleItems
import org.sui.lang.core.resolve.ref.Namespace
import org.sui.lang.core.resolve.ref.Visibility
import org.sui.lang.core.withParent

object ImportsCompletionProvider : MvCompletionProvider() {
    override val elementPattern: ElementPattern<PsiElement>
        get() = PlatformPatterns
            .psiElement().withParent<MvUseItem>()

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val itemImport = parameters.position.parent as MvUseItem
        if (parameters.position !== itemImport.referenceNameElement) return

        val moduleRef = itemImport.itemUseSpeck.fqModuleRef
        val referredModule = moduleRef.reference?.resolve() as? MvModule
            ?: return

        val p = itemImport.parent
        if (p is MvUseItemGroup && "Self" !in p.names) {
            result.addElement(referredModule.createSelfLookup())
        }

        val vs = when {
            moduleRef.isSelfModuleRef -> setOf(Visibility.Internal)
            else -> Visibility.visibilityScopesForElement(itemImport)
        }
        val ns = setOf(Namespace.NAME, Namespace.TYPE, Namespace.FUNCTION)
        val contextScopeInfo =
            ContextScopeInfo(
                letStmtScope = itemImport.letStmtScope,
                refItemScopes = itemImport.refItemScopes,
            )

        val completionContext = CompletionContext(itemImport, contextScopeInfo)
        processModuleItems(referredModule, ns, vs, contextScopeInfo) {
            result.addElement(
                it.element.createLookupElement(
                    completionContext,
                    insertHandler = BasicInsertHandler(),
                    structAsType = true
                )
            )
            false
        }
    }
}
