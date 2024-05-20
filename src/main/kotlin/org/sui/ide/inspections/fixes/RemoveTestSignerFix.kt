package org.sui.ide.inspections.fixes

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.sui.ide.inspections.DiagnosticFix
import org.sui.lang.MvElementTypes
import org.sui.lang.core.psi.MvAttrItem
import org.sui.lang.core.psi.MvAttrItemList
import org.sui.lang.core.psi.ext.elementType
import org.sui.lang.core.psi.ext.getNextNonCommentSibling
import org.sui.lang.core.psi.ext.getPrevNonCommentSibling

class RemoveTestSignerFix(
    attrItem: MvAttrItem,
    val signerName: String
) : DiagnosticFix<MvAttrItem>(attrItem) {

    override fun getText(): String = "Remove '$signerName'"
    override fun getFamilyName(): String = "Remove unused test signer"

    override fun invoke(project: Project, file: PsiFile, element: MvAttrItem) {
        val attrItem = element
        val attrItemList = element.parent as MvAttrItemList

        // remove trailing comma
        attrItem.getNextNonCommentSibling()
            ?.takeIf { it.elementType == MvElementTypes.COMMA }
            ?.delete()

        // remove previous comma if this is last element
        val index = attrItemList.attrItemList.indexOf(attrItem)
        if (index == attrItemList.attrItemList.size - 1) {
            element.getPrevNonCommentSibling()
                ?.takeIf { it.elementType == MvElementTypes.COMMA }
                ?.delete()
        }
        attrItem.delete()
    }
}
