package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvMatchArm
import org.sui.lang.core.psi.MvMatchBody
import org.sui.lang.core.psi.MvMatchExpr

val MvMatchArm.matchBody: MvMatchBody get() = this.parent as MvMatchBody
val MvMatchArm.matchExpr: MvMatchExpr get() = this.matchBody.parent as MvMatchExpr
