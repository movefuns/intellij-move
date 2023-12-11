package org.sui.ide.actions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.execution.process.ProcessOutput
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import org.sui.cli.settings.suiExec
import org.sui.common.NOTIFACATION_GROUP
import org.sui.ide.dialog.EnvDialog
import org.sui.ide.utils.ChecCliPath

class OpenSwitchEnvsDialogAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {

        val project = e.project ?: return

        if (ChecCliPath.checkCliPath(project)) {
            val onProcessComplete: (ProcessOutput?) -> Unit = { output ->
                if (output != null && output.exitCode == 0) {
                    println("Process executed successfully with output: ${output.stdout}")
                    val envJSON = output.stdout
                    val gson = Gson()
                    val type = object : TypeToken<List<Any>>() {}.type
                    val parsedList: List<Any> = gson.fromJson(envJSON, type)
                    val netList =
                        gson.fromJson<List<NetEnv>>(
                            gson.toJson(parsedList[0]),
                            object : TypeToken<List<NetEnv>>() {}.type
                        )
                    val active = parsedList[1] as String

                    val envs = Envs(active, netList)

                    ApplicationManager.getApplication().invokeLater {
                        EnvDialog(envs).show()
                    }
                } else {
                    Notifications.Bus.notify(
                        Notification(
                            NOTIFACATION_GROUP,
                            "Switch env",
                            "Execution failure, please check the sui cli path.",
                            NotificationType.ERROR
                        )
                    )
                }
            }
            project.suiExec.toExecutor()?.simpleCommand(project, "client", listOf("envs", "--json"), onProcessComplete)
        }
    }

    data class Envs(
        val active: String,
        val netList: List<NetEnv>
    )

    data class NetEnv(
        val alias: String,
        val rpc: String,
        val ws: String?
    )
}
