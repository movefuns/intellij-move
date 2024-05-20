package org.sui.cli.runConfigurations.aptos.any

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.project.Project
import org.sui.cli.moveProjectsService
import org.sui.cli.runConfigurations.CommandConfigurationBase
import org.sui.cli.settings.aptosPath
import java.nio.file.Path

class AnyCommandConfiguration(
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

    override fun getCliPath(project: Project): Path? = project.aptosPath

    override fun getConfigurationEditor() = AnyCommandConfigurationEditor()
}
