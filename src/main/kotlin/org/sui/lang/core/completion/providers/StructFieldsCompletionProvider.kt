package org.sui.lang.core.completion.providers

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import org.sui.lang.core.MvPsiPattern.bindingPat
import org.sui.lang.core.completion.CompletionContext
import org.sui.lang.core.completion.createLookupElement
import org.sui.lang.core.psi.*
import org.sui.lang.core.psi.ext.*
import org.sui.lang.core.withParent
import org.sui.lang.core.withSuperParent

object StructFieldsCompletionProvider: MvCompletionProvider() {
    override val elementPattern: ElementPattern<out PsiElement>
        get() = StandardPatterns.or(
            PlatformPatterns
                .psiElement()
                .withParent<MvStructLitField>(),
            PlatformPatterns
                .psiElement()
                .withParent<MvPatField>(),
            bindingPat()
                .withSuperParent<MvPatField>(2),
        )

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val pos = parameters.position
        var element = pos.parent as? MvElement ?: return

        if (element is MvPatBinding) element = element.parent as MvElement

        val completionCtx = CompletionContext(element, element.isMsl())
        when (element) {
            is MvPatField -> {
                val patStruct = element.patStruct
                addFieldsToCompletion(
                    patStruct.path.maybeStruct ?: return,
                    patStruct.providedFieldNames,
                    result,
                    completionCtx
                )
            }
            is MvStructLitField -> {
                val structLit = element.structLitExpr
                addFieldsToCompletion(
                    structLit.path.maybeStruct ?: return,
                    structLit.providedFieldNames,
                    result,
                    completionCtx
                )
            }
        }
    }


    private fun addFieldsToCompletion(
        referredStruct: MvStruct,
        providedFieldNames: Set<String>,
        result: CompletionResultSet,
        completionContext: CompletionContext,
    ) {
        for (field in referredStruct.fields.filter { it.name !in providedFieldNames }) {
            result.addElement(
                field.createLookupElement(completionContext)
            )
        }
    }
}

