package org.sui.cli.settings

import com.intellij.openapi.project.ProjectManager
import org.sui.cli.defaultMoveSettings
import org.sui.cli.runConfigurations.sui.SuiCliExecutor
import org.sui.openapiext.PluginPathManager
import org.sui.stdext.toPathOrNull
import java.nio.file.Path

sealed class SuiExec {
    abstract val execPath: String

    object Bundled: SuiExec() {
        override val execPath: String
            get() = PluginPathManager.bundledSuiCli ?: ""
    }

    data class LocalPath(override val execPath: String): SuiExec()

    fun pathOrNull() = this.execPath.toPathOrNull()

    fun toExecutor(): SuiCliExecutor? =
        execPath.toPathOrNull()?.let { SuiCliExecutor(it) }

    fun pathToSettingsFormat(): String? =
        when (this) {
            is LocalPath -> this.execPath
            is Bundled -> null
        }

    companion object {
        fun fromSettingsFormat(suiPath: String?): SuiExec =
            when (suiPath) {
                null -> Bundled
                else -> LocalPath(suiPath)
            }

        fun getVersion(suiExecPath: Path?): String? {
            return suiExecPath?.let { path ->
                val executor = SuiCliExecutor(path)
                val version = executor.version()
                val matchResult = Regex("sui\\s+(\\d+\\.\\d+\\.\\d+(-[a-f0-9]+)?)").find(version ?: "")
                val isValid = matchResult != null
                if (isValid) {
                    version
                } else {
                    ""
                }.also { _ ->
                    val project = ProjectManager.getInstance().defaultMoveSettings
                    project?.modify {
                        it.suiPath = path.toString()
                        it.isValidExec = isValid
                    }
                }
            }
        }

    }
}
