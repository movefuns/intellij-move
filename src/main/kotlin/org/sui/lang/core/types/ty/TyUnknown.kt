/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.sui.lang.core.types.ty

import org.sui.ide.presentation.tyToString
import org.sui.lang.core.types.infer.HAS_TY_UNKNOWN_MASK

object TyUnknown : Ty(HAS_TY_UNKNOWN_MASK) {
    override fun abilities() = Ability.all()
    override fun toString(): String = tyToString(this)
}
