package org.sui.ide.actions

import com.intellij.execution.process.ProcessOutput
import com.intellij.execution.util.ExecUtil
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressManager
import org.sui.cli.runConfigurations.SuiCommandLine
import org.sui.cli.settings.suiExecPath
import org.sui.ide.notifications.MvNotifications
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@Suppress("DEPRECATION")
class GetActiveAddressAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val task = Runnable {
            val suiCommandLine = SuiCommandLine("client active-address")
            if (project.suiExecPath == null) return@Runnable

            val commandLine = suiCommandLine.toGeneralCommandLine(project.suiExecPath!!)
            val processOutput: ProcessOutput = ExecUtil.execAndGetOutput(commandLine)

            val exitCode = processOutput.exitCode
            val output = processOutput.stdout + processOutput.stderr
            val notificationType = if (exitCode == 0) NotificationType.INFORMATION else NotificationType.ERROR
            MvNotifications.pluginNotifications().createNotification(
                "Active Address",
                output, notificationType
            ).notify(project)
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
}