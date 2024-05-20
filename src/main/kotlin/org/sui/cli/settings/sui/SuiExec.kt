package org.sui.cli.settings.sui;

import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.SystemInfo
import org.sui.cli.settings.MoveProjectSettingsService
import org.sui.cli.settings.isValidExecutable
import org.sui.stdext.toPathOrNull

sealed class SuiExec {

    abstract val execPath: String

    object Bundled : SuiExec() {
        override val execPath: String
            get() = ""
    }

    data class LocalPath(override val execPath: String) : SuiExec()

    fun isValid(): Boolean {
        if (this is Bundled && !isBundledSupportedForThePlatform()) return false
        return this.toPathOrNull().isValidExecutable()
    }

    fun toPathOrNull() = this.execPath.toPathOrNull()

    fun pathToSettingsFormat(): String? =
        when (this) {
            is LocalPath -> this.execPath
            is Bundled -> null
        }

    companion object {
        fun default(): SuiExec {
            // Don't use `Project.moveSettings` here because `getService` can return `null`
            // for default project after dynamic plugin loading. As a result, you can get
            // `java.lang.IllegalStateException`. So let's handle it manually:
            val defaultProjectSettings =
                ProjectManager.getInstance().defaultProject.getService(MoveProjectSettingsService::class.java)

            val defaultProjectSuiExec = defaultProjectSettings?.state?.suiExec()
            return defaultProjectSuiExec
                ?: LocalPath("")
        }

        fun isBundledSupportedForThePlatform(): Boolean = !SystemInfo.isMac
    }
}
