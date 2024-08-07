package org.sui.cli.runConfigurations.sui

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.openapi.util.NotNullLazyValue
import org.sui.cli.runConfigurations.sui.cmd.SuiCommandConfigurationFactory
import org.sui.ide.MoveIcons

class SuiCommandConfigurationType :
    ConfigurationTypeBase(
        "SuiCommandConfiguration",
        "Sui",
        "Sui command execution",
        NotNullLazyValue.createConstantValue(MoveIcons.SUI_LOGO)
    ) {

    init {
        addFactory(SuiCommandConfigurationFactory(this))
    }

    val factory: ConfigurationFactory get() = configurationFactories.single()

    @Suppress("CompanionObjectInExtension")
    companion object {
        fun getInstance(): SuiCommandConfigurationType {
            return ConfigurationTypeUtil.findConfigurationType(SuiCommandConfigurationType::class.java)
        }
    }
}