package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvAttr
import org.sui.lang.core.psi.MvAttrItem
import org.sui.lang.core.psi.MvFunction
import org.sui.lang.core.psi.MvPathExpr

fun MvPathExpr.isAbortCodeConst(): Boolean {
    val abortCodeItem =
        (this.parent.parent as? MvAttrItem)
            ?.takeIf { it.isAbortCode }
            ?: return false
    val attr = abortCodeItem.ancestorStrict<MvAttr>() ?: return false
    return (attr.owner as? MvFunction)?.hasTestAttr ?: false
}
