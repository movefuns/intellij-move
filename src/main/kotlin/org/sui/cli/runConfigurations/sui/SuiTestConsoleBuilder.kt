package org.sui.cli.runConfigurations.sui

import com.intellij.execution.Executor
import com.intellij.execution.filters.Filter
import com.intellij.execution.filters.TextConsoleBuilder
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.execution.ui.ConsoleView
import org.sui.cli.runConfigurations.sui.cmd.SuiCommandConfiguration
import org.sui.cli.runConfigurations.test.SuiTestConsoleProperties.Companion.TEST_FRAMEWORK_NAME

class SuiTestConsoleBuilder(
    private val config: SuiCommandConfiguration,
    private val executor: Executor
) : TextConsoleBuilder() {

    private val filters: MutableList<Filter> = mutableListOf()

    override fun addFilter(filter: Filter) {
        filters.add(filter)
    }

    override fun setViewer(isViewer: Boolean) {}

    override fun getConsole(): ConsoleView {
        val consoleProperties = config.createTestConsoleProperties(executor)
        val consoleView = SMTestRunnerConnectionUtil.createConsole(TEST_FRAMEWORK_NAME, consoleProperties!!)
        filters.forEach { consoleView.addMessageFilter(it) }
        return consoleView

    }
}