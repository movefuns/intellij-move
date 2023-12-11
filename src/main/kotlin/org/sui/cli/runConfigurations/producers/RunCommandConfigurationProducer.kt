package org.sui.cli.runConfigurations.producers

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.psi.PsiElement
import org.sui.cli.runConfigurations.sui.SuiConfigurationType
import org.sui.cli.runConfigurations.sui.run.*

class RunCommandConfigurationProducer : FunctionCallConfigurationProducerBase<RunCommandConfiguration>() {
    override fun getConfigurationFactory(): ConfigurationFactory =
        RunCommandConfigurationFactory(SuiConfigurationType.getInstance())

    override fun configFromLocation(location: PsiElement) =
        RunCommandConfigurationHandler().configurationFromLocation(location)
}
