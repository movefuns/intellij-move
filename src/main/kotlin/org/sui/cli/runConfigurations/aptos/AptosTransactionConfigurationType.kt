package org.sui.cli.runConfigurations.aptos

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.openapi.util.NotNullLazyValue
import org.sui.cli.runConfigurations.aptos.run.RunCommandConfigurationFactory
import org.sui.cli.runConfigurations.aptos.view.ViewCommandConfigurationFactory
import org.sui.ide.MoveIcons

class AptosTransactionConfigurationType :
    ConfigurationTypeBase(
        "AptosTransactionConfiguration",
        "Aptos Transaction",
        "Aptos transaction execution",
        NotNullLazyValue.createConstantValue(MoveIcons.APTOS_LOGO)
    ) {

    init {
        addFactory(RunCommandConfigurationFactory(this))
        addFactory(ViewCommandConfigurationFactory(this))
    }

    @Suppress("CompanionObjectInExtension")
    companion object {
        fun getInstance(): AptosTransactionConfigurationType {
            return ConfigurationTypeUtil.findConfigurationType(AptosTransactionConfigurationType::class.java)
        }
    }
}
