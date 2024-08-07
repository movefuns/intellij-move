package org.sui.cli.runConfigurations

import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.impl.RunConfigurationBeforeRunProvider
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.openapi.project.Project
import com.intellij.task.*
import org.jetbrains.concurrency.Promise
import org.jetbrains.concurrency.resolvedPromise
import org.sui.cli.moveProjectRoot
import org.sui.cli.runConfigurations.sui.cmd.SuiCommandConfiguration
import org.sui.ide.newProject.ProjectInitializationSteps
import org.sui.openapiext.runManager
import org.sui.openapiext.suiCommandConfigurationsSettings


@Suppress("UnstableApiUsage")
class SuiBuildTaskRunner : ProjectTaskRunner() {
    override fun canRun(projectTask: ProjectTask): Boolean {
        return projectTask is ModuleBuildTask && projectTask.module.moveProjectRoot != null
    }

    override fun run(
        project: Project,
        context: ProjectTaskContext,
        vararg tasks: ProjectTask?
    ): Promise<Result> {
        val compileConfigurationWithSettings =
            project.suiCommandConfigurationsSettings()
                .find { (it.configuration as SuiCommandConfiguration).command == "move build" }
                ?: ProjectInitializationSteps.createDefaultCompileConfiguration(project, false)
//        val compileConfiguration =
//            project.aptosCommandConfigurations().find { it.command.startsWith("move compile") }
////            project.aptosBuildRunConfigurations().firstOrNull()
//                ?: ProjectInitialization.createDefaultCompileConfiguration(project, false)
//        val configurationSettings =
//            project.runManager.findConfigurationByName(compileConfiguration.name)
//                ?: return resolvedPromise(TaskRunnerResults.ABORTED)
        project.runManager.selectedConfiguration = compileConfigurationWithSettings

        val environment = ExecutionEnvironmentBuilder.createOrNull(
            DefaultRunExecutor.getRunExecutorInstance(),
            compileConfigurationWithSettings
        )?.build() ?: return resolvedPromise(TaskRunnerResults.ABORTED)

        val success =
            RunConfigurationBeforeRunProvider.doExecuteTask(environment, compileConfigurationWithSettings, null)
        if (success) {
            return resolvedPromise(TaskRunnerResults.SUCCESS)
        } else {
            return resolvedPromise(TaskRunnerResults.FAILURE)
        }
    }
}

