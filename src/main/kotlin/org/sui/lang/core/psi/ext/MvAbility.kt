package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvAbility
import org.sui.lang.core.types.ty.Ability

val MvAbility.ability: Ability?
    get() =
        when (this.text) {
            "copy" -> Ability.COPY
            "store" -> Ability.STORE
            "key" -> Ability.KEY
            "drop" -> Ability.DROP
            else -> null
        }
