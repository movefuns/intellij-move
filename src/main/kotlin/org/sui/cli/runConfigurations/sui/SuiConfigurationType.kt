package org.sui.cli.runConfigurations.sui

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.openapi.util.NotNullLazyValue
import org.sui.cli.runConfigurations.sui.any.AnyCommandConfigurationFactory
import org.sui.ide.MoveIcons

class SuiConfigurationType :
    ConfigurationTypeBase(
        "SuiCommandConfiguration",
        "Sui Command",
        "Sui command execution",
        NotNullLazyValue.createConstantValue(MoveIcons.SUI_ICON)
    ) {

    init {
//        addFactory(BuildCommandConfigurationFactory(this))
//        addFactory(TestCommandConfigurationFactory(this))
        addFactory(AnyCommandConfigurationFactory(this))
    }

    companion object {
        fun getInstance(): SuiConfigurationType {
            return ConfigurationTypeUtil.findConfigurationType(SuiConfigurationType::class.java)
        }
    }
}
