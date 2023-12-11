package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvAttr

val MvAttr.owner: MvDocAndAttributeOwner?
    get() = this.parent as? MvDocAndAttributeOwner
