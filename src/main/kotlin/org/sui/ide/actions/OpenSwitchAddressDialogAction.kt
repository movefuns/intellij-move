package org.sui.ide.actions

import com.google.gson.Gson
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessOutput
import com.intellij.execution.util.ExecUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import org.sui.ide.dialog.AddressDialog

class OpenSwitchAddressDialogAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        ApplicationManager.getApplication().executeOnPooledThread {
            val commandLine = GeneralCommandLine("sui", "client", "addresses", "--json")
            val processOutput: ProcessOutput = ExecUtil.execAndGetOutput(commandLine)

            val addressesJson = processOutput.stdout + processOutput.stderr
            val data = extractAddressData(addressesJson)
            val addressList = data.addresses
            ApplicationManager.getApplication().invokeLater {
                AddressDialog(addressList).show()
            }
        }
    }

    data class AddressData(
        val activeAddress: String,
        val addresses: List<List<String>>
    )

    fun extractAddressData(s: String): AddressData {
        val gson = Gson()
        return gson.fromJson(s, AddressData::class.java)
    }
}

