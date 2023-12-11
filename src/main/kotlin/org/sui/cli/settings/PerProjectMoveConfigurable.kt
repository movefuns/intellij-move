package org.sui.cli.settings

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.util.Disposer
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel

class PerProjectMoveConfigurable(val project: Project) : BoundConfigurable("Sui Move Language"),
                                                         SearchableConfigurable {

    override fun getId(): String = "org.sui.lang.settings"

    private val settingsState: MoveProjectSettingsService.State = project.moveSettings.state

    private val suiSettingsPanel = SuiSettingsPanel(showDefaultProjectSettingsLink = !project.isDefault)

    override fun createPanel(): DialogPanel {
        return panel {
            suiSettingsPanel.attachTo(this)
            group {
                row {
                    checkBox("Auto-fold specs in opened files")
                        .bindSelected(settingsState::foldSpecs)
                }
//                row {
//                    checkBox("Disable telemetry for new Run Configurations")
//                        .bindSelected(settingsState::disableTelemetry)
//                }
//                row {
//                    checkBox("Enable debug mode")
//                        .bindSelected(settingsState::debugMode)
//                    comment(
//                        "Enables some explicit crashes in the plugin code. Useful for the error reporting."
//                    )
//                }
//                row {
//                    checkBox("Skip fetching latest git dependencies for tests")
//                        .bindSelected(settingsState::skipFetchLatestGitDeps)
//                    comment(
//                        "Adds --skip-fetch-latest-git-deps to the test runs."
//                    )
//                }
            }
        }
    }

    override fun disposeUIResources() {
        super<BoundConfigurable>.disposeUIResources()
        Disposer.dispose(suiSettingsPanel)
    }

    override fun reset() {
        super<BoundConfigurable>.reset()
        suiSettingsPanel.panelData =
            SuiSettingsPanel.PanelData(SuiExec.fromSettingsFormat(settingsState.suiPath))
    }

    override fun isModified(): Boolean {
        if (super<BoundConfigurable>.isModified()) return true
        val panelData = suiSettingsPanel.panelData
        return panelData.suiExec.pathToSettingsFormat() != settingsState.suiPath
    }

    override fun apply() {
        super.apply()
        val newSettingsState = settingsState
        newSettingsState.suiPath = suiSettingsPanel.panelData.suiExec.pathToSettingsFormat()
        project.moveSettings.state = newSettingsState

    }
}
