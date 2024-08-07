package org.sui.cli.runConfigurations.sui.view

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.project.Project
import org.sui.cli.runConfigurations.sui.FunctionCallConfigurationBase
import org.sui.cli.runConfigurations.sui.FunctionCallConfigurationEditor

class ViewCommandConfiguration(
    project: Project,
    factory: ConfigurationFactory
) : FunctionCallConfigurationBase(project, factory, ViewCommandConfigurationHandler()) {

    override fun getConfigurationEditor(): FunctionCallConfigurationEditor<ViewCommandConfiguration> {
        return FunctionCallConfigurationEditor(project, ViewCommandConfigurationHandler())
    }
}
