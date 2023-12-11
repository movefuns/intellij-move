package org.sui.cli.projectAware

import org.sui.utils.tests.MvProjectTestBase

class ImportProjectTest : MvProjectTestBase() {
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
