package org.sui.ide

import org.sui.utils.tests.MvTestBase

class FoldingBuilderTest : MvTestBase() {
    override val dataPath = "org.sui.ide/folding.fixtures"

    fun `test script`() = doTest()
    fun `test module`() = doTest()
    fun `test script with parameters`() = doTest()

    private fun doTest() {
        myFixture.testFolding("$testDataPath/$fileName")
    }
}
