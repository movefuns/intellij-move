package org.sui.cli.runConfigurations.producers

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.psi.PsiElement
import org.sui.cli.runConfigurations.sui.SuiConfigurationType
import org.sui.cli.runConfigurations.sui.view.ViewCommandConfiguration
import org.sui.cli.runConfigurations.sui.view.ViewCommandConfigurationFactory
import org.sui.cli.runConfigurations.sui.view.ViewCommandConfigurationHandler

class ViewCommandConfigurationProducer : FunctionCallConfigurationProducerBase<ViewCommandConfiguration>() {
    override fun getConfigurationFactory(): ConfigurationFactory =
        ViewCommandConfigurationFactory(SuiConfigurationType.getInstance())

    override fun configFromLocation(location: PsiElement) =
        ViewCommandConfigurationHandler().configurationFromLocation(location)
}
