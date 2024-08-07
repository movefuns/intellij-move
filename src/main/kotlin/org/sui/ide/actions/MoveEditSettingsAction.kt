package org.sui.ide.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import org.sui.cli.settings.PerProjectSuiConfigurable
import org.sui.openapiext.showSettingsDialog

class MoveEditSettingsAction : DumbAwareAction("Sui Settings") {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.isEnabledAndVisible = e.project != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.showSettingsDialog<PerProjectSuiConfigurable>()
    }
}
