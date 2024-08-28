package org.sui.lang.core.completion.providers

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import org.sui.lang.core.completion.CompletionContext
import org.sui.lang.core.completion.getOriginalOrSelf
import org.sui.lang.core.psi.MvSchemaLitField
import org.sui.lang.core.psi.ext.fields
import org.sui.lang.core.psi.ext.isMsl
import org.sui.lang.core.psi.ext.processSchemaLitFieldResolveVariants
import org.sui.lang.core.psi.ext.schemaLit
import org.sui.lang.core.resolve.collectCompletionVariants
import org.sui.lang.core.resolve.wrapWithFilter
import org.sui.lang.core.withParent

object SchemaFieldsCompletionProvider : MvCompletionProvider() {
    override val elementPattern: ElementPattern<out PsiElement>
        get() =
            PlatformPatterns.psiElement().withParent<MvSchemaLitField>()

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val pos = parameters.position
        val literalField = pos.parent as? MvSchemaLitField ?: return

        val schemaLit = literalField.schemaLit?.getOriginalOrSelf() ?: return
        val existingFieldNames = schemaLit.fields
            .filter { !it.textRange.contains(pos.textOffset) }
            .map { it.referenceName }

        val completionCtx = CompletionContext(literalField, literalField.isMsl())
        collectCompletionVariants(result, completionCtx) {
            val processor = it.wrapWithFilter { e -> e.name !in existingFieldNames }
            processSchemaLitFieldResolveVariants(literalField, processor)
        }
    }
}
