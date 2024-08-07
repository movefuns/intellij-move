package org.sui.ide.actions

import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import org.sui.cli.sdks.DownloadSuiSdkDialog
import org.sui.cli.sdks.DownloadSuiSdkTask
import org.sui.cli.sdks.SuiSdk
import org.sui.cli.sdks.sdksService
import org.sui.ide.notifications.MvNotifications

@Suppress("DialogTitleCapitalization")
class DownloadSuiSDKAction : DumbAwareAction("Download pre-compiled binary from GitHub") {

    var onFinish: (SuiSdk) -> Unit = { _ -> }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        val sdkParametersDialog = DownloadSuiSdkDialog(e.project)
        val isOk = sdkParametersDialog.showAndGet()
        if (isOk) {
            // download Sui SDK
            val sdkVersion = sdkParametersDialog.versionField.text
            val network = sdkParametersDialog.networkComboBox.selectedItem as String
            val project: Project = e.project ?: return
            val sdksDir = sdksService().sdksDir

            if (sdksDir == null) {
                MvNotifications.pluginNotifications().createNotification(
                    "Sui SDK Download",
                    "The SDKs directory does not exist.",
                    NotificationType.ERROR
                ).notify(project)
                return
            }
            val archive = SuiSdk(sdksDir, sdkVersion, network)
            ProgressManager.getInstance()
                .run(DownloadSuiSdkTask(archive, onFinish))
        }
    }

    companion object {
        fun create(onFinish: (SuiSdk) -> Unit): DownloadSuiSDKAction {
            val action = DownloadSuiSDKAction()
            action.onFinish = onFinish
            return action
        }
    }
}
