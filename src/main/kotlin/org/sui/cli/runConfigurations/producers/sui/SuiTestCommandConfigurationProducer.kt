package org.sui.cli.runConfigurations.producers.sui

import com.intellij.execution.configurations.ConfigurationFactory
import org.sui.cli.runConfigurations.producers.TestCommandConfigurationProducerBase
import org.sui.cli.runConfigurations.sui.SuiConfigurationType
import org.sui.cli.settings.Blockchain

class SuiTestCommandConfigurationProducer : TestCommandConfigurationProducerBase(Blockchain.SUI) {

    override fun getConfigurationFactory(): ConfigurationFactory = SuiConfigurationType()
}