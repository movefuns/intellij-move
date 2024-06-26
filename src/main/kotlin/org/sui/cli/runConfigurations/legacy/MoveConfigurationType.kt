package org.sui.cli.runConfigurations.legacy

import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.SimpleConfigurationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NotNullLazyValue
import org.sui.ide.MoveIcons

class MoveConfigurationType :
    SimpleConfigurationType(
        "MoveRunConfiguration",
        "Move",
        "Move command execution",
        NotNullLazyValue.createConstantValue(MoveIcons.MOVE_LOGO)
    ) {
    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return MoveCommandConfiguration(project, this)
    }
}
