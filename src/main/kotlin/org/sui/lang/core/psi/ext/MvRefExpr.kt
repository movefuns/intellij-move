package org.sui.lang.core.psi.ext


import org.sui.lang.core.psi.MvAttrItem
import org.sui.lang.core.psi.MvAttrItemArgument
import org.sui.lang.core.psi.MvFunction
import org.sui.lang.core.psi.MvRefExpr

fun MvRefExpr.isAbortCodeConst(): Boolean {
    val itemArgument = this.parent as? MvAttrItemArgument ?: return false
    if (itemArgument.identifier.text != "abort_code") return false

    val attrItem = itemArgument.parent?.parent as? MvAttrItem ?: return false
    return (attrItem.attr.owner as? MvFunction)?.isTest ?: false
}
