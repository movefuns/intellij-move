package org.sui.cli.externalSystem

import org.sui.utils.tests.MvProjectTestBase

class ImportTreeProjectTest : MvProjectTestBase() {
    fun `test import project with circular dependencies no stackoverflow`() {
        testProject {
            moveToml(
                """
            [package]
            name = "MyPackage"
                
            [dependencies]
            MyLocal = { local = "." }
            """
            )
            sources { main("/*caret*/") }
        }
    }
}
