package org.sui.cli.settings.sui

import com.intellij.openapi.Disposable
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.util.Disposer
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.bindText
import org.sui.cli.settings.VersionLabel
import org.sui.openapiext.pathField
import org.sui.stdext.toPathOrNull

class ChooseSuiCliPanel(
    private val versionUpdateListener: (() -> Unit)? = null
) : Disposable {

    private val localPathField =
        pathField(
            FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor(),
            this,
            "Choose Sui CLI",
            onTextChanged = { text ->
//                if ("" != text && "null" != text) {
                    val exec = SuiExec.LocalPath(text)
                    _suiExec = exec
                exec.toPathOrNull()?.let { versionLabel.updateAndNotifyListeners(it) }
//                }
            })

    private val versionLabel = VersionLabel(this, versionUpdateListener)

    private lateinit var _suiExec: SuiExec

    var selectedSuiExec: SuiExec
        get() = _suiExec
        set(suiExec) {
            this._suiExec = suiExec
            localPathField.text = suiExec.execPath
            suiExec.toPathOrNull()?.let { versionLabel.updateAndNotifyListeners(it) }
        }

    private lateinit var _suiCliPath: String

    fun getSuiCliPath(): String {
        return _suiCliPath
    }

    fun attachToLayout(layout: Panel): Row {
        val panel = this
        if (!panel::_suiExec.isInitialized) {
            panel._suiExec = SuiExec.default()
        }
        val resultRow = with(layout) {
            group("Sui CLI") {
                row {
                    cell(localPathField)
                        .bindText(
                            { _suiExec.toPathOrNull()?.toString() ?: "" },
                            { _suiExec = SuiExec.LocalPath(localPathField.toString()) }
                        )
                        .onChanged {
                            localPathField.text.toPathOrNull()?.let { versionLabel.updateAndNotifyListeners(it) }
                        }
                        .align(AlignX.FILL).resizableColumn()
                }
                row("--version :") { cell(versionLabel) }
            }
        }
        _suiExec.toPathOrNull()?.let { versionLabel.updateAndNotifyListeners(it) }
        return resultRow
    }

    override fun dispose() {
        Disposer.dispose(localPathField)
    }
}
