package org.sui.lang.core.completion.providers

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import org.sui.lang.core.completion.createCompletionLookupElement
import org.sui.lang.core.psi.MvBindingPat
import org.sui.lang.core.psi.MvLetStmt
import org.sui.lang.core.psi.containingModule
import org.sui.lang.core.psi.namedItemScopes
import org.sui.lang.core.psiElement
import org.sui.lang.core.resolve.ContextScopeInfo
import org.sui.lang.core.resolve.LetStmtScope
import org.sui.lang.core.resolve.processModuleItems
import org.sui.lang.core.resolve.ref.Namespace
import org.sui.lang.core.resolve.ref.Visibility
import org.sui.lang.core.withParent

object StructPatCompletionProvider : MvCompletionProvider() {
    override val elementPattern: ElementPattern<out PsiElement>
        get() =
            PlatformPatterns.psiElement()
                .withParent<MvBindingPat>()
                .withSuperParent(2, psiElement<MvLetStmt>())

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val bindingPat = parameters.position.parent as MvBindingPat
        val module = bindingPat.containingModule ?: return

        val namespaces = setOf(Namespace.TYPE)
        val contextScopeInfo =
            ContextScopeInfo(
                letStmtScope = LetStmtScope.NONE,
                refItemScopes = bindingPat.namedItemScopes,
            )
        processModuleItems(module, namespaces, setOf(Visibility.Internal), contextScopeInfo) {
            val lookup = it.element.createCompletionLookupElement()
            result.addElement(lookup)
            false

        }
    }


}
