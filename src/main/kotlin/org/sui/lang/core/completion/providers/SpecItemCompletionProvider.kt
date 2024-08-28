package org.sui.lang.core.completion.providers

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.util.ProcessingContext
import org.sui.lang.core.MvPsiPattern
import org.sui.lang.core.completion.addSuffix
import org.sui.lang.core.completion.alreadyHasSpace
import org.sui.lang.core.completion.createLookupElementWithIcon
import org.sui.lang.core.psi.MvItemSpecRef
import org.sui.lang.core.psi.ext.itemSpec
import org.sui.lang.core.psi.ext.module
import org.sui.lang.core.psi.ext.mslSpecifiableItems

object SpecItemCompletionProvider : MvCompletionProvider() {
    override val elementPattern get() = MvPsiPattern.itemSpecRef()

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val itemSpecRef = parameters.position.parent as? MvItemSpecRef ?: return
        val module = itemSpecRef.itemSpec.module ?: return
        module.mslSpecifiableItems
            .forEach {
                val lookup = it.createLookupElementWithIcon()
                    .withInsertHandler { ctx, _ ->
                        if (!ctx.alreadyHasSpace) ctx.addSuffix(" ")
                    }
                result.addElement(lookup)
            }
    }


}
