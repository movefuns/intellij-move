package org.sui.cli

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.startup.StartupManager
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.PlatformProjectOpenProcessor
import com.intellij.projectImport.ProjectOpenProcessor
import org.sui.cli.runConfigurations.addDefaultBuildRunConfiguration
import org.sui.cli.settings.MoveProjectSettingsService
import org.sui.cli.settings.moveSettings
import org.sui.ide.MoveIcons
import org.sui.ide.newProject.openFile
import org.sui.ide.notifications.updateAllNotifications
import org.sui.openapiext.contentRoots
import org.sui.openapiext.suiBuildRunConfigurations
import org.sui.openapiext.suiRunConfigurations
import javax.swing.Icon

class SuiProjectOpenProcessor : ProjectOpenProcessor() {
    override val name: String get() = "Move"
    override val icon: Icon get() = MoveIcons.MOVE_LOGO

    override fun canOpenProject(file: VirtualFile): Boolean =
        FileUtil.namesEqual(file.name, Consts.MANIFEST_FILE)
                || (file.isDirectory && file.findChild(Consts.MANIFEST_FILE) != null)

    override fun doOpenProject(
        virtualFile: VirtualFile,
        projectToClose: Project?,
        forceOpenInNewFrame: Boolean,
    ): Project? {
        val platformOpenProcessor = PlatformProjectOpenProcessor.getInstance()
        return platformOpenProcessor.doOpenProject(
            virtualFile,
            projectToClose,
            forceOpenInNewFrame
        )?.also { it ->
            StartupManager.getInstance(it).runAfterOpened {
                println("open project")
                // create default build configuration if it doesn't exist
                if (it.suiBuildRunConfigurations().isEmpty()) {
                    val isEmpty = it.suiRunConfigurations().isEmpty()
                    it.addDefaultBuildRunConfiguration(isSelected = isEmpty)
                }
                val defaultProjectSettings = ProjectManager.getInstance().defaultMoveSettings
                it.moveSettings.modify {
                    val suiPath = defaultProjectSettings?.state?.suiPath ?: ""
                    it.suiPath = suiPath
                }

                // opens Move.toml file
                val packageRoot = it.contentRoots.firstOrNull()
                if (packageRoot != null) {
                    val manifest = packageRoot.findChild(Consts.MANIFEST_FILE)
                    if (manifest != null) {
                        it.openFile(manifest)
                    }
                    updateAllNotifications(it)
                }
                it.moveProjects.refreshAllProjects()

            }
        }
    }
}

fun defaultProjectSettings(): MoveProjectSettingsService? = ProjectManager.getInstance().defaultMoveSettings

val ProjectManager.defaultMoveSettings: MoveProjectSettingsService?
    get() = this.defaultProject.getService(MoveProjectSettingsService::class.java)
