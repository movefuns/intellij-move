package org.sui.ide.newProject

import com.intellij.ide.util.projectWizard.AbstractNewProjectStep
import com.intellij.ide.util.projectWizard.CustomStepProjectGenerator
import com.intellij.ide.util.projectWizard.ProjectSettingsStepBase
import com.intellij.openapi.components.service
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.impl.welcomeScreen.AbstractActionWithPanel
import com.intellij.platform.DirectoryProjectGenerator
import com.intellij.platform.DirectoryProjectGeneratorBase
import com.intellij.platform.ProjectGeneratorPeer
import org.sui.cli.PluginApplicationDisposable
import org.sui.cli.moveProjectsService
import org.sui.cli.runConfigurations.sui.Sui
import org.sui.cli.settings.sui.SuiExecType
import org.sui.cli.settings.moveSettings
import org.sui.ide.MoveIcons
import org.sui.openapiext.computeWithCancelableProgress
import org.sui.stdext.unwrapOrThrow

data class MoveProjectConfig(
    val suiExecType: SuiExecType,
    val localSuiPath: String?,
)

class MoveProjectGenerator : DirectoryProjectGeneratorBase<MoveProjectConfig>(),
    CustomStepProjectGenerator<MoveProjectConfig> {

    private val disposable = service<PluginApplicationDisposable>()

    override fun getName() = "Sui Move"
    override fun getLogo() = MoveIcons.SUI_LOGO
    override fun createPeer(): ProjectGeneratorPeer<MoveProjectConfig> = MoveProjectGeneratorPeer(disposable)

    override fun generateProject(
        project: Project,
        baseDir: VirtualFile,
        projectConfig: MoveProjectConfig,
        module: Module
    ) {
        val packageName = project.name
        val suiPath =
            SuiExecType.suiExecPath(projectConfig.suiExecType, projectConfig.localSuiPath)
                ?: error("validated before")
        val sui = Sui(suiPath, disposable)
        val manifestFile =
            project.computeWithCancelableProgress("Generating Sui project...") {
                val manifestFile =
                    sui.init(
                        project,
                        rootDirectory = baseDir,
                        packageName = packageName
                    )
                        .unwrapOrThrow() // TODO throw? really??
                manifestFile
            }
        // update settings (and refresh Sui projects too)
        project.moveSettings.modify {
            it.suiExecType = projectConfig.suiExecType
            it.localSuiPath = projectConfig.localSuiPath
                }

        ProjectInitializationSteps.createDefaultCompileConfigurationIfNotExists(project)
        // NOTE:
        // this cannot be moved to a ProjectActivity, as Move.toml files
        // are not created by the time those activities are executed
        ProjectInitializationSteps.openMoveTomlInEditor(project, manifestFile)

        project.moveProjectsService.scheduleProjectsRefresh("After `sui move init`")
    }

    override fun createStep(
        projectGenerator: DirectoryProjectGenerator<MoveProjectConfig>,
        callback: AbstractNewProjectStep.AbstractCallback<MoveProjectConfig>
    ): AbstractActionWithPanel =
        ConfigStep(projectGenerator)

    class ConfigStep(generator: DirectoryProjectGenerator<MoveProjectConfig>) :
        ProjectSettingsStepBase<MoveProjectConfig>(
            generator,
            AbstractNewProjectStep.AbstractCallback()
        )

}
