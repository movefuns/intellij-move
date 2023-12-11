package org.sui.lang.core.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.util.textCompletion.TextCompletionContributor
import org.sui.lang.core.psi.MvCodeFragment

class MoveTextFieldCompletionContributor : TextCompletionContributor() {
    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        if (parameters.originalFile is MvCodeFragment) {
            super.fillCompletionVariants(parameters, result)
        }
    }
}
