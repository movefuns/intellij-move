/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.sui.utils.tests

import com.intellij.execution.filters.Filter
import com.intellij.execution.filters.OpenFileHyperlinkInfo
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile

/**
 * Base class for tests of output highlighting filters.
 */
abstract class HighlightFilterTestBase : MvProjectTestBase() {
    val projectDir: VirtualFile
        get() =
            myFixture.tempDirFixture.getFile("")
                ?: error("Can't get temp directory for console filter tests")

    override fun setUp() {
        super.setUp()
        runWriteAction {
            projectDir
                .createChildDirectory(this, "src")
                .createChildData(this, "main.move")
        }
    }

    protected fun checkNoHighlights(filter: Filter, text: String) {
        val items = filter.applyFilter(text, text.length)?.resultItems ?: return
        check(items.size == 0) {
            "Expected zero highlights, got $items"
        }
    }

    protected fun checkHighlights(
        filter: Filter,
        treeBuilder: TreeBuilder,
        before: String,
        after: String,
        lineIndex: Int = 0
    ) {
        testProject(treeBuilder)
        checkHighlights(filter, before, after, lineIndex)
    }

    companion object {
        fun checkHighlights(filter: Filter, before: String, after: String, lineIndex: Int = 0) {
            val line = before.splitLinesKeepSeparators()[lineIndex]
            val result = checkNotNull(filter.applyFilter(line, before.length)) {
                "No match in \"${StringUtil.escapeStringCharacters(line)}\""
            }
            var checkText = before
            val items = ArrayList(result.resultItems)
            items.sortByDescending { it.highlightEndOffset }
            items.forEach { item ->
                val range = IntRange(item.highlightStartOffset, item.highlightEndOffset - 1)
                var itemText = before.substring(range)
                (item.hyperlinkInfo as? OpenFileHyperlinkInfo)?.let { link ->
                    itemText = "$itemText -> ${link.descriptor?.file?.name}"
                }
                checkText = checkText.replaceRange(range, "[$itemText]")
            }
            checkText = checkText.splitLinesKeepSeparators()[lineIndex]
            assertEquals(after, checkText)
        }

        private fun String.splitLinesKeepSeparators() = split("(?<=\n)".toRegex())
    }
}
