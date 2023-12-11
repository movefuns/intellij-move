package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvRefType

val MvRefType.mutable: Boolean
    get() =
        "mut" in this.refTypeStart.text
