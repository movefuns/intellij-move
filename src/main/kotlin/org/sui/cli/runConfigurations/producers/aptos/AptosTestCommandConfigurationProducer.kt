package org.sui.cli.runConfigurations.producers.aptos

import org.sui.cli.runConfigurations.aptos.AptosConfigurationType
import org.sui.cli.runConfigurations.aptos.any.AnyCommandConfigurationFactory
import org.sui.cli.runConfigurations.producers.TestCommandConfigurationProducerBase
import org.sui.cli.settings.Blockchain

class AptosTestCommandConfigurationProducer : TestCommandConfigurationProducerBase(Blockchain.APTOS) {

    override fun getConfigurationFactory() =
        AnyCommandConfigurationFactory(AptosConfigurationType.getInstance())
}
