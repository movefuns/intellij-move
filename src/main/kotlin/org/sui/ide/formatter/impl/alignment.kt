package org.sui.ide.formatter.impl

import org.sui.ide.formatter.MoveFmtBlock
import org.sui.ide.formatter.MvAlignmentStrategy
import org.sui.lang.MvElementTypes.*

fun MoveFmtBlock.getAlignmentStrategy(): MvAlignmentStrategy = when (node.elementType) {
    FUNCTION_PARAMETER_LIST, VALUE_ARGUMENT_LIST ->
        MvAlignmentStrategy
            .shared()
            .alignUnlessBlockDelim()
            .alignIf(ctx.commonSettings.ALIGN_MULTILINE_PARAMETERS)
    TYPE_PARAMETER_LIST ->
        MvAlignmentStrategy
            .wrap()
            .alignIf(TYPE_PARAMETER)
    else -> MvAlignmentStrategy.NullStrategy

}

fun MvAlignmentStrategy.alignUnlessBlockDelim(): MvAlignmentStrategy =
    alignIf { c, p, _ -> !c.isDelimiterOfCurrentBlock(p) }
