package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvAttr
import org.sui.lang.core.psi.MvAttrItem
import org.sui.lang.core.psi.MvFunction
import org.sui.lang.core.psi.MvRefExpr

fun MvRefExpr.isAbortCodeConst(): Boolean {
    val abortCodeItem =
        (this.parent.parent as? MvAttrItem)
            ?.takeIf { it.identifier.text == "abort_code" }
            ?: return false
    val attr = abortCodeItem.ancestorStrict<MvAttr>() ?: return false
    return (attr.owner as? MvFunction)?.hasTestAttr ?: false
}
