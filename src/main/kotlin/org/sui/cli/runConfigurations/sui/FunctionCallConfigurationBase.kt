package org.sui.cli.runConfigurations.sui

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.project.Project
import org.sui.cli.moveProjectsService
import org.sui.cli.runConfigurations.CommandConfigurationBase

abstract class FunctionCallConfigurationBase(
    project: Project,
    factory: ConfigurationFactory,
    val configurationHandler: CommandConfigurationHandler,
) : CommandConfigurationBase(project, factory) {

    var packageId: String? = null
    var gasId: String? = null
    var gasBudget: String? = null

    fun firstRunShouldOpenEditor(): Boolean {
        val moveProject = workingDirectory
            ?.let { wdir -> project.moveProjectsService.findMoveProjectForPath(wdir) } ?: return true
        val (_, functionCall) = configurationHandler
            .parseTransactionCommand(moveProject, command).unwrapOrNull() ?: return true
        return functionCall.parametersRequired()
    }
}
