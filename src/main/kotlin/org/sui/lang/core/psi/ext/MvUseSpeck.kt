package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvPath
import org.sui.lang.core.psi.MvUseGroup
import org.sui.lang.core.psi.MvUseSpeck

val MvUseSpeck.qualifier: MvPath? get() {
    val parentUseSpeck = (context as? MvUseGroup)?.parentUseSpeck ?: return null
    return parentUseSpeck.path
}
val MvUseSpeck.isSelf: Boolean get() = this.path.identifier?.textMatches("Self") ?: false
