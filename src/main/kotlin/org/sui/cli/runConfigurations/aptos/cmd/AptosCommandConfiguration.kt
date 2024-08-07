package org.sui.cli.runConfigurations.aptos.cmd

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.testframework.TestConsoleProperties
import com.intellij.execution.testframework.actions.ConsolePropertiesProvider
import com.intellij.openapi.project.Project
import org.sui.cli.moveProjectsService
import org.sui.cli.runConfigurations.CommandConfigurationBase
import org.sui.cli.runConfigurations.test.AptosTestConsoleProperties

class AptosCommandConfiguration(
    project: Project,
    factory: ConfigurationFactory
) :
    CommandConfigurationBase(project, factory),
    ConsolePropertiesProvider {

    init {
        workingDirectory = if (!project.isDefault) {
            project.moveProjectsService.allProjects.firstOrNull()?.contentRootPath
        } else {
            null
        }
    }

    override fun getConfigurationEditor() = AptosCommandConfigurationEditor()

    override fun createTestConsoleProperties(executor: Executor): TestConsoleProperties? {
        val config = clean().ok ?: return null
        return if (showTestToolWindow(config.cmd)) {
            AptosTestConsoleProperties(this, executor)
        } else {
            null
        }
    }
}
