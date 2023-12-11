package org.sui.lang.core.completion.providers

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.util.ProcessingContext
import org.sui.lang.core.MvPsiPatterns
import org.sui.lang.core.completion.addSuffix
import org.sui.lang.core.completion.alreadyHasSpace
import org.sui.lang.core.completion.createLookupElementWithIcon
import org.sui.lang.core.psi.MvItemSpecRef
import org.sui.lang.core.psi.itemScope
import org.sui.lang.core.resolve.ItemVis
import org.sui.lang.core.resolve.MslLetScope
import org.sui.lang.core.resolve.processItems
import org.sui.lang.core.resolve.ref.Namespace
import org.sui.lang.core.resolve.ref.Visibility

object SpecItemCompletionProvider : MvCompletionProvider() {
    override val elementPattern get() = MvPsiPatterns.itemSpecRef()

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val itemSpecRef = parameters.position.parent as? MvItemSpecRef ?: return

        val itemVis = ItemVis(
            namespaces = setOf(Namespace.SPEC_ITEM),
            visibilities = Visibility.none(),
            mslLetScope = MslLetScope.NONE,
            itemScope = itemSpecRef.itemScope,
        )
        processItems(itemSpecRef, itemVis) {
            val lookup = it.element.createLookupElementWithIcon()
                .withInsertHandler { ctx, _ ->
                    if (!ctx.alreadyHasSpace) ctx.addSuffix(" ")
                }
            result.addElement(lookup)
            false
        }
    }


}
