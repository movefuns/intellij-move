package org.sui.lang.core.types.ty

import org.sui.lang.core.psi.MvSchema
import org.sui.lang.core.types.infer.Substitution
import org.sui.lang.core.types.infer.TypeFolder
import org.sui.lang.core.types.infer.mergeFlags

data class TySchema(
    override val item: MvSchema,
    override val substitution: Substitution,
    val typeArguments: List<Ty>
) : GenericTy(item, substitution, mergeFlags(typeArguments)) {

    override fun abilities(): Set<Ability> = Ability.all()

    override fun innerFoldWith(folder: TypeFolder): Ty {
        return TySchema(
            item,
            substitution.foldValues(folder),
            typeArguments.map { it.foldWith(folder) }
        )
    }
}
