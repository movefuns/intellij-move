package org.sui.ide.dialog

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.util.ExecUtil
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import org.sui.common.NOTIFACATION_GROUP
import org.sui.ide.actions.OpenSwitchEnvsDialogAction
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*
import javax.swing.table.DefaultTableModel

class EnvDialog(var data: OpenSwitchEnvsDialogAction.Envs) : DialogWrapper(true) {

    private val BUTTON_TEXT = "Switch"
    private val SWITCH_ENV_TITLE = "Switch Network Environment"

    init {
        init()
        title = "Click Environment To Switch"
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel()
        val tableModel = DefaultTableModel(arrayOf("alias", "rpc", "ws"), 0)
        val table = setUpTable(tableModel)

        val button = setUpButton(tableModel, table)

        val scrollPane = JBScrollPane(table)
        scrollPane.preferredSize = Dimension(550, 150)
        panel.layout = BorderLayout()
        panel.add(scrollPane, BorderLayout.NORTH)
        panel.add(button, BorderLayout.AFTER_LINE_ENDS)
        return panel
    }

    private fun setUpTable(tableModel: DefaultTableModel): JBTable {
        val table = JBTable(tableModel)
        data.networks.forEach { net ->
            tableModel.addRow(arrayOf(net.alias, net.rpc, net.ws))
        }
        table.selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        return table
    }

    private fun setUpButton(tableModel: DefaultTableModel, table: JBTable): JButton {
        val button = JButton(BUTTON_TEXT)
        button.addActionListener {
            val row = table.selectedRow
            if (row >= 0) {
                executeCommand(tableModel.getValueAt(row, 0).toString())
            }
            dispose()
        }
        return button
    }

    override fun createActions(): Array<Action> {
        return arrayOf()
    }

    private fun executeCommand(env: String) {
        ApplicationManager.getApplication().executeOnPooledThread {
            val commandLine = GeneralCommandLine("sui", "client", "switch", "--env", env)
            ExecUtil.execAndGetOutput(commandLine)
            showNotification("network environment switched successfully")
        }
    }

    private fun showNotification(message: String) {
        Notifications.Bus.notify(
            Notification(
                NOTIFACATION_GROUP,
                SWITCH_ENV_TITLE,
                message,
                NotificationType.INFORMATION
            )
        )
    }
}