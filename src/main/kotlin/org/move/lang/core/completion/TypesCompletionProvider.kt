package org.move.lang.core.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.patterns.ElementPattern
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import org.move.lang.core.MvPsiPatterns
import org.move.lang.core.psi.MvModuleDef
import org.move.lang.core.psi.MvPathType
import org.move.lang.core.psi.ext.isSpecElement
import org.move.lang.core.resolve.processNestedScopesUpwards
import org.move.lang.core.resolve.ref.Namespace
import org.move.lang.core.resolve.ref.Visibility
import org.move.lang.core.resolve.ref.processModuleItems

object TypesCompletionProvider : MvCompletionProvider() {
    override val elementPattern: ElementPattern<out PsiElement>
        get() = MvPsiPatterns.pathType()

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val maybePath = parameters.position.parent.parent
        val maybeQualPathType = maybePath.parent
        val refElement =
            maybeQualPathType as? MvPathType
                ?: maybeQualPathType.parent as MvPathType

        if (parameters.position !== refElement.path.referenceNameElement) return

        val moduleRef = refElement.path.pathIdent.moduleRef
        if (moduleRef != null) {
            val module = moduleRef.reference?.resolve() as? MvModuleDef ?: return
            val vs = setOf(Visibility.Public)
            val ns = setOf(Namespace.TYPE)
            processModuleItems(module, vs, ns) {
                if (it.element != null) {
                    val lookup = it.element.createLookupElement(false)
                    result.addElement(lookup)
                }
                false
            }
            return
        }

//        val module = resolveModule(refElement)
//        if (module != null) {
//            processPublicModuleItems(module, setOf(Namespace.TYPE)) {
//                if (it.element != null) {
//                    val lookup = it.element.createLookupElement(false)
//                    result.addElement(lookup)
//                }
//                false
//            }
//            return
//        }

        processNestedScopesUpwards(refElement, Namespace.TYPE) {
            if (it.element != null) {
                val lookup = it.element.createLookupElement(refElement.isSpecElement())
                result.addElement(lookup)
            }
            false
        }
    }
}
