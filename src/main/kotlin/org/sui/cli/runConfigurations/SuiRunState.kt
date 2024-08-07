package org.sui.cli.runConfigurations

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.filters.Filter
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import org.sui.cli.MoveFileHyperlinkFilter

abstract class SuiRunStateBase(
    environment: ExecutionEnvironment,
    val runConfiguration: CommandConfigurationBase,
    val config: CommandConfigurationBase.CleanConfiguration.Ok
) : CommandLineState(environment) {

    val project = environment.project
    val commandLine: SuiCommandLine = config.cmd

    override fun startProcess(): ProcessHandler {
        commandLine.appendSkipFetchDependencyIfNeeded(project)
        val generalCommandLine = commandLine.toGeneralCommandLine(config.suiPath)
        val handler = KillableColoredProcessHandler(generalCommandLine)
        consoleBuilder.console.attachToProcess(handler)
        ProcessTerminatedListener.attach(handler)  // shows exit code upon termination
        return handler
    }

    protected fun createFilters(): Collection<Filter> {
        val filters = mutableListOf<Filter>()
        val wd = commandLine.workingDirectory
        if (wd != null) {
            filters.add(MoveFileHyperlinkFilter(project, wd))
        }
        return filters
    }
}

class SuiRunState(
    environment: ExecutionEnvironment,
    runConfiguration: CommandConfigurationBase,
    config: CommandConfigurationBase.CleanConfiguration.Ok
) :
    SuiRunStateBase(environment, runConfiguration, config) {

    init {
        createFilters().forEach { consoleBuilder.addFilter(it) }
    }

}
