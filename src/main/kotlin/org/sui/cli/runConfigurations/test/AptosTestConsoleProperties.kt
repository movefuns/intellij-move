package org.sui.cli.runConfigurations.test

import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.testframework.TestConsoleProperties
import com.intellij.execution.testframework.sm.SMCustomMessagesParsing
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.testframework.sm.runner.SMTestLocator

class AptosTestConsoleProperties(
    runconfig: RunConfiguration,
    executor: Executor
) :
    SMTRunnerConsoleProperties(runconfig, TEST_FRAMEWORK_NAME, executor),
    SMCustomMessagesParsing {

    init {
        isIdBasedTestTree = false
    }

    override fun getTestLocator(): SMTestLocator = SuiTestLocator

    override fun createTestEventsConverter(
        testFrameworkName: String,
        consoleProperties: TestConsoleProperties
    ): OutputToGeneralTestEventsConverter =
        SuiTestEventsConverter(testFrameworkName, consoleProperties)

    companion object {
        const val TEST_FRAMEWORK_NAME: String = "Sui Test"
        const val TEST_TOOL_WINDOW_SETTING_KEY: String = "org.move.sui.test.tool.window"
    }
}
