package org.sui.lang.core.types.ty

import org.sui.ide.presentation.tyToString
import org.sui.lang.core.psi.MvEnum
import org.sui.lang.core.psi.MvStruct
import org.sui.lang.core.psi.ext.MvStructOrEnumItemElement
import org.sui.lang.core.psi.ext.abilities
import org.sui.lang.core.psi.typeParameters
import org.sui.lang.core.types.infer.*

data class TyAdt(
    override val item: MvStructOrEnumItemElement,
    override val substitution: Substitution,
    val typeArguments: List<Ty>,
): GenericTy(item, substitution, mergeFlags(typeArguments) or HAS_TY_ADT_MASK) {

    override fun abilities(): Set<Ability> = this.item.abilities

    override fun innerFoldWith(folder: TypeFolder): Ty {
        return TyAdt(
            item,
            substitution.foldValues(folder),
            typeArguments.map { it.foldWith(folder) }
        )
    }

    override fun toString(): String = tyToString(this)

    override fun innerVisitWith(visitor: TypeVisitor): Boolean {
        return typeArguments.any { it.visitWith(visitor) } || substitution.visitValues(visitor)
    }

    // This method is rarely called (in comparison with folding), so we can implement it in a such inefficient way.
    override val typeParameterValues: Substitution
        get() {
            val typeSubst = item.typeParameters.withIndex().associate { (i, param) ->
                TyTypeParameter(param) to typeArguments.getOrElse(i) { TyUnknown }
            }
            return Substitution(typeSubst)
        }
}

val TyAdt.enumItem: MvEnum? get() = this.item as? MvEnum
val TyAdt.structItem: MvStruct? get() = this.item as? MvStruct