package org.sui.openapiext

import com.intellij.openapi.util.SystemInfo
import java.nio.file.Path

object PluginPathManager {
    private fun pluginDir(): Path = plugin().pluginPath

    fun getCurrentOS(): String {
        return when {
            SystemInfo.isMac -> "macos"
            SystemInfo.isWindows -> "windows"
            SystemInfo.isLinux -> "ubuntu"
            else -> "ubuntu"
        }
    }

    val bundledSuiCli: String?
        get() {
            return null
        }
}
