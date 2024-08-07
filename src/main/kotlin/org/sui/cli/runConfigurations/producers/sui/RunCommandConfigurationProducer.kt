package org.sui.cli.runConfigurations.producers.sui

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.psi.PsiElement
import org.sui.cli.runConfigurations.producers.SuiCommandLineFromContext
import org.sui.cli.runConfigurations.sui.SuiTransactionConfigurationType
import org.sui.cli.runConfigurations.sui.run.RunCommandConfiguration
import org.sui.cli.runConfigurations.sui.run.RunCommandConfigurationFactory
import org.sui.cli.runConfigurations.sui.run.RunCommandConfigurationHandler

class RunCommandConfigurationProducer : FunctionCallConfigurationProducerBase<RunCommandConfiguration>() {
    override fun getConfigurationFactory(): ConfigurationFactory =
        RunCommandConfigurationFactory(SuiTransactionConfigurationType.getInstance())

    override fun fromLocation(location: PsiElement, climbUp: Boolean): SuiCommandLineFromContext? =
        RunCommandConfigurationHandler().configurationFromLocation(location)
}
