package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvStructLitExpr
import org.sui.lang.core.psi.MvStructLitField
import org.sui.lang.core.psi.MvStructLitFieldsBlock

val MvStructLitExpr.fields: List<MvStructLitField>
    get() =
        structLitFieldsBlock.structLitFieldList

val MvStructLitExpr.fieldNames: List<String>
    get() =
        fields.map { it.referenceName }

val MvStructLitFieldsBlock.litExpr: MvStructLitExpr
    get() = this.parent as MvStructLitExpr
