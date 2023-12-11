package org.sui.ide.utils

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import org.sui.cli.settings.suiExec
import org.sui.common.NOTIFACATION_GROUP
import org.sui.ide.actions.OpenSettingsAction
import org.sui.stdext.isExecutableFile
import org.sui.stdext.toPathOrNull
import javax.swing.event.HyperlinkEvent

class ChecCliPath {

    companion object {
        fun checkCliPath(project: Project): Boolean {
            val path = project.suiExec.execPath.toPathOrNull()
            if (path == null || !path.isExecutableFile()) {
                val notification = NotificationGroupManager.getInstance()
                    .getNotificationGroup(NOTIFACATION_GROUP)
                    .createNotification(
                        "The Sui Cli was not found",
                        "Please configure the Sui Cli path first.<br><a href='openSettings'>Open Setting</a>",
                        NotificationType.INFORMATION
                    )
                    .setListener { n, e ->
                        if (e.eventType == HyperlinkEvent.EventType.ACTIVATED && e.description == "openSettings") {
                            val dataContext = DataContext { dataId ->
                                if (CommonDataKeys.PROJECT.`is`(dataId)) project else null
                            }
                            OpenSettingsAction().actionPerformed(
                                AnActionEvent.createFromAnAction(OpenSettingsAction(), null, "MyPlugin", dataContext)
                            )
                        }
                    }
                Notifications.Bus.notify(notification, project)
                return false
            } else {
                return true
            }
        }
    }

}