package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvAddressRef
import org.sui.lang.moveProject

val MvAddressRef.normalizedText: String get() = this.text.lowercase()

val MvAddressRef.useGroupLevel: Int
    get() {
        // sort to the end if not a named address
        if (this.namedAddress == null) return 4

        val name = this.namedAddress?.text.orEmpty().lowercase()
        val currentPackageAddresses =
            this.moveProject?.currentPackageAddresses()?.keys.orEmpty().map { it.lowercase() }
        return when (name) {
            "std", "aptos_std", "aptos_framework", "aptos_token" -> 1
            !in currentPackageAddresses -> 2
            else -> 3
        }
    }
