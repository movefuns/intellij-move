/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.sui.ide.newProject

import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.Disposer
import com.intellij.platform.GeneratorPeerImpl
import com.intellij.ui.dsl.builder.panel
import org.sui.cli.runConfigurations.InitProjectCli
import org.sui.cli.settings.Blockchain
import org.sui.cli.settings.isValidExecutable
import org.sui.cli.settings.sui.ChooseSuiCliPanel
import javax.swing.JComponent

class MoveProjectGeneratorPeer(val parentDisposable: Disposable) : GeneratorPeerImpl<MoveProjectConfig>() {

    private val chooseSuiCliPanel = ChooseSuiCliPanel { checkValid?.run() }

    init {
        Disposer.register(parentDisposable, chooseSuiCliPanel)
    }

    private var checkValid: Runnable? = null
    private var blockchain: Blockchain = Blockchain.SUI

    override fun getSettings(): MoveProjectConfig {
        val initCli = InitProjectCli.Sui(this.chooseSuiCliPanel.selectedSuiExec)
        return MoveProjectConfig(blockchain, initCli)
    }

    override fun getComponent(myLocationField: TextFieldWithBrowseButton, checkValid: Runnable): JComponent {
        this.checkValid = checkValid
        return super.getComponent(myLocationField, checkValid)
    }

    override fun getComponent(): JComponent {
        return panel {
            chooseSuiCliPanel.attachToLayout(this)
        }
    }

    override fun validate(): ValidationInfo? {
        val suiPath = this.chooseSuiCliPanel.selectedSuiExec.toPathOrNull()
        if (suiPath == null || !suiPath.isValidExecutable()) {
            return ValidationInfo("Invalid path to $blockchain executable")
        }
        return null
    }
}
