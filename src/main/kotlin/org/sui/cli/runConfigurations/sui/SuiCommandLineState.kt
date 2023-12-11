package org.sui.cli.runConfigurations.sui

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.filters.Filter
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import org.sui.cli.MoveFileHyperlinkFilter
import org.sui.cli.runConfigurations.sui.SuiCommandLine
import java.nio.file.Path

open class SuiCommandLineState(
    execEnv: ExecutionEnvironment,
    private val suiPath: Path,
    private val commandLine: SuiCommandLine
) :
    CommandLineState(execEnv) {

    val project = environment.project

    init {
        createFilters().forEach { addConsoleFilters(it) }
    }

    override fun startProcess(): ProcessHandler {
        val suiCli = SuiCliExecutor(suiPath)
        val generalCommandLine = commandLine.toGeneralCommandLine(suiCli)

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
