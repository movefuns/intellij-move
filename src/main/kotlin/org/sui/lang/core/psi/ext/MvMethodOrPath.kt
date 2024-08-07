package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvTypeArgument
import org.sui.lang.core.psi.MvTypeArgumentList
import org.sui.lang.core.resolve.ref.MvReferenceElement

val MvMethodOrPath.typeArguments: List<MvTypeArgument> get() = typeArgumentList?.typeArgumentList.orEmpty()

interface MvMethodOrPath : MvReferenceElement {
    val typeArgumentList: MvTypeArgumentList?
}