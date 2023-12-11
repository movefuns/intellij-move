/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.sui.lang.core.types.ty

import org.sui.ide.presentation.tyToString

import org.sui.lang.core.types.infer.TypeFolder
import org.sui.lang.core.types.infer.TypeVisitor
import org.sui.lang.core.types.infer.mergeFlags

data class TyTuple(val types: List<Ty>) : Ty(mergeFlags(types)) {
    override fun abilities() = Ability.all()

    override fun innerFoldWith(folder: TypeFolder): Ty =
        TyTuple(types.map { it.foldWith(folder) })

    override fun innerVisitWith(visitor: TypeVisitor): Boolean =
        types.any(visitor)

    override fun toString(): String = tyToString(this)

    companion object {
        fun unknown(arity: Int): TyTuple = TyTuple(generateSequence { TyUnknown }.take(arity).toList())
    }
}
