package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvPatStruct
import org.sui.lang.core.psi.MvStruct

val MvPatStruct.providedFieldNames: Set<String>
    get() =
        patFieldList.map { it.fieldReferenceName }.toSet()

//val MvPatStruct.structItem: MvStruct? get() = this.path.reference?.resolveFollowingAliases() as? MvStruct
