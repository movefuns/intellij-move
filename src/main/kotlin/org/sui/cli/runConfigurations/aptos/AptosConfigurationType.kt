package org.sui.cli.runConfigurations.aptos

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.openapi.util.NotNullLazyValue
import org.sui.cli.runConfigurations.aptos.any.AnyCommandConfigurationFactory
import org.sui.cli.runConfigurations.aptos.run.RunCommandConfigurationFactory
import org.sui.cli.runConfigurations.aptos.view.ViewCommandConfigurationFactory
import org.sui.ide.MoveIcons

class AptosConfigurationType :
    ConfigurationTypeBase(
        "AptosCommandConfiguration",
        "Aptos",
        "Aptos command execution",
        NotNullLazyValue.createConstantValue(MoveIcons.APTOS_LOGO)
    ) {

    init {
        addFactory(RunCommandConfigurationFactory(this))
        addFactory(ViewCommandConfigurationFactory(this))
        addFactory(AnyCommandConfigurationFactory(this))
    }

    companion object {
        fun getInstance(): AptosConfigurationType {
            return ConfigurationTypeUtil.findConfigurationType(AptosConfigurationType::class.java)
        }
    }
}
