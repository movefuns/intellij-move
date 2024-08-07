package org.sui.ide

import com.intellij.testFramework.UsefulTestCase
import org.intellij.lang.annotations.Language
import org.sui.utils.tests.MvTestBase

class MvBreadcrumbsProviderTest : MvTestBase() {
    fun `test breadcrumbs`() = doTextTest(
        """
        module 0x1::M {
            fun main() {
                while (true) if (true) /*caret*/;
            }
        }
    """, """
        0x1::M
        main()
        {...}
        while (true)
        if (true)
    """
    )

    private fun doTextTest(@Language("Sui Move") content: String, info: String) {
        InlineFile(content.trimIndent())
        val crumbs = myFixture.breadcrumbsAtCaret.joinToString(separator = "\n") { it.text }
        UsefulTestCase.assertSameLines(info.trimIndent(), crumbs)
    }
}
