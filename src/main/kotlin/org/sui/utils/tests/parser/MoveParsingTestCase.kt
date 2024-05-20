package org.sui.utils.tests.parser

import com.intellij.testFramework.ParsingTestCase
import org.jetbrains.annotations.NonNls
import org.sui.lang.MoveParserDefinition
import org.sui.utils.tests.base.TestCase

abstract class MvParsingTestCase(@NonNls dataPath: String) : ParsingTestCase(
    "org/sui/lang/parser/$dataPath",
    "move",
    true,
    MoveParserDefinition()
) {
    override fun getTestDataPath(): String = "src/test/resources"

    override fun getTestName(lowercaseFirstLetter: Boolean): String {
        val camelCase = super.getTestName(lowercaseFirstLetter)
        return TestCase.camelOrWordsToSnake(camelCase)
    }
}
