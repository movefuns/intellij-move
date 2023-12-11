package org.sui.cli.runConfigurations

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import org.sui.cli.runConfigurations.sui.SuiConfigurationType
import org.sui.cli.runConfigurations.sui.any.AnyCommandConfiguration
import org.sui.stdext.toPath

private val LOG = logger<Project>()

fun Project.addDefaultBuildRunConfiguration(isSelected: Boolean = false): RunnerAndConfigurationSettings {
    val runManager = RunManager.getInstance(this)
    val configurationFactory = DefaultRunConfigurationFactory(runManager, this)
    val configuration = configurationFactory.createSuiBuildConfiguration()

    runManager.addConfiguration(configuration)
    LOG.info("Default \"Build\" run configuration is added")
    if (isSelected) {
        runManager.selectedConfiguration = configuration
    }
    return configuration
}

private class DefaultRunConfigurationFactory(val runManager: RunManager, val project: Project) {

    fun createSuiBuildConfiguration(): RunnerAndConfigurationSettings =
        runManager.createConfiguration("Build", SuiConfigurationType::class.java)
            .apply {
                (configuration as? AnyCommandConfiguration)?.apply {
                    command = "move build"
                    workingDirectory = project.basePath?.toPath()
                }
            }
}
