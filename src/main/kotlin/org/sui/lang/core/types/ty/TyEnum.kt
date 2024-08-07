package org.sui.lang.core.types.ty

import org.sui.ide.presentation.tyToString
import org.sui.lang.core.psi.MvEnum
import org.sui.lang.core.psi.typeParameters
import org.sui.lang.core.types.infer.*

data class TyEnum(
    override val item: MvEnum,
    override val substitution: Substitution,
    val typeArguments: List<Ty>,
) : GenericTy(item, substitution, mergeFlags(typeArguments) or HAS_TY_STRUCT_MASK) {

    override fun innerFoldWith(folder: TypeFolder): Ty {
        return TyEnum(
            item,
            substitution.foldValues(folder),
            typeArguments.map { it.foldWith(folder) }
        )
    }

    override fun toString(): String = tyToString(this)

    override fun abilities(): Set<Ability> {
        TODO("Not yet implemented")
    }

    // This method is rarely called (in comparison with folding), so we can implement it in a such inefficient way.
    override val typeParameterValues: Substitution
        get() {
            val typeSubst = item.typeParameters.withIndex().associate { (i, param) ->
                TyTypeParameter(param) to typeArguments.getOrElse(i) { TyUnknown }
            }
            return Substitution(typeSubst)
        }

    override fun innerVisitWith(visitor: TypeVisitor): Boolean {
        return typeArguments.any { it.visitWith(visitor) } || substitution.visitValues(visitor)
    }
}
