package org.sui.lang.core.completion

import org.sui.ide.presentation.text
import org.sui.lang.core.psi.MvFunction
import org.sui.lang.core.psi.ext.name
import org.sui.lang.core.psi.parameters
import org.sui.lang.core.types.infer.TypeFoldable
import org.sui.lang.core.types.infer.TypeFolder
import org.sui.lang.core.types.infer.TypeVisitor
import org.sui.lang.core.types.ty.Ty
import org.sui.lang.core.types.ty.TyReference
import org.sui.lang.core.types.ty.TyUnit

data class FuncSignature(
    private val params: Map<String, Ty>,
    private val retType: Ty,
) : TypeFoldable<FuncSignature> {

    override fun innerFoldWith(folder: TypeFolder): FuncSignature {
        return FuncSignature(
            params = params.mapValues { (_, it) -> folder.fold(it) },
            retType = folder.fold(retType)
        )
    }

    override fun innerVisitWith(visitor: TypeVisitor): Boolean =
        params.values.any { visitor(it) } || visitor(retType)

    fun paramsText(): String {
        return params.entries
            .withIndex()
            .joinToString(", ", prefix = "(", postfix = ")") { (i, value) ->
                val (paramName, paramTy) = value
                if (i == 0 && paramName == "self") {
                    when (paramTy) {
                        is TyReference -> "&${if (paramTy.isMut) "mut " else ""}self"
                        else -> "self"
                    }
                } else {
                    "$paramName: ${paramTy.text(false)}"
                }
            }
    }

    fun retTypeText(): String = retType.text(false)

    fun retTypeSuffix(): String {
        return if (retType is TyUnit) "" else ": ${retTypeText()}"
    }

    companion object {
        fun fromFunction(function: MvFunction, msl: Boolean): FuncSignature {
            val declaredType = function.declaredType(msl)
            val params = function.parameters.zip(declaredType.paramTypes)
                .associate { (param, paramTy) -> Pair(param.name, paramTy) }
            val retType = declaredType.retType
            return FuncSignature(params, retType)
        }
    }
}