package org.sui.ide.dialog

import com.intellij.execution.util.ExecUtil
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import org.sui.cli.runConfigurations.SuiCommandLine
import org.sui.cli.settings.suiExecPath
import org.sui.ide.notifications.MvNotifications
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*
import javax.swing.table.DefaultTableModel


class AddressDialog(var data: List<List<String>>, val project: Project) : DialogWrapper(true) {
    val NOTIFICATION_TITLE = "Active Address"
    val SWITCH_SUCCESS_MSG = "active address switched successfully"

    init {
        init()
        title = "Click Address To Switch"
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel()
        val tableModel = DefaultTableModel(arrayOf("Address"), 0)
        val table = JBTable(tableModel)

        data.forEach { address ->
            tableModel.addRow(arrayOf(address[1]))
        }
        table.selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION

        val button = JButton("Switch")
        button.addActionListener {
            val row = table.selectedRow
            if (row >= 0) {
                switchAddressAndNotify(tableModel.getValueAt(row, 0).toString())
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

    fun switchAddressAndNotify(address: String) {
        ApplicationManager.getApplication().executeOnPooledThread {
            val suiCommandLine = SuiCommandLine("client switch", listOf("--address", address))
            if (project.suiExecPath == null) return@executeOnPooledThread
            val commandLine = suiCommandLine.toGeneralCommandLine(project.suiExecPath!!)

            val output = ExecUtil.execAndGetOutput(commandLine)
            val notificationType = if (output.exitCode == 0) NotificationType.INFORMATION else NotificationType.ERROR
            SwingUtilities.invokeLater { displayNotification(NOTIFICATION_TITLE, SWITCH_SUCCESS_MSG, notificationType) }
        }
    }

    private fun displayNotification(title: String, message: String, type: NotificationType) {
        MvNotifications.pluginNotifications().createNotification(
            title,
            message,
            type
        ).notify(project)
    }
}