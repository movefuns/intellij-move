package org.sui.ide.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import org.sui.cli.moveProjects
import org.sui.openapiext.saveAllDocuments

class RefreshMoveProjectsAction : DumbAwareAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        saveAllDocuments()
        project.moveProjects.refreshAllProjects()
    }
}
