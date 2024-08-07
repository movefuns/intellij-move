package org.sui.lang.core.completion.providers

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import org.sui.lang.core.completion.CompletionContext
import org.sui.lang.core.completion.createLookupElement
import org.sui.lang.core.psi.MvSchemaLitField
import org.sui.lang.core.psi.ext.fieldBindings
import org.sui.lang.core.psi.ext.fieldNames
import org.sui.lang.core.psi.ext.schema
import org.sui.lang.core.psi.ext.schemaLit
import org.sui.lang.core.resolve.ContextScopeInfo
import org.sui.lang.core.withParent

object SchemaFieldsCompletionProvider : MvCompletionProvider() {
    override val elementPattern: ElementPattern<out PsiElement>
        get() = PlatformPatterns
            .psiElement()
            .withParent<MvSchemaLitField>()

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val pos = parameters.position
        val element = pos.parent as? MvSchemaLitField ?: return
        val schemaLit = element.schemaLit ?: return
        val schema = schemaLit.schema ?: return
        val providedFieldNames = schemaLit.fieldNames

        val completionCtx = CompletionContext(element, ContextScopeInfo.msl())
        for (fieldBinding in schema.fieldBindings.filter { it.name !in providedFieldNames }) {
            result.addElement(
                fieldBinding.createLookupElement(completionCtx)
            )
        }
    }
}
