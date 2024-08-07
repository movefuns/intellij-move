package org.sui.cli.filters

import org.sui.cli.MoveFileHyperlinkFilter
import org.sui.utils.tests.HighlightFilterTestBase
import java.nio.file.Paths

class MoveFileHyperlinkFilterTest : HighlightFilterTestBase() {
    fun `test compilation failure hyperlink`() {
        val rootDir = projectDir.toNioPath()
        checkHighlights(
            MoveFileHyperlinkFilter(project, rootDir),
            {
                moveToml(
                    """
                    [package]
                    name = "MyPackage"
                    
                    [addresses]
                    main = "0x1234"
                """
                )
                sources {
                    move(
                        "main.move", """
                        module main::main {
                            fun main() {/*caret*/}
                        }                    
                    """
                    )
                }
            },
            """
        ┌─ ${Paths.get(rootDir.toString(), "sources", "main.move")}:1:1
            """.trimIndent(),
            """
        ┌─ [${Paths.get(rootDir.toString(), "sources", "main.move")}:1:1 -> main.move]
            """.trimIndent(),
        )
    }
}
