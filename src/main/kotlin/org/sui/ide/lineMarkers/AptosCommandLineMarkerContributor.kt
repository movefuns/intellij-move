package org.sui.ide.lineMarkers

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.psi.PsiElement
import org.sui.cli.runConfigurations.producers.AnyCommandConfigurationProducer
import org.sui.cli.runConfigurations.sui.run.BuildCommandConfigurationHandler
import org.sui.cli.runConfigurations.sui.view.ViewCommandConfigurationHandler
import org.sui.ide.MoveIcons
import org.sui.lang.MvElementTypes.IDENTIFIER
import org.sui.lang.core.psi.MvFunction
import org.sui.lang.core.psi.MvModule
import org.sui.lang.core.psi.MvNameIdentifierOwner
import org.sui.lang.core.psi.ext.elementType
import org.sui.lang.core.psi.ext.isEntry
import org.sui.lang.core.psi.ext.isTest
import org.sui.lang.core.psi.ext.isView

class AptosCommandLineMarkerContributor : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? {
        if (element.elementType != IDENTIFIER) return null

        val parent = element.parent
        if (parent !is MvNameIdentifierOwner || element != parent.nameIdentifier) return null

        if (parent is MvFunction) {
            when {
                parent.isTest -> {
                    val config =
                        AnyCommandConfigurationProducer.fromLocation(parent, climbUp = false)
                    if (config != null) {
                        return Info(
                            MoveIcons.RUN_TEST_ITEM,
                            { config.configurationName },
                            *contextActions()
                        )
                    }
                }
                parent.isEntry -> {
                    val config = BuildCommandConfigurationHandler().configurationFromLocation(parent)
                    if (config != null) {
                        return Info(
                            MoveIcons.RUN_TRANSACTION_ITEM,
                            { config.configurationName },
                            *contextActions()
                        )
                    }
                }
                parent.isView -> {
                    val config = ViewCommandConfigurationHandler().configurationFromLocation(parent)
                    if (config != null) {
                        return Info(
                            MoveIcons.VIEW_FUNCTION_ITEM,
                            { config.configurationName },
                            *contextActions()
                        )
                    }
                }
            }
        }
        if (parent is MvModule) {
            val testConfig = AnyCommandConfigurationProducer.fromLocation(parent, climbUp = false)
            if (testConfig != null) {
                return Info(
                    MoveIcons.RUN_ALL_TESTS_IN_ITEM,
                    { testConfig.configurationName },
                    *contextActions()
                )
            }
        }
        return null
    }
}

private fun contextActions(): Array<AnAction> {
    return ExecutorAction.getActions(0).toList()
//        .filter { it.toString().startsWith("Run context configuration") }
        .toTypedArray()
}
