package org.sui.utils

import com.intellij.psi.PsiElement
import org.sui.ide.refactoring.isValidMoveVariableIdentifier
import org.sui.lang.MvElementTypes
import org.sui.lang.core.psi.MvPsiFactory
import org.sui.lang.core.psi.ext.elementType

fun doRenameIdentifier(identifier: PsiElement, newName: String) {
    val factory = MvPsiFactory(identifier.project)
    val newIdentifier = when (identifier.elementType) {
        MvElementTypes.IDENTIFIER -> {
            if (!isValidMoveVariableIdentifier(newName)) return
            factory.identifier(newName)
        }
        else -> error("Unsupported identifier type for `$newName` (${identifier.elementType})")
    }
    identifier.replace(newIdentifier)
}
