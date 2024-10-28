package org.sui.lang.core.psi.ext

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.sui.cli.MoveProject
import org.sui.lang.core.psi.*
import org.sui.lang.core.resolve.ref.MvPolyVariantReference
import org.sui.lang.core.resolve.ref.MvPolyVariantReferenceBase
import org.sui.lang.core.types.address
import org.sui.lang.core.types.infer.inference
import org.sui.lang.core.types.ty.Ty
import org.sui.lang.core.types.ty.TyAdt
import org.sui.lang.core.types.ty.TyVector
import org.sui.stdext.wrapWithList

//typealias MatchSequence<T> = Sequence<ScopeItem<T>>

//fun <T: MvNamedElement> MatchSequence<T>.filterByName(refName: String): Sequence<T> {
//    return this
//        .filter { it.name == refName }
//        .map { it.element }
//}

fun Ty.itemModule(moveProject: MoveProject): MvModule? {
    val norefTy = this.derefIfNeeded()
    return when (norefTy) {
        is TyVector -> {
            moveProject
                .getModulesFromIndex("vector")
                .firstOrNull { it.is0x1Address(moveProject) }
        }
        is TyAdt -> norefTy.item.module
        else -> null
    }
}

fun MvModule.is0x1Address(moveProject: MoveProject): Boolean {
    val moduleAddress = this.address(moveProject)?.canonicalValue(moveProject)
    return moduleAddress == "0x00000000000000000000000000000001"
}

class MvMethodCallReferenceImpl(
    element: MvMethodCall
):
    MvPolyVariantReferenceBase<MvMethodCall>(element) {

    override fun multiResolve(): List<MvNamedElement> {
        val msl = element.isMsl()
        val receiverExpr = element.receiverExpr
        val inference = receiverExpr.inference(msl) ?: return emptyList()
        return inference.getResolvedMethod(element).wrapWithList()
    }

    override fun isReferenceTo(element: PsiElement): Boolean =
        element is MvFunction && super.isReferenceTo(element)
}

abstract class MvMethodCallMixin(node: ASTNode): MvElementImpl(node),
                                                 MvMethodCall {

    override fun getReference(): MvPolyVariantReference = MvMethodCallReferenceImpl(this)
}