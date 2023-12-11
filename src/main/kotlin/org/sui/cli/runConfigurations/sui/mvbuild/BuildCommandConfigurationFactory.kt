package org.sui.cli.runConfigurations.sui.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project

class BuildCommandConfigurationFactory(
    configurationType: ConfigurationType
) : ConfigurationFactory(configurationType) {

    override fun getId(): String = "BuildCommand"

    override fun getName(): String = "Build"

    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return BuildCommandConfiguration(project, this)
    }
}
