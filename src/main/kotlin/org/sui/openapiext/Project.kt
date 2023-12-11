package org.sui.openapiext

import com.intellij.execution.RunManager
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import org.sui.cli.runConfigurations.legacy.MoveCommandConfiguration

val Project.runManager: RunManager get() = RunManager.getInstance(this)

fun Project.suiRunConfigurations(): List<MoveCommandConfiguration> =
    runManager.allConfigurationsList
        .filterIsInstance<MoveCommandConfiguration>()

fun Project.suiBuildRunConfigurations(): List<MoveCommandConfiguration> =
    suiRunConfigurations().filter { it.command.startsWith("move build") }

inline fun <reified T : Configurable> Project.showSettings() {
    ShowSettingsUtil.getInstance().showSettingsDialog(this, T::class.java)
}
