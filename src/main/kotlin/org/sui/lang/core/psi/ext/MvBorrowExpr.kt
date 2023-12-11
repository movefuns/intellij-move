package org.sui.lang.core.psi.ext

import org.sui.lang.MvElementTypes
import org.sui.lang.core.psi.MvBorrowExpr

val MvBorrowExpr.isMut: Boolean get() = hasChild(MvElementTypes.MUT)
