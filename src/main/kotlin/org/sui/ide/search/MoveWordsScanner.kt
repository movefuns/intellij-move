package org.sui.ide.search

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import org.sui.lang.MvElementTypes.BYTE_STRING_LITERAL
import org.sui.lang.MvElementTypes.IDENTIFIER
import org.sui.lang.core.MOVE_COMMENTS
import org.sui.lang.core.lexer.createMoveLexer
import org.sui.lang.core.tokenSetOf

class MvWordsScanner : DefaultWordsScanner(
    createMoveLexer(),
    tokenSetOf(IDENTIFIER),
    MOVE_COMMENTS,
    tokenSetOf(BYTE_STRING_LITERAL)
)
