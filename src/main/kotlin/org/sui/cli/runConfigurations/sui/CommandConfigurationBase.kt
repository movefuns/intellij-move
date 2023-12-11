package org.sui.cli.runConfigurations.sui

import com.intellij.execution.Executor
import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.configurations.RunConfigurationWithSuppressedDefaultDebugAction
import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts
import org.jdom.Element
import org.sui.cli.*
import org.sui.cli.runConfigurations.legacy.MoveCommandConfiguration
import org.sui.stdext.exists
import java.nio.file.Path

abstract class CommandConfigurationBase(
    project: Project,
    factory: ConfigurationFactory
) :
    LocatableConfigurationBase<SuiCommandLineState>(project, factory),
    RunConfigurationWithSuppressedDefaultDebugAction {

    var command: String = ""
    var workingDirectory: Path? = if (!project.isDefault) {
        project.moveProjects.allProjects.firstOrNull()?.contentRootPath
    } else {
        null
    }
    var environmentVariables: EnvironmentVariablesData = EnvironmentVariablesData.DEFAULT

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        element.writeString("command", this.command)
        element.writePath("workingDirectory", this.workingDirectory)
        environmentVariables.writeExternal(element)
    }

    override fun readExternal(element: Element) {
        super.readExternal(element)
        this.command = element.readString("command") ?: return
        this.workingDirectory = element.readPath("workingDirectory") ?: return
        this.environmentVariables = EnvironmentVariablesData.readExternal(element)
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): SuiCommandLineState? {
        return clean().ok?.let { stateConfig ->
            SuiCommandLineState(environment, stateConfig.suiPath, stateConfig.commandLine)
        }
    }

    fun clean(): CleanConfiguration {
        val workingDirectory = workingDirectory
            ?: return CleanConfiguration.error("No working directory specified")
        val parsedCommand = MoveCommandConfiguration.ParsedCommand.parse(command)
            ?: return CleanConfiguration.error("No command specified")

        val suiCli = SuiCliExecutor.fromProject(project)
            ?: return CleanConfiguration.error("No Sui CLI specified")
        if (!suiCli.location.exists()) {
            return CleanConfiguration.error("Invalid Sui CLI: ${suiCli.location}")
        }

        val commandLine = SuiCommandLine(
            parsedCommand.command,
            parsedCommand.additionalArguments,
            workingDirectory,
            environmentVariables
        )
        return CleanConfiguration.Ok(suiCli.location, commandLine)
    }

    sealed class CleanConfiguration {
        class Ok(val suiPath: Path, val commandLine: SuiCommandLine) : CleanConfiguration()
        class Err(val error: RuntimeConfigurationError) : CleanConfiguration()

        val ok: Ok? get() = this as? Ok

        companion object {
            fun error(@Suppress("UnstableApiUsage") @NlsContexts.DialogMessage message: String) = Err(
                RuntimeConfigurationError(message)
            )
        }
    }
}
