package org.sui.cli.runConfigurations.producers.sui

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.psi.PsiElement
import org.sui.cli.runConfigurations.producers.SuiCommandLineFromContext
import org.sui.cli.runConfigurations.sui.SuiTransactionConfigurationType
import org.sui.cli.runConfigurations.sui.view.ViewCommandConfiguration
import org.sui.cli.runConfigurations.sui.view.ViewCommandConfigurationFactory
import org.sui.cli.runConfigurations.sui.view.ViewCommandConfigurationHandler

class ViewCommandConfigurationProducer : FunctionCallConfigurationProducerBase<ViewCommandConfiguration>() {
    override fun getConfigurationFactory(): ConfigurationFactory =
        ViewCommandConfigurationFactory(SuiTransactionConfigurationType.getInstance())

    override fun fromLocation(location: PsiElement, climbUp: Boolean): SuiCommandLineFromContext? =
        ViewCommandConfigurationHandler().configurationFromLocation(location)
}
