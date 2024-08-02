package org.sui.ide.actions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.execution.process.ProcessOutput
import com.intellij.execution.util.ExecUtil
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import org.sui.cli.runConfigurations.SuiCommandLine
import org.sui.cli.settings.suiExecPath
import org.sui.ide.dialog.ObjectDialog
import org.sui.ide.notifications.MvNotifications
import org.sui.utils.StringUtils

class OpenObjectListAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {

        val project = e.project ?: return
        if (project.suiExecPath == null) return

        val suiCommandLine = SuiCommandLine("client objects", listOf("--json"))
        val commandLine = suiCommandLine.toGeneralCommandLine(project.suiExecPath!!)

        val task = Runnable {
            val processOutput: ProcessOutput = ExecUtil.execAndGetOutput(commandLine)
            val addressesJson = processOutput.stdout + processOutput.stderr
            val extractData = extractData(addressesJson, project)
            ApplicationManager.getApplication().invokeLater {
                ObjectDialog(extractData).show()
            }
        }
        ProgressManager.getInstance().runProcessWithProgressSynchronously(task, "Processing", true, project)
    }

    data class SuiObject(
        val objectId: String,
        val version: String,
        val digest: String,
        val type: String
    )

    private fun extractData(s: String, project: Project): List<SuiObject> {
        val gson = Gson()
        if (s.contains("This address has no owned objects")) {
            MvNotifications.pluginNotifications().createNotification(
                "Objects",
                "This address has no owned objects",
                NotificationType.WARNING
            ).notify(project)
        }

        val cleanString = StringUtils.cleanJsonListString(s)

        val type = object : TypeToken<List<Map<String, SuiObject>>>() {}.type
        val jsonList: List<Map<String, SuiObject>> = gson.fromJson(cleanString, type)

        return jsonList.mapNotNull { it["data"] }
    }

}

