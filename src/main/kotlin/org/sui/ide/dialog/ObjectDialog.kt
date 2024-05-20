package org.sui.ide.dialog

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import org.sui.ide.actions.OpenObjectListAction.SuiObject
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.Action
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.ListSelectionModel
import javax.swing.table.DefaultTableModel

class ObjectDialog(var data: List<SuiObject>) : DialogWrapper(true) {
    init {
        init()
        title = "Objects"
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel()
        val tableModel = DefaultTableModel(arrayOf("objectId", "version", "digest", "type"), 0)
        val table = JBTable(tableModel)
        table.columnModel.getColumn(0).preferredWidth = 470
        table.columnModel.getColumn(1).preferredWidth = 60
        table.columnModel.getColumn(2).preferredWidth = 345
        table.columnModel.getColumn(3).preferredWidth = 325

        data.forEach { suiObject ->
            tableModel.addRow(arrayOf(suiObject.objectId, suiObject.version, suiObject.digest, suiObject.type))
        }
        table.selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION

        val scrollPane = JBScrollPane(table)
        scrollPane.preferredSize = Dimension(1200, 250)
        panel.layout = BorderLayout()
        panel.add(scrollPane, BorderLayout.NORTH)
        return panel
    }

    override fun createActions(): Array<Action> {
        return arrayOf()
    }

}