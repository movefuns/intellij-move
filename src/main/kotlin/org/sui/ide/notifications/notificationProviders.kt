package org.sui.ide.notifications

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationProvider
import com.intellij.ui.EditorNotifications
import org.sui.cli.moveProjects
import org.sui.cli.settings.MoveSettingsChangedEvent
import org.sui.cli.settings.MoveSettingsListener
import org.sui.cli.settings.PerProjectMoveConfigurable
import org.sui.cli.settings.moveSettings
import org.sui.lang.isMoveOrManifest
import org.sui.openapiext.common.isUnitTestMode
import org.sui.openapiext.showSettings
import java.util.function.Function
import javax.swing.JComponent

fun updateAllNotifications(project: Project) {
    EditorNotifications.getInstance(project).updateAllNotifications()
}

class UpdateNotificationsOnSettingsChangeListener(val project: Project) : MoveSettingsListener {

    override fun moveSettingsChanged(e: MoveSettingsChangedEvent) {
        updateAllNotifications(project)
    }
}

class InvalidSuiBinaryNotification(
    private val project: Project
) : EditorNotificationProvider,
    DumbAware {

    override fun collectNotificationData(
        project: Project,
        file: VirtualFile
    ): Function<in FileEditor, out JComponent?> {
        return Function { createNotificationPanel(file, project) }
    }

    fun createNotificationPanel(file: VirtualFile, project: Project): EditorNotificationPanel? {
        if (isUnitTestMode) return null
        if (!file.isMoveOrManifest) return null

        if (project.moveProjects.allProjects.isEmpty()) {
            project.moveProjects.refreshAllProjects()
        }

        if (isNotificationDisabled(file)) return null

        if (project.moveSettings.state.isValidExec) return null

        return EditorNotificationPanel().apply {
            text = "Sui binary path is not provided or invalid"
            createActionLabel("Configure") {
                project.showSettings<PerProjectMoveConfigurable>()
            }
            createActionLabel("Do not show again") {
                disableNotification(file)
                updateAllNotifications(project)
            }
        }
    }

    private fun disableNotification(file: VirtualFile) {
        PropertiesComponent.getInstance(project).setValue(file.disablingKey, true)
    }

    private fun isNotificationDisabled(file: VirtualFile): Boolean =
        PropertiesComponent.getInstance(project).getBoolean(file.disablingKey)

    private val VirtualFile.disablingKey: String get() = NOTIFICATION_STATUS_KEY + path

    companion object {
        private const val NOTIFICATION_STATUS_KEY = "org.move.hideMoveNotifications"
    }
}
