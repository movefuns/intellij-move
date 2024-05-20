package org.sui.ide.actions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessOutput
import com.intellij.execution.util.ExecUtil
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import org.sui.common.NOTIFACATION_GROUP
import org.sui.ide.dialog.ObjectDialog

class OpenObjectListAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {

        val project = e.project ?: return
        val commandLine = GeneralCommandLine("sui", "client", "objects", "--json")

        val task = Runnable {
            val processOutput: ProcessOutput = ExecUtil.execAndGetOutput(commandLine)

            val addressesJson = processOutput.stdout + processOutput.stderr
            val extractData = extractData(addressesJson, project)
            ApplicationManager.getApplication().invokeLater {
                ObjectDialog(extractData).show()
            }
        }
        ProgressManager.getInstance().runProcessWithProgressSynchronously(task, "Processing", false, project)
    }

    data class SuiObject(
        val objectId: String,
        val version: String,
        val digest: String,
        val type: String
    )

    fun extractData(s: String, project: Project): List<SuiObject> {
        val gson = Gson()
        if (s.contains("This address has no owned objects")) {
            val notificationType = NotificationType.WARNING
            val notification =
                Notification(NOTIFACATION_GROUP, "Objects", "This address has no owned objects", notificationType)
            Notifications.Bus.notify(notification, project)
        }
        val type = object : TypeToken<List<Map<String, SuiObject>>>() {}.type
        val jsonList: List<Map<String, SuiObject>> = gson.fromJson(s, type)

        return jsonList.mapNotNull { it["data"] }
    }
}

