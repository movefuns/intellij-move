package org.sui.cli.runConfigurations.sui.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project

class TestCommandConfigurationFactory(
    configurationType: ConfigurationType
) : ConfigurationFactory(configurationType) {

    override fun getId(): String = "TestCommand"

    override fun getName(): String = "test"

    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return TestCommandConfiguration(project, this)
    }
}
