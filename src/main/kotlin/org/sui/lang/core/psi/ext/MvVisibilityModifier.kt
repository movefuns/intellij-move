package org.sui.lang.core.psi.ext

import org.sui.lang.MvElementTypes.*
import org.sui.lang.core.psi.MvFunction
import org.sui.lang.core.psi.MvVisibilityModifier

val MvVisibilityModifier.isPublic get() = hasChild(PUBLIC)
val MvVisibilityModifier.isPublicScript get() = hasChild(SCRIPT)
val MvVisibilityModifier.isPublicPackage get() = hasChild(PACKAGE)
val MvVisibilityModifier.isPublicFriend get() = hasChild(FRIEND)

val MvVisibilityModifier.function: MvFunction? get() = parent as? MvFunction