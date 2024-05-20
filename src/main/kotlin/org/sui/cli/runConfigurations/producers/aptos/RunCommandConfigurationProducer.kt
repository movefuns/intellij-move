package org.sui.cli.runConfigurations.producers.aptos

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.psi.PsiElement
import org.sui.cli.runConfigurations.aptos.AptosConfigurationType
import org.sui.cli.runConfigurations.aptos.run.RunCommandConfiguration
import org.sui.cli.runConfigurations.aptos.run.RunCommandConfigurationFactory
import org.sui.cli.runConfigurations.aptos.run.RunCommandConfigurationHandler
import org.sui.cli.runConfigurations.producers.CommandLineArgsFromContext

class RunCommandConfigurationProducer : FunctionCallConfigurationProducerBase<RunCommandConfiguration>() {
    override fun getConfigurationFactory(): ConfigurationFactory =
        RunCommandConfigurationFactory(AptosConfigurationType.getInstance())

    override fun fromLocation(location: PsiElement, climbUp: Boolean): CommandLineArgsFromContext? =
        RunCommandConfigurationHandler().configurationFromLocation(location)
}
