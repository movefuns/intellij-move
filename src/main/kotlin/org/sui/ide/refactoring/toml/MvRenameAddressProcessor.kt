package org.sui.ide.refactoring.toml

import com.intellij.psi.PsiElement
import com.intellij.refactoring.rename.RenamePsiElementProcessor
import org.sui.openapiext.addressesTable
import org.toml.lang.psi.TomlKeySegment

class MvRenameAddressProcessor : RenamePsiElementProcessor() {
    override fun canProcessElement(element: PsiElement): Boolean {
        return element is TomlKeySegment && element.addressesTable != null
    }
}
