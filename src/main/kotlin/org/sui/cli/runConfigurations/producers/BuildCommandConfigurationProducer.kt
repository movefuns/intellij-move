package org.sui.cli.runConfigurations.producers

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.psi.PsiElement
import org.sui.cli.runConfigurations.sui.SuiConfigurationType
import org.sui.cli.runConfigurations.sui.run.BuildCommandConfiguration
import org.sui.cli.runConfigurations.sui.run.BuildCommandConfigurationFactory
import org.sui.cli.runConfigurations.sui.run.BuildCommandConfigurationHandler

class BuildCommandConfigurationProducer : FunctionCallConfigurationProducerBase<BuildCommandConfiguration>() {
    override fun getConfigurationFactory(): ConfigurationFactory =
        BuildCommandConfigurationFactory(SuiConfigurationType.getInstance())

    override fun configFromLocation(location: PsiElement) =
        BuildCommandConfigurationHandler().configurationFromLocation(location)
}
