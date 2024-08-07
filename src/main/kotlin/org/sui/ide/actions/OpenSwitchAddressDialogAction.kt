package org.sui.ide.actions

import com.google.gson.Gson
import com.intellij.execution.process.ProcessOutput
import com.intellij.execution.util.ExecUtil
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressManager
import org.sui.cli.runConfigurations.SuiCommandLine
import org.sui.cli.settings.suiExecPath
import org.sui.ide.dialog.AddressDialog
import org.sui.ide.notifications.MvNotifications
import org.sui.utils.StringUtils
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class OpenSwitchAddressDialogAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {

        val project = e.project ?: return

        val task = Runnable {
            val suiCommandLine = SuiCommandLine("client addresses", listOf("--json"))
            if (project.suiExecPath == null) return@Runnable
            val commandLine = suiCommandLine.toGeneralCommandLine(project.suiExecPath!!)
            val processOutput: ProcessOutput = ExecUtil.execAndGetOutput(commandLine)

            val addressesJson = processOutput.stdout + processOutput.stderr
            val data = extractAddressData(addressesJson)
            val addressList = data.addresses
            ApplicationManager.getApplication().invokeLater {
                AddressDialog(addressList, project).show()
            }
        }

        val executor = Executors.newSingleThreadExecutor()
        val future = executor.submit(task)

        try {
            ProgressManager.getInstance().runProcessWithProgressSynchronously({
                future.get(6, TimeUnit.SECONDS) // 设置超时时间为60秒
            }, "Processing", true, project)
        } catch (e: TimeoutException) {
            future.cancel(true)
            MvNotifications.pluginNotifications().createNotification(
                "Timeout",
                "The operation timed out.",
                NotificationType.ERROR
            ).notify(project)
        } finally {
            executor.shutdown()
        }
    }

    data class AddressData(
        val activeAddress: String,
        val addresses: List<List<String>>
    )

    fun extractAddressData(s: String): AddressData {
        val gson = Gson()
        val cleanJsonString = StringUtils.cleanJsonObjectString(s)
        return gson.fromJson(cleanJsonString, AddressData::class.java)
    }
}

