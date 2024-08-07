/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.sui.ide.newProject

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.Disposer
import com.intellij.platform.GeneratorPeerImpl
import com.intellij.ui.dsl.builder.panel
import org.sui.cli.settings.MvProjectSettingsService
import org.sui.cli.settings.sui.ChooseSuiCliPanel
import org.sui.cli.settings.sui.SuiExecType
import org.sui.stdext.getCliFromPATH
import javax.swing.JComponent

class MoveProjectGeneratorPeer(val parentDisposable: Disposable) : GeneratorPeerImpl<MoveProjectConfig>() {

    private val chooseSuiCliPanel = ChooseSuiCliPanel { checkValid?.run() }

    init {
        Disposer.register(parentDisposable, chooseSuiCliPanel)

        // set values from the default project settings
        val defaultProjectSettings =
            ProjectManager.getInstance().defaultProject.getService(MvProjectSettingsService::class.java)

        val localSuiPath =
            defaultProjectSettings.localSuiPath ?: getCliFromPATH("sui")?.toString()
        chooseSuiCliPanel.data =
            ChooseSuiCliPanel.Data(defaultProjectSettings.suiExecType, localSuiPath)
    }

    private var checkValid: Runnable? = null

    override fun getSettings(): MoveProjectConfig {
        val localSuiPath = this.chooseSuiCliPanel.data.localSuiPath
        if (localSuiPath != null) {
            this.chooseSuiCliPanel.updateSuiSdks(localSuiPath)
        }
        return MoveProjectConfig(
            suiExecType = this.chooseSuiCliPanel.data.suiExecType,
            localSuiPath = localSuiPath,
        )
    }

    override fun getComponent(myLocationField: TextFieldWithBrowseButton, checkValid: Runnable): JComponent {
        this.checkValid = checkValid
        return super.getComponent(myLocationField, checkValid)
    }

    override fun getComponent(): JComponent {
        return panel {
            chooseSuiCliPanel
                .attachToLayout(this)
        }
    }

    override fun validate(): ValidationInfo? {
        val panelData = this.chooseSuiCliPanel.data
        val suiExecPath =
            SuiExecType.suiExecPath(panelData.suiExecType, panelData.localSuiPath)
        if (suiExecPath == null) {
            return ValidationInfo("Invalid path to Sui executable")
        }
        return null
    }
}
