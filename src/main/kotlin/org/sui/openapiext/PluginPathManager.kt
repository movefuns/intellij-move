package org.sui.openapiext

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.util.SystemInfo
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

const val PLUGIN_ID: String = "org.sui.lang"

fun plugin(): IdeaPluginDescriptor = PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID))!!

class OpenSSLInfoService {
    var openssl3: Boolean = true

    init {
        if (!SystemInfo.isWindows) {
            val fut = ApplicationManager.getApplication().executeOnPooledThread {
                val openSSLVersion = determineOpenSSLVersion()
                when {
                    openSSLVersion.startsWith("OpenSSL 1") -> openssl3 = false
                    else -> openssl3 = true
                }
            }
            // blocks
            fut.get()
        }
    }

    private fun determineOpenSSLVersion(): String {
//        if (!isUnitTestMode) {
//            checkIsBackgroundThread()
//        }
        return GeneralCommandLine("openssl", "version").execute()?.stdoutLines?.firstOrNull()
            ?: "OpenSSL 3.0.2"
    }
}

object PluginPathManager {
    private fun pluginDir(): Path = plugin().pluginPath

    val bundledAptosCli: String?
        get() {
            val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
            val osArch = System.getProperty("os.arch").lowercase(Locale.getDefault())

            val isMac = osName.contains("mac")
            val isArm = osArch.contains("aarch64") || osArch.contains("arm")

            if (isMac && isArm) {
                println("This is a Mac with ARM architecture")
            } else {
                println("This is not a Mac with ARM architecture")
            }
            val (os, binaryName) = when {
                isMac && isArm -> "macos-arm" to "64"
                isMac && !isArm -> "macos" to "-x86_64"
                SystemInfo.isWindows -> "windows" to "-x86_64.exe"
                else -> {
                    "ubuntu" to "-x86_64"
                }
            }
            val aptosCli = pluginDir().resolve("bin/$os/target/release/sui-$os$binaryName").takeIf { Files.exists(it) } ?: return null
            return if (Files.isExecutable(aptosCli) || aptosCli.toFile().setExecutable(true)) {
                aptosCli.toString()
            } else {
                null
            }
        }

    val bundledSuiCli: String?
        get() {
            if (!SystemInfo.isWindows) {
                val result = runCommand("whereis sui")
                return result.split("sui: ").getOrNull(1)?.trim()
            } else {
                return null
            }
        }
}
