package org.sui.lang.core.psi.ext

import com.intellij.psi.util.descendantsOfType
import org.sui.lang.core.psi.MvBindingPat
import org.sui.lang.core.psi.MvNamedElement
import org.sui.lang.core.psi.MvPat

val MvPat.bindings: Sequence<MvNamedElement> get() = this.descendantsOfType<MvBindingPat>()
