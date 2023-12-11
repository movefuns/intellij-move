package org.sui.cli.runConfigurations.sui.any

import com.intellij.execution.RunManagerEx
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import org.sui.cli.runConfigurations.sui.SuiConfigurationType

class AnyCommandConfigurationFactory(
    configurationType: ConfigurationType
) :
    ConfigurationFactory(configurationType) {

    override fun getId(): String = "AnyCommand"

    override fun getName(): String = "any command"

    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return AnyCommandConfiguration(project, this)
    }

    companion object {
        fun createTemplateRunConfiguration(
            project: Project,
            configurationName: String,
            save: Boolean
        ): RunnerAndConfigurationSettings {
            val runManager = RunManagerEx.getInstanceEx(project)
            val runConfiguration = runManager.createConfiguration(
                configurationName,
                AnyCommandConfigurationFactory(SuiConfigurationType.getInstance())
            )
            if (save) {
                runManager.setTemporaryConfiguration(runConfiguration)
            }
            return runConfiguration
        }
    }
}
