package org.sui.ide.newProject

import com.intellij.ide.util.projectWizard.AbstractNewProjectStep
import com.intellij.ide.util.projectWizard.CustomStepProjectGenerator
import com.intellij.openapi.components.service
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.impl.welcomeScreen.AbstractActionWithPanel
import com.intellij.platform.DirectoryProjectGenerator
import com.intellij.platform.DirectoryProjectGeneratorBase
import com.intellij.platform.ProjectGeneratorPeer
import org.sui.cli.PluginApplicationDisposable
import org.sui.cli.moveProjects
import org.sui.cli.runConfigurations.addDefaultBuildRunConfiguration
import org.sui.cli.settings.SuiSettingsPanel
import org.sui.cli.settings.moveSettings
import org.sui.ide.MoveIcons
import org.sui.ide.notifications.updateAllNotifications
import org.sui.stdext.unwrapOrThrow
import org.sui.openapiext.computeWithCancelableProgress

data class SuiProjectConfig(
    val panelData: SuiSettingsPanel.PanelData,
)

class SuiProjectGenerator: DirectoryProjectGeneratorBase<SuiProjectConfig>(),
                             CustomStepProjectGenerator<SuiProjectConfig> {

    private val disposable = service<PluginApplicationDisposable>()

    override fun getName() = "Sui"
    override fun getLogo() = MoveIcons.SUI_LOGO
    override fun createPeer(): ProjectGeneratorPeer<SuiProjectConfig> = SuiProjectGeneratorPeer(disposable)

    override fun generateProject(
        project: Project,
        baseDir: VirtualFile,
        projectConfig: SuiProjectConfig,
        module: Module
    ) {
        val suiExecutor = projectConfig.panelData.suiExec.toExecutor() ?: return
        val packageName = project.name

        val manifestFile =
            project.computeWithCancelableProgress("Generating Sui Project...") {
                val manifestFile = suiExecutor.moveNew(
                    project,
                    disposable,
                    rootDirectory = baseDir,
                    packageName = packageName
                )
                    .unwrapOrThrow() // TODO throw? really??

                manifestFile
            }


        project.moveSettings.modify {
            it.suiPath = projectConfig.panelData.suiExec.pathToSettingsFormat()
        }
        project.addDefaultBuildRunConfiguration(isSelected = true)
        project.openFile(manifestFile)

        updateAllNotifications(project)
        project.moveProjects.refreshAllProjects()
    }

    override fun createStep(
        projectGenerator: DirectoryProjectGenerator<SuiProjectConfig>,
        callback: AbstractNewProjectStep.AbstractCallback<SuiProjectConfig>
    ): AbstractActionWithPanel =
        SuiProjectConfigStep(projectGenerator)
}
