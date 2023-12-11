package org.sui.lang.core.psi.ext

import org.sui.lang.MvElementTypes.POST
import org.sui.lang.core.psi.MvLetStmt

val MvLetStmt.post: Boolean get() = this.hasChild(POST)
