package org.sui.ide.actions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessOutput
import com.intellij.execution.util.ExecUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import org.sui.ide.dialog.EnvDialog

class OpenSwitchEnvsDialogAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        ApplicationManager.getApplication().executeOnPooledThread {
            val commandLine = GeneralCommandLine("sui", "client", "envs", "--json")
            val processOutput: ProcessOutput = ExecUtil.execAndGetOutput(commandLine)

            val outputJson = processOutput.stdout + processOutput.stderr

            val gson = Gson()
            val type = object : TypeToken<List<Any>>() {}.type
            val data = gson.fromJson<List<Any>>(outputJson, type)
            val networks = gson.fromJson<List<NetEnv>>(gson.toJson(data[0]), object : TypeToken<List<NetEnv>>() {}.type)
            val activeNetwork = data[1] as String
            val networkList = Envs(activeNetwork, networks)
            ApplicationManager.getApplication().invokeLater {
                EnvDialog(networkList).show()
            }
        }
    }

    data class Envs(
        val activeNetwork: String,
        val networks: List<NetEnv>
    )

    data class NetEnv(
        val alias: String,
        val rpc: String,
        val ws: String?
    )
}