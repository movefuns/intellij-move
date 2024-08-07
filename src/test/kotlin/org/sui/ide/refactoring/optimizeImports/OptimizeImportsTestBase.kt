package org.sui.ide.refactoring.optimizeImports

import org.intellij.lang.annotations.Language
import org.sui.ide.inspections.MvUnusedImportInspection
import org.sui.utils.tests.MvTestBase
import org.sui.utils.tests.WithEnabledInspections

@WithEnabledInspections(MvUnusedImportInspection::class)
abstract class OptimizeImportsTestBase : MvTestBase() {

    protected fun doTest(@Language("Sui Move") before: String, @Language("Sui Move") after: String) =
        checkEditorAction(before, after, "OptimizeImports")
}
