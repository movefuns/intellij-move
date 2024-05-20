package org.sui.cli.runConfigurations.sui

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.project.Project
import org.sui.cli.moveProjectsService
import org.sui.cli.runConfigurations.CommandConfigurationBase
import org.sui.cli.settings.moveSettings
import org.sui.stdext.toPathOrNull
import java.nio.file.Path

class SuiCommandConfiguration(
    project: Project,
    factory: ConfigurationFactory
) :
    CommandConfigurationBase(project, factory) {

    init {
        workingDirectory = if (!project.isDefault) {
            project.moveProjectsService.allProjects.firstOrNull()?.contentRootPath
        } else {
            null
        }
    }

    override fun getCliPath(project: Project): Path? {
        return project.moveSettings.state.suiPath
            .takeIf { it?.isNotBlank() ?: false }
            ?.toPathOrNull()
    }

    override fun getConfigurationEditor() = SuiCommandConfigurationEditor()
}
