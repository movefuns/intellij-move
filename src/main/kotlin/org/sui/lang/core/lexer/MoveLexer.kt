package org.sui.lang.core.lexer

import com.intellij.lexer.FlexAdapter
import org.sui.lang._MoveLexer

fun createMoveLexer(): MoveLexer {
    return MoveLexer()
}

class MoveLexer : FlexAdapter(_MoveLexer(null))
