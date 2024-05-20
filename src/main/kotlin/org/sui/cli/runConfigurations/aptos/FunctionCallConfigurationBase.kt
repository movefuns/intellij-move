package org.sui.cli.runConfigurations.aptos

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.project.Project
import org.sui.cli.MoveProject
import org.sui.cli.moveProjectsService
import org.sui.cli.runConfigurations.CommandConfigurationBase
import org.sui.cli.settings.aptosPath
import java.nio.file.Path

abstract class FunctionCallConfigurationBase(
    project: Project,
    factory: ConfigurationFactory,
    val configurationHandler: CommandConfigurationHandler,
) : CommandConfigurationBase(project, factory) {

    var moveProjectFromWorkingDirectory: MoveProject?
        get() = workingDirectory?.let { wdir -> project.moveProjectsService.findMoveProjectForPath(wdir) }
        set(value) {
            workingDirectory = value?.contentRootPath
        }

    override fun getCliPath(project: Project): Path? = project.aptosPath

    fun firstRunShouldOpenEditor(): Boolean {
        val moveProject = moveProjectFromWorkingDirectory ?: return true
        val (_, functionCall) = configurationHandler
            .parseCommand(moveProject, command).unwrapOrNull() ?: return true
        return functionCall.parametersRequired()
    }
}
