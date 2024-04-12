package org.sui.ide.newProject

import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.sui.cli.Consts
import org.sui.cli.runConfigurations.aptos.AptosConfigurationType
import org.sui.cli.runConfigurations.aptos.any.AnyCommandConfiguration
import org.sui.cli.runConfigurations.aptos.any.AnyCommandConfigurationFactory
import org.sui.ide.notifications.updateAllNotifications
import org.sui.openapiext.addRunConfiguration
import org.sui.openapiext.contentRoots
import org.sui.openapiext.openFile
import org.sui.openapiext.runManager
import org.sui.stdext.toPath

object ProjectInitializationSteps {
    fun openMoveTomlInEditor(project: Project, moveTomlFile: VirtualFile? = null) {
        val file =
            moveTomlFile ?: run {
                val packageRoot = project.contentRoots.firstOrNull()
                if (packageRoot != null) {
                    val manifest = packageRoot.findChild(Consts.MANIFEST_FILE)
                    return@run manifest
                }
                return@run null
            }
        if (file != null) {
            project.openFile(file)
        }
        updateAllNotifications(project)
    }

    fun createDefaultCompileConfigurationIfNotExists(project: Project) {
        if (project.runManager.allConfigurationsList.isEmpty()) {
            createDefaultCompileConfiguration(project, true)
        }
    }

    fun createDefaultCompileConfiguration(project: Project, selected: Boolean): RunnerAndConfigurationSettings {
        val runConfigurationAndWithSettings =
            project.addRunConfiguration(selected) { runManager, _ ->
                val configurationFactory = AptosConfigurationType.getInstance()
                    .configurationFactories.find { it is AnyCommandConfigurationFactory }
                    ?: error("AnyCommandConfigurationFactory should be present in the factories list")
                runManager.createConfiguration("Compile Project", configurationFactory)
                    .apply {
                        (configuration as? AnyCommandConfiguration)?.apply {
                            command = "move compile"
                            workingDirectory = project.basePath?.toPath()
                        }
                    }
            }
        return runConfigurationAndWithSettings
    }
}
