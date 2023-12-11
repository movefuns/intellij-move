package org.sui.ide.intentions

import com.intellij.codeInspection.util.IntentionName
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.descendantsOfType
import org.sui.lang.MoveParserDefinition.Companion.EOL_COMMENT
import org.sui.lang.MvElementTypes.COMMA
import org.sui.lang.core.psi.MvElement
import org.sui.lang.core.psi.ext.elementType
import org.sui.lang.core.psi.ext.getNextNonCommentSibling
import org.sui.lang.core.psi.ext.leftSiblings
import org.sui.lang.core.psi.ext.rightSiblings

abstract class ListIntentionBase<TList : MvElement, TElement : MvElement>(
    private val listClass: Class<TList>,
    private val elementClass: Class<TElement>,
    @IntentionName intentionText: String
) : MvElementBaseIntentionAction<TList>() {

    init {
        text = intentionText
    }

    override fun getFamilyName(): String = text

    protected val PsiElement.listContext: TList?
        get() = PsiTreeUtil.getParentOfType(this, listClass, true)

    protected open fun getElements(context: TList): List<PsiElement> =
        PsiTreeUtil.getChildrenOfTypeAsList(context, elementClass)

    protected open fun getEndElement(ctx: TList, element: PsiElement): PsiElement =
        commaAfter(element) ?: element

    protected fun hasLineBreakAfter(ctx: TList, element: PsiElement): Boolean =
        nextBreak(getEndElement(ctx, element)) != null

    protected fun nextBreak(element: PsiElement): PsiWhiteSpace? =
        element.rightSiblings.lineBreak()

    protected fun hasLineBreakBefore(element: PsiElement): Boolean =
        prevBreak(element) != null

    protected fun prevBreak(element: PsiElement): PsiWhiteSpace? =
        element.leftSiblings.lineBreak()

    protected fun hasEolComment(element: PsiElement): Boolean =
        element.descendantsOfType<PsiComment>().any { it.elementType == EOL_COMMENT }

    private fun commaAfter(element: PsiElement): PsiElement? =
        element.getNextNonCommentSibling()?.takeIf { it.elementType == COMMA }

    private fun Sequence<PsiElement>.lineBreak(): PsiWhiteSpace? =
        takeWhile { !elementClass.isInstance(it) }
            .firstOrNull { it is PsiWhiteSpace && it.textContains('\n') } as? PsiWhiteSpace
}
