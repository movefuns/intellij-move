package org.sui.cli.sdks

import com.intellij.openapi.util.SystemInfo
import org.sui.openapiext.PluginPathManager
import java.io.File

data class SuiSdk(val sdksDir: String, val version: String, val network: String) {
    val githubArchiveUrl: String
        get() {
            return "https://github.com/MystenLabs/sui/releases/download" +
                    "/$network-v$version/$githubArchiveFileName"
        }

    val githubArchiveFileName: String
        get() {
            // 如果是mac系统，判断当前处理器架构是arm还是x86
            val arch = getMacProcessorArchitecture()
            return "sui-$network-v$version-${PluginPathManager.getCurrentOS()}-${arch}.tgz"
        }

    val targetFile: File
        get() = File(sdksDir, if (SystemInfo.isWindows) "sui-$network-$version.exe" else "sui-$network-$version")

    private fun getMacProcessorArchitecture(): String {
        if (SystemInfo.isMac) {
            val osArch = System.getProperty("os.arch")
            return when (osArch) {
                "aarch64" -> "arm64"
                "x86_64" -> "x86_64"
                else -> "unknown"
            }
        } else {
            return "x86_64"
        }
    }
}
