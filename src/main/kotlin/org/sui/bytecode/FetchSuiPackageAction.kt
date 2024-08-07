package org.sui.bytecode

import com.intellij.openapi.actionSystem.AnActionEvent
import org.sui.cli.runConfigurations.sui.RunSuiCommandActionBase

class FetchSuiPackageAction : RunSuiCommandActionBase("Fetch on-chain package") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val parametersDialog = FetchSuiPackageDialog(project)
        parametersDialog.show()
    }

}