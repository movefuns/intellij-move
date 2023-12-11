package org.sui.cli.runConfigurations.producers

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.psi.PsiElement
import org.sui.cli.runConfigurations.sui.run.TestCommandConfiguration
import org.sui.cli.runConfigurations.sui.run.TestCommandConfigurationFactory
import org.sui.cli.runConfigurations.sui.run.TestCommandConfigurationHandler
import org.sui.cli.runConfigurations.sui.SuiConfigurationType
import org.sui.cli.runConfigurations.sui.run.*

class TestCommandConfigurationProducer : FunctionCallConfigurationProducerBase<TestCommandConfiguration>() {
    override fun getConfigurationFactory(): ConfigurationFactory =
        TestCommandConfigurationFactory(SuiConfigurationType.getInstance())

    override fun configFromLocation(location: PsiElement) =
        TestCommandConfigurationHandler().configurationFromLocation(location)
}
