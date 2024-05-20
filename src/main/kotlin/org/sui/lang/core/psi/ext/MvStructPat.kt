package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvStruct
import org.sui.lang.core.psi.MvStructPat
import org.sui.lang.core.psi.MvStructPatField

val MvStructPat.patFields: List<MvStructPatField>
    get() =
        structPatFieldsBlock.structPatFieldList

val MvStructPat.patFieldNames: List<String>
    get() =
        patFields.map { it.referenceName }

val MvStructPat.structItem: MvStruct? get() = this.path.reference?.resolveWithAliases() as? MvStruct
