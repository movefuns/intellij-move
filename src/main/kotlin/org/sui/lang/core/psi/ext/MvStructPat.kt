package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvStruct
import org.sui.lang.core.psi.MvStructPat

val MvStructPat.providedFieldNames: Set<String>
    get() =
        fieldPatList.map { it.fieldReferenceName }.toSet()

val MvStructPat.structItem: MvStruct? get() = this.path.reference?.resolveFollowingAliases() as? MvStruct
