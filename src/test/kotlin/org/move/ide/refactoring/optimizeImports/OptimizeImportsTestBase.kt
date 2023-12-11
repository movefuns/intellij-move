package org.move.ide.refactoring.optimizeImports

import org.intellij.lang.annotations.Language
import org.sui.ide.inspections.SuiMvUnusedImportInspection
import org.sui.utils.tests.WithEnabledInspections
import org.sui.utils.tests.MvTestBase

@WithEnabledInspections(SuiMvUnusedImportInspection::class)
abstract class OptimizeImportsTestBase: MvTestBase() {

    protected fun doTest(@Language("Move") before: String, @Language("Move") after: String) =
        checkEditorAction(before, after, "OptimizeImports")
}
