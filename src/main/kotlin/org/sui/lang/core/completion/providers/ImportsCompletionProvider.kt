package org.sui.lang.core.completion.providers

import com.intellij.codeInsight.completion.BasicInsertHandler
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import org.sui.lang.core.completion.createCompletionLookupElement
import org.sui.lang.core.completion.createSelfLookup
import org.sui.lang.core.psi.MvModule
import org.sui.lang.core.psi.MvUseItem
import org.sui.lang.core.psi.MvUseItemGroup
import org.sui.lang.core.psi.ext.isSelf
import org.sui.lang.core.psi.ext.names
import org.sui.lang.core.psi.ext.useSpeck
import org.sui.lang.core.psi.itemScope
import org.sui.lang.core.resolve.ItemVis
import org.sui.lang.core.resolve.mslLetScope
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
        val moduleRef = itemImport.useSpeck().fqModuleRef

        if (parameters.position !== itemImport.referenceNameElement) return
        val referredModule = moduleRef.reference?.resolve() as? MvModule
            ?: return

        val p = itemImport.parent
        if (p is MvUseItemGroup && "Self" !in p.names) {
            result.addElement(referredModule.createSelfLookup())
        }

        val vs = when {
            moduleRef.isSelf -> setOf(Visibility.Internal)
            else -> Visibility.buildSetOfVisibilities(itemImport)
        }
        val ns = setOf(Namespace.NAME, Namespace.TYPE, Namespace.FUNCTION)
        val itemVis = ItemVis(
            ns, vs,
            mslLetScope = itemImport.mslLetScope,
            itemScope = itemImport.itemScope,
        )
        processModuleItems(referredModule, itemVis) {
            val lookup =
                it.element.createCompletionLookupElement(
                    BasicInsertHandler(),
                    ns = itemVis.namespaces
                )
            result.addElement(lookup)
            false
        }
    }
}
