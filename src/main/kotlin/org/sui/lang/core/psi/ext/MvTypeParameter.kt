package org.sui.lang.core.psi.ext

import com.intellij.lang.ASTNode
import org.sui.lang.MvElementTypes
import org.sui.lang.core.psi.MvAbility
import org.sui.lang.core.psi.MvTypeParameter
import org.sui.lang.core.psi.impl.MvNameIdentifierOwnerImpl
import org.sui.lang.core.types.ty.TyTypeParameter

val MvTypeParameter.isPhantom get() = hasChild(MvElementTypes.PHANTOM)

val MvTypeParameter.typeParamType: TyTypeParameter
    get() {
        return TyTypeParameter(this)
    }

val MvTypeParameter.abilityBounds: List<MvAbility>
    get() {
        return typeParamBound?.abilityList.orEmpty()
    }

//fun MvTypeParameter.ty(): TyTypeParameter = TyTypeParameter(this)

abstract class MvTypeParameterMixin(node: ASTNode) : MvNameIdentifierOwnerImpl(node),
                                                     MvTypeParameter
