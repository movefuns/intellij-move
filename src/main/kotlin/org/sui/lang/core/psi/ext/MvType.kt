package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvPathType
import org.sui.lang.core.psi.MvRefType
import org.sui.lang.core.psi.MvType
import org.sui.lang.core.psi.MvTypeArgument
import org.sui.lang.core.resolve.ref.MvPolyVariantReference

val MvType.moveReference: MvPolyVariantReference?
    get() = when (this) {
        is MvPathType -> this.path.reference
        is MvRefType -> this.type?.moveReference
        else -> null
    }
val MvType.typeArguments: List<MvTypeArgument>
    get() {
        return when (this) {
            is MvPathType -> this.path.typeArguments
            is MvRefType -> this.type?.typeArguments.orEmpty()
            else -> emptyList()
        }
    }
