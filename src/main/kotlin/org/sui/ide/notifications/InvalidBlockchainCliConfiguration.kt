package org.sui.ide.notifications

import com.intellij.ide.impl.isTrusted
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import org.sui.cli.settings.PerProjectSuiConfigurable
import org.sui.cli.settings.sui.SuiExecType
import org.sui.cli.settings.suiExecPath
import org.sui.cli.settings.isValidExecutable
import org.sui.cli.settings.moveSettings
import org.sui.lang.isMoveFile
import org.sui.lang.isMoveTomlManifestFile
import org.sui.openapiext.common.isUnitTestMode
import org.sui.openapiext.showSettingsDialog
import org.sui.stdext.getCliFromPATH

class InvalidBlockchainCliConfiguration(project: Project) : MvEditorNotificationProvider(project),
    DumbAware {

    override val VirtualFile.disablingKey: String get() = NOTIFICATION_STATUS_KEY + path

    override fun createNotificationPanel(file: VirtualFile, project: Project): EditorNotificationPanel? {
        if (isUnitTestMode) return null
        if (!(file.isMoveFile || file.isMoveTomlManifestFile)) return null
        @Suppress("UnstableApiUsage")
        if (!project.isTrusted()) return null
        if (isNotificationDisabled(file)) return null

        if (project.suiExecPath.isValidExecutable()) return null

        val suiCliFromPATH = getCliFromPATH("sui")?.toString()
        return EditorNotificationPanel().apply {
            text = "Sui CLI path is not provided or invalid"
            if (suiCliFromPATH != null) {
                createActionLabel("Set to \"$suiCliFromPATH\"") {
                    project.moveSettings.modify {
                        it.suiExecType = SuiExecType.LOCAL
                        it.localSuiPath = suiCliFromPATH
                    }
                }
            }
            createActionLabel("Configure") {
                project.showSettingsDialog<PerProjectSuiConfigurable>()
            }
            createActionLabel("Do not show again") {
                disableNotification(file)
                updateAllNotifications(project)
            }
        }
    }

    companion object {
        private const val NOTIFICATION_STATUS_KEY = "org.sui.hideMoveNotifications"
    }
}
