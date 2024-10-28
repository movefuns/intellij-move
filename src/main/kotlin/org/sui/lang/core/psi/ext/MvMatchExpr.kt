package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvMatchArgument
import org.sui.lang.core.psi.MvMatchArm
import org.sui.lang.core.psi.MvMatchBody
import org.sui.lang.core.psi.MvMatchExpr

val MvMatchExpr.matchArgument: MvMatchArgument get() = childOfType<MvMatchArgument>()!!
val MvMatchExpr.matchBody: MvMatchBody get() = childOfType<MvMatchBody>()!!
val MvMatchExpr.arms: List<MvMatchArm> get() = matchBody.matchArmList
