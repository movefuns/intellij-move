package org.sui.lang

import com.intellij.lang.Language

object MoveLanguage : Language("Sui Move") {
    private fun readResolve(): Any = MoveLanguage
    override fun isCaseSensitive() = true
    override fun getDisplayName() = "Sui Move"
}
