package org.sui.utils.tests

import com.intellij.psi.formatter.FormatterTestCase
import org.intellij.lang.annotations.Language
import org.sui.utils.tests.base.TestCase

abstract class MvFormatterTestBase : FormatterTestCase() {
    override fun getTestDataPath() = "src/test/resources"
    override fun getBasePath(): String = "org/move/ide/formatter.fixtures"
    override fun getFileExtension() = "move"

    override fun getTestName(lowercaseFirstLetter: Boolean): String {
        val camelCase = super.getTestName(lowercaseFirstLetter)
        return TestCase.camelOrWordsToSnake(camelCase)
    }

    override fun doTextTest(@Language("Move") text: String, @Language("Move") textAfter: String) {
        check(text.trimIndent() != textAfter.trimIndent()) {
            "Formatter before and after should be different"
        }
        super.doTextTest(text.trimIndent(), textAfter.trimIndent())
    }

}
