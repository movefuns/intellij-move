package org.sui.cli.runConfigurations.aptos.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project

class RunCommandConfigurationFactory(
    configurationType: ConfigurationType
) : ConfigurationFactory(configurationType) {

    override fun getId(): String = "RunCommand"

    override fun getName(): String = "run"

    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return RunCommandConfiguration(project, this)
    }
}
