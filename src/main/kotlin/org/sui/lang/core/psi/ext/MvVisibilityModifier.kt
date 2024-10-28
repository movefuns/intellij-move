package org.sui.lang.core.psi.ext

import org.sui.lang.MvElementTypes.*
import org.sui.lang.core.psi.MvFunction
import org.sui.lang.core.psi.MvVisibilityModifier

val MvVisibilityModifier.hasPublic get() = hasChild(PUBLIC)
val MvVisibilityModifier.hasScript get() = hasChild(SCRIPT)
val MvVisibilityModifier.hasPackage get() = hasChild(PACKAGE)
val MvVisibilityModifier.hasFriend get() = hasChild(FRIEND)

val MvVisibilityModifier.function: MvFunction? get() = parent as? MvFunction