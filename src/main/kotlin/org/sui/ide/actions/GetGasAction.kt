package org.sui.ide.actions

import com.intellij.execution.process.ProcessOutput
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.sui.ide.notifications.MvNotifications

class GetGasAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {

        val project = e.project ?: return

        val onProcessComplete: (ProcessOutput?) -> Unit = { output ->
            if (output != null && output.exitCode == 0) {
                println("Process executed successfully with output: ${output.stdout}")
                val stdout = output.stdout
                val message = if (!stdout.startsWith("No gas")) {
                    convertToHtmlTable(stdout)
                } else {
                    stdout
                }
                println(message)
            } else {
                MvNotifications.pluginNotifications().createNotification(
                    "Switch address",
                    "Execution failure, please check the sui cli path.",
                    NotificationType.ERROR
                ).notify(project)
            }
        }
//        project.suiExec.toExecutor()?.simpleCommand(project, "client", listOf("gas"), onProcessComplete)
    }

    fun convertToHtmlTable(input: String): String {
        val lines = input.split("\n")
            .filter { it.startsWith("│") && !it.startsWith("╭") && !it.startsWith("╰") && !it.startsWith("├") }
        val headers = lines.first().split("│").map { it.trim() }.filter { it.isNotEmpty() }
        val rows = lines.drop(1).map {
            it.split("│")
                .map { cell -> cell.trim() }
                .filter { cell -> cell.isNotEmpty() }
        }

        return buildString {
            append("<table>")
            // Headers
            append("<tr>")
            headers.forEach { header ->
                append("<th>$header</th>")
            }
            append("</tr>")
            // Rows
            rows.forEach { row ->
                append("<tr>")
                row.forEach { cell ->
                    append("<td>$cell</td>")
                }
                append("</tr>")
            }
            append("</table>")
        }
    }


}
