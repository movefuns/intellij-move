/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.sui.ide.utils.imports

import org.sui.cli.MoveProject
import org.sui.lang.core.psi.MvUseSpeck
import org.sui.lang.core.psi.MvUseStmt
import org.sui.lang.core.psi.ext.basePath
import org.sui.lang.core.psi.ext.hasTestOnlyAttr
import org.sui.lang.core.psi.ext.hasVerifyOnlyAttr
import org.sui.lang.core.psi.ext.isSelf
import org.sui.lang.moveProject

class UseStmtWrapper(val useStmt: MvUseStmt) : Comparable<UseStmtWrapper> {
    private val namedAddress: String?
        get() {
            val useSpeck = useStmt.useSpeck ?: return null
            val base = useSpeck.path.basePath()
            if (base.pathAddress != null) return null
            return base.identifier?.text
        }

    // `use` order:
    // 1. Standard library (stdlib)
    // 2. Related third party (extern crate)
    // 3. Local
    //    - otherwise
    //    - crate::
    //    - super::
    //    - self::
    val packageGroupLevel: Int = when {
        this.useStmt.hasTestOnlyAttr -> 5
        this.useStmt.hasVerifyOnlyAttr -> 6
        else -> getBetweenGroupsLevel(this.namedAddress, useStmt.moveProject)
    }

    override fun compareTo(other: UseStmtWrapper): Int =
        compareValuesBy(
            this,
            other,
            { it.packageGroupLevel },
            { getWithinGroupLevel(it.namedAddress) },
            { it.useStmt.useSpeck?.text ?: "" }
        )
}

val COMPARATOR_FOR_ITEMS_IN_USE_GROUP: Comparator<MvUseSpeck> =
    compareBy<MvUseSpeck> { !it.isSelf }.thenBy { it.path.referenceName?.lowercase() }

private fun getWithinGroupLevel(namedAddress: String?): Int =
    when (namedAddress?.lowercase()) {
        "std" -> 0
        "aptos_std" -> 1
        "aptos_framework" -> 2
        "aptos_token" -> 3
        else -> 4
    }

private fun getBetweenGroupsLevel(namedAddress: String?, moveProject: MoveProject?): Int {
    // sort to the end if not a named address
    if (namedAddress == null) return 4

    val name = namedAddress.lowercase()
    val currentPackageAddresses =
        moveProject?.currentPackageAddresses()?.keys.orEmpty().map { it.lowercase() }
    return when (name) {
        "std", "aptos_std", "aptos_framework", "aptos_token" -> 1
        !in currentPackageAddresses -> 2
        else -> 3
    }
}
