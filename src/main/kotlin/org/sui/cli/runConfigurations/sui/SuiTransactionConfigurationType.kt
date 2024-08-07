package org.sui.cli.runConfigurations.sui

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.openapi.util.NotNullLazyValue
import org.sui.cli.runConfigurations.sui.run.RunCommandConfigurationFactory
import org.sui.ide.MoveIcons

class SuiTransactionConfigurationType :
    ConfigurationTypeBase(
        "SuiTransactionConfiguration",
        "Sui Transaction",
        "Sui transaction execution",
        NotNullLazyValue.createConstantValue(MoveIcons.SUI_LOGO)
    ) {

    init {
        addFactory(RunCommandConfigurationFactory(this))
//        addFactory(ViewCommandConfigurationFactory(this))
    }

    @Suppress("CompanionObjectInExtension")
    companion object {
        fun getInstance(): SuiTransactionConfigurationType {
            return ConfigurationTypeUtil.findConfigurationType(SuiTransactionConfigurationType::class.java)
        }
    }
}
