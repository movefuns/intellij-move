package org.sui.lang.core.types.infer

import org.sui.lang.core.psi.MvType
import org.sui.lang.core.psi.MvTypeParameter

/** Similar to [Substitution], but maps PSI to PSI instead of [Ty] to [Ty] */
class RsPsiSubstitution(
    val typeSubst: Map<MvTypeParameter, Value<MvType>> = emptyMap(),
) {
    sealed class Value<out P> {
        object RequiredAbsent : Value<Nothing>()
        object OptionalAbsent : Value<Nothing>()
        class Present<P>(val value: P) : Value<P>()
    }
}
