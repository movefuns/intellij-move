package org.sui.ide.dialog

import com.intellij.execution.process.ProcessOutput
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import org.sui.cli.settings.suiExec
import org.sui.common.NOTIFACATION_GROUP
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*
import javax.swing.table.DefaultTableModel


class AddressDialog(var data: List<String>) : DialogWrapper(true) {
    init {
        init()
        title = "Click Address To Switch"
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel()
        val tableModel = DefaultTableModel(arrayOf("Address"), 0)
        val table = JBTable(tableModel)

        data.forEach { address ->
            tableModel.addRow(arrayOf(address))
        }
        table.selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION

        val button = JButton("Switch")
        button.addActionListener {
            val row = table.selectedRow
            if (row >= 0) {
                executeCommand(tableModel.getValueAt(row, 0).toString())
            }
            dispose()
        }

        val scrollPane = JBScrollPane(table)
        scrollPane.preferredSize = Dimension(550, 150)
        panel.layout = BorderLayout()
        panel.add(scrollPane, BorderLayout.NORTH)
        panel.add(button, BorderLayout.AFTER_LINE_ENDS)
        return panel
    }

    override fun createActions(): Array<Action> {
        return arrayOf()
    }


    private fun executeCommand(address: String) {
        // 获取project 对象
        val project = ProjectManager.getInstance().defaultProject

        val onProcessComplete: (ProcessOutput?) -> Unit = { output ->
            if (output != null && output.exitCode == 0) {
                println("Process executed successfully with output: ${output.stdout}")
                showNotification(address)
            } else {
                println("Process failed with error: ${output?.stderr}")
            }
        }
        project.suiExec.toExecutor()
            ?.simpleCommand(project, "client", listOf("switch", "--address", address), onProcessComplete)
    }

    private fun showNotification(message: String) {
        // 显示通知
        Notifications.Bus.notify(
            Notification(
                NOTIFACATION_GROUP,
                "Switch Address",
                message,
                NotificationType.INFORMATION
            )
        )
    }
}