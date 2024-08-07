package org.sui.cli.settings.sui

import com.intellij.ide.DataManager
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.JBPopupFactory.ActionSelectionAid.SPEEDSEARCH
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.registry.Registry
import com.intellij.ui.components.DropDownLink
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.Row
import org.sui.cli.sdks.sdksService
import org.sui.cli.settings.MvProjectSettingsService
import org.sui.cli.settings.VersionLabel
import org.sui.cli.settings.isValidExecutable
import org.sui.cli.settings.sui.SuiExecType.BUNDLED
import org.sui.cli.settings.sui.SuiExecType.LOCAL
import org.sui.ide.actions.DownloadSuiSDKAction
import org.sui.ide.notifications.logOrShowBalloon
import org.sui.openapiext.PluginPathManager
import org.sui.openapiext.pathField
import org.sui.stdext.blankToNull
import org.sui.stdext.toPathOrNull
import java.nio.file.Path

enum class SuiExecType {
    BUNDLED,
    LOCAL;

    companion object {
        val isPreCompiledSupportedForThePlatform: Boolean
            get() {
                if (Registry.`is`("org.move.sui.bundled.force.supported", false)) {
                    return true
                }
                if (Registry.`is`("org.move.sui.bundled.force.unsupported", false)) {
                    return false
                }
                return true
            }

        fun bundledPath(): String? = PluginPathManager.bundledSuiCli

        fun suiExecPath(execType: SuiExecType, localSuiPath: String?): Path? {
            val pathCandidate = localSuiPath?.blankToNull()?.toPathOrNull()
            return pathCandidate?.takeIf { it.isValidExecutable() }
        }
    }
}

class ChooseSuiCliPanel(versionUpdateListener: (() -> Unit)?) : Disposable {

    data class Data(
        val suiExecType: SuiExecType,
        val localSuiPath: String?
    )

    var data: Data
        get() {
            val execType = LOCAL
            val path = localPathField.text.blankToNull()
            return Data(
                suiExecType = execType,
                localSuiPath = path
            )
        }
        set(value) {
            when (value.suiExecType) {
                BUNDLED -> {
                    bundledRadioButton.isSelected = true
                    localRadioButton.isSelected = false
                }

                LOCAL -> {
                    bundledRadioButton.isSelected = false
                    localRadioButton.isSelected = true
                }
            }
            localPathField.text = value.localSuiPath ?: ""
            updateVersion()
        }

    private val localPathField =
        pathField(
            FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor(),
            this,
            "Choose Sui CLI",
            onTextChanged = { _ ->
                updateVersion()
            })
    private val versionLabel = VersionLabel(this, versionUpdateListener)

    private val bundledRadioButton = JBRadioButton("Bundled")
    private val localRadioButton = JBRadioButton("Local")

    private val downloadPrecompiledBinaryAction = DownloadSuiSDKAction().also {
        it.onFinish = { sdk ->
            bundledRadioButton.isSelected = false
            localRadioButton.isSelected = true
            localPathField.text = sdk.targetFile.toString()
            updateVersion()
        }
    }
    private val popupActionGroup = DefaultActionGroup(
        listOfNotNull(
            if (SuiExecType.isPreCompiledSupportedForThePlatform) downloadPrecompiledBinaryAction else null
        )
    )
    private val getSuiActionLink =
        DropDownLink("Get Sui") { dropDownLink ->
            val dataContext = DataManager.getInstance().getDataContext(dropDownLink)
            JBPopupFactory.getInstance().createActionGroupPopup(
                null,
                popupActionGroup,
                dataContext,
                SPEEDSEARCH,
                false,
                null,
                -1,
                { _ -> false },
                null
            )
        }


    fun attachToLayout(layout: Panel): Row {
        val resultRow = with(layout) {
            group("Sui CLI") {
                buttonsGroup {
//                    row {
//                        cell(bundledRadioButton)
//                            .enabled(SuiExecType.isPreCompiledSupportedForThePlatform)
//                            .actionListener { _, _ ->
//                                updateVersion()
//                            }
//                    }
//                    row {
//                        comment(
//                            "Bundled version is not available for MacOS. Refer to the " +
//                                    "<a href=\"https://aptos.dev/tools/aptos-cli/install-cli/install-cli-mac\">Official Aptos CLI docs</a> " +
//                                    "on how to install it on your platform."
//                        )
//                            .visible(!SuiExecType.isPreCompiledSupportedForThePlatform)
//                    }
                    row {
//                        cell(localRadioButton)
//                            .actionListener { _, _ ->
//                                updateVersion()
//                            }
                        cell(localPathField)
                            .align(AlignX.FILL)
                            .resizableColumn()
//                            .enabledIf(localRadioButton.selected)
                        if (popupActionGroup.childrenCount != 0) {
                            cell(getSuiActionLink)
                        }
                    }
                    row("--version :") { cell(versionLabel) }
//                    row {
//                        comment(
//                            "Bundled version of the Aptos CLI can be outdated. Refer to the " +
//                                    "<a href=\"https://aptos.dev/tools/aptos-cli/install-cli\">Official Aptos CLI docs</a> " +
//                                    "on how to install and update new version for your platform."
//                        )
//                            .visible(AptosExecType.isPreCompiledSupportedForThePlatform)
//                    }
                }
            }
        }
        updateVersion()
        return resultRow
    }

    private fun updateVersion() {
        val suiPath = localPathField.text.toPathOrNull()
        versionLabel.updateAndNotifyListeners(suiPath)
    }

    fun updateSuiSdks(sdkPath: String) {
        if (sdkPath == "") return

        // do not save if the executable has no `--version`
        if (versionLabel.isError()) return

        // do not save if it's not an sui
        if ("sui" !in versionLabel.text) return

        val settingsService = sdksService()
        if (sdkPath in settingsService.state.suiSdkPaths) return

        settingsService.state.suiSdkPaths.add(sdkPath)
        // update default path
        ProjectManager.getInstance().defaultProject.getService(MvProjectSettingsService::class.java).modify {
            it.localSuiPath = sdkPath
        }

        LOG.logOrShowBalloon("Sui SDK saved: $sdkPath")
    }

    override fun dispose() {
        Disposer.dispose(localPathField)
    }

    companion object {
        private val LOG = logger<ChooseSuiCliPanel>()
    }
}
