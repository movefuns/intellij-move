package org.sui.ide.notifications

import com.intellij.ide.impl.isTrusted
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import org.sui.cli.settings.*
import org.sui.lang.isMoveFile
import org.sui.lang.isMoveTomlManifestFile
import org.sui.openapiext.common.isUnitTestMode
import org.sui.openapiext.showSettings

class InvalidBlockchainCliConfiguration(project: Project) : MvEditorNotificationProvider(project),
    DumbAware {

    override val VirtualFile.disablingKey: String get() = NOTIFICATION_STATUS_KEY + path

    override fun createNotificationPanel(file: VirtualFile, project: Project): EditorNotificationPanel? {
        if (isUnitTestMode) return null
        if (!(file.isMoveFile || file.isMoveTomlManifestFile)) return null
        @Suppress("UnstableApiUsage")
        if (!project.isTrusted()) return null
        if (isNotificationDisabled(file)) return null

        val blockchain = project.blockchain
        when (blockchain) {
            Blockchain.APTOS -> {
                if (project.aptosExec.isValid()) return null
            }

            Blockchain.SUI -> {
                if (project.suiPath.isValidExecutable()) return null
            }
        }

        return EditorNotificationPanel().apply {
            text = "$blockchain CLI path is not provided or invalid"
            createActionLabel("Configure") {
                project.showSettings<PerProjectMoveConfigurable>()
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
