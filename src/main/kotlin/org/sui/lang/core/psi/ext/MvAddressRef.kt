package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvAddressRef

val MvAddressRef.normalizedText: String get() = this.text.lowercase()
