package org.sui.lang.parser

import org.sui.utils.tests.parser.MvParsingTestCase

class SpecsParsingTest : MvParsingTestCase("specs") {
    fun `test scopes`() = doTest()
    fun `test conditions`() = doTest()
    fun `test forall exists`() = doTest()
    fun `test pragma`() = doTest()

    fun `test apply`() = doTest()
    fun `test spec statements`() = doTest(true, false)
    fun `test spec file`() = doTest()
    fun `test spec properties`() = doTest()
    fun `test spec variables`() = doTest()

    fun doTest() {
        super.doTest(true, true)
    }
}
