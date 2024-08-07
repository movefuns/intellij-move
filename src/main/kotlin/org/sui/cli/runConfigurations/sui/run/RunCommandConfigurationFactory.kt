package org.sui.cli.runConfigurations.sui.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import org.sui.cli.moveProjectsService
import org.sui.cli.runConfigurations.sui.workingDirectory

class RunCommandConfigurationFactory(
    configurationType: ConfigurationType
) : ConfigurationFactory(configurationType) {

    override fun getId(): String = "CallCommand"

    override fun getName(): String = "call"

    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        val templateConfiguration = RunCommandConfiguration(project, this)
        templateConfiguration.workingDirectory =
            project.moveProjectsService.allProjects.firstOrNull()?.workingDirectory
        return templateConfiguration
    }
}
