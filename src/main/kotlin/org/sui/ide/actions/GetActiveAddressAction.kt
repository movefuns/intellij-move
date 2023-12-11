package org.sui.ide.actions

import com.intellij.execution.process.ProcessOutput
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.sui.cli.settings.suiExec
import org.sui.common.NOTIFACATION_GROUP
import org.sui.ide.utils.ChecCliPath.Companion.checkCliPath

@Suppress("DEPRECATION")
class GetActiveAddressAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {

        val project = e.project ?: return


        if (checkCliPath(project)) {
            val onProcessComplete: (ProcessOutput?) -> Unit = { output ->
                if (output != null && output.exitCode == 0) {
                    Notifications.Bus.notify(
                        Notification(
                            NOTIFACATION_GROUP,
                            "Active address",
                            output.stdout,
                            NotificationType.INFORMATION
                        )
                    )
                } else {
                    Notifications.Bus.notify(
                        Notification(
                            NOTIFACATION_GROUP,
                            "Active address",
                            "Execution failure, please check the sui cli path.",
                            NotificationType.ERROR
                        )
                    )
                }
            }
            project.suiExec.toExecutor()?.simpleCommand(project, "client", listOf("active-address"), onProcessComplete)
        }
    }

}
