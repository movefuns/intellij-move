package org.sui.cli.runConfigurations.test

import com.intellij.execution.runners.ExecutionEnvironment
import org.sui.cli.runConfigurations.AptosRunStateBase
import org.sui.cli.runConfigurations.CommandConfigurationBase
import org.sui.cli.runConfigurations.aptos.AptosTestConsoleBuilder
import org.sui.cli.runConfigurations.aptos.cmd.AptosCommandConfiguration

class AptosTestRunState(
    environment: ExecutionEnvironment,
    runConfiguration: CommandConfigurationBase,
    config: CommandConfigurationBase.CleanConfiguration.Ok
) : AptosRunStateBase(environment, runConfiguration, config) {

    init {
        consoleBuilder =
            AptosTestConsoleBuilder(environment.runProfile as AptosCommandConfiguration, environment.executor)
        createFilters().forEach { consoleBuilder.addFilter(it) }
    }

//    override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
//        val processHandler = startProcess()
//        val console = createConsole(executor)
//        console?.attachToProcess(processHandler)
//        return DefaultExecutionResult(console, processHandler).apply { setRestartActions(ToggleAutoTestAction()) }
//    }
}
