package org.sui.ide.actions

import com.intellij.execution.process.ProcessOutput
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import org.sui.cli.settings.suiExec
import org.sui.common.NOTIFACATION_GROUP
import org.sui.ide.dialog.AddressDialog
import org.sui.ide.utils.ChecCliPath

class OpenSwitchAddressDialogAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {

        val project = e.project ?: return

        if (ChecCliPath.checkCliPath(project)) {
            val onProcessComplete: (ProcessOutput?) -> Unit = { output ->
                if (output != null && output.exitCode == 0) {
                    val addresses = output.stdout
                    val addressPattern = "0x[a-fA-F0-9]{64}".toRegex()
                    val addressList = addressPattern.findAll(addresses).map { it.value }.toSet().toList()
                    ApplicationManager.getApplication().invokeLater {
                        AddressDialog(addressList).show()
                    }
                } else {
                    Notifications.Bus.notify(
                        Notification(
                            NOTIFACATION_GROUP,
                            "Switch address",
                            "Execution failure, please check the sui cli path.",
                            NotificationType.ERROR
                        )
                    )
                }
            }
            project.suiExec.toExecutor()?.simpleCommand(project, "client", listOf("addresses"), onProcessComplete)
        }
    }

}
