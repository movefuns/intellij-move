package org.sui.toml

import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import org.sui.lang.core.MvPsiPattern
import org.sui.lang.core.psi.MvNamedAddress
import org.sui.lang.core.resolve.ref.NamedAddressReference

class NamedAddressReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(
        element: PsiElement,
        context: ProcessingContext
    ): Array<PsiReference> {
        if (element !is MvNamedAddress) return emptyArray()
        return arrayOf(NamedAddressReference(element))
    }
}

class NamedAddressReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            MvPsiPattern.namedAddress(), NamedAddressReferenceProvider()
        )
    }
}
