package org.sui.cli.runConfigurations.sui.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.project.Project
import org.sui.cli.moveProjects
import org.sui.cli.runConfigurations.sui.FunctionCallConfigurationBase
import org.sui.cli.runConfigurations.sui.FunctionCallConfigurationEditor

class BuildCommandConfiguration(
    project: Project,
    factory: ConfigurationFactory
) : FunctionCallConfigurationBase(project, factory, BuildCommandConfigurationHandler()) {

    override fun getConfigurationEditor(): FunctionCallConfigurationEditor<BuildCommandConfiguration> {
        val moveProject = project.moveProjects.allProjects.first()
        return FunctionCallConfigurationEditor(
            BuildCommandConfigurationHandler(),
            moveProject,
        )
    }
}
