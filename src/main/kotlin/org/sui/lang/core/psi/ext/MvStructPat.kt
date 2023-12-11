package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvStruct
import org.sui.lang.core.psi.MvStructPat

val MvStructPat.structItem: MvStruct? get() = this.path.reference?.resolveWithAliases() as? MvStruct
