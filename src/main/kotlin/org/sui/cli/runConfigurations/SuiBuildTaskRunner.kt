package org.sui.cli.runConfigurations

import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.impl.RunConfigurationBeforeRunProvider
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.openapi.project.Project
import com.intellij.task.*
import org.jetbrains.concurrency.Promise
import org.jetbrains.concurrency.resolvedPromise
import org.sui.cli.moveProjectRoot
import org.sui.openapiext.runManager
import org.sui.openapiext.suiBuildRunConfigurations

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
        val buildConfiguration =
            project.suiBuildRunConfigurations().firstOrNull()
                ?: project.addDefaultBuildRunConfiguration().configuration
        val configurationSettings =
            project.runManager.findConfigurationByName(buildConfiguration.name)
                ?: return resolvedPromise(TaskRunnerResults.ABORTED)
        project.runManager.selectedConfiguration = configurationSettings

        val environment = ExecutionEnvironmentBuilder.createOrNull(
            DefaultRunExecutor.getRunExecutorInstance(),
            configurationSettings
        )?.build() ?: return resolvedPromise(TaskRunnerResults.ABORTED)

        val success =
            RunConfigurationBeforeRunProvider.doExecuteTask(environment, configurationSettings, null)
        if (success) {
            return resolvedPromise(TaskRunnerResults.SUCCESS)
        } else {
            return resolvedPromise(TaskRunnerResults.FAILURE)
        }
    }
}
