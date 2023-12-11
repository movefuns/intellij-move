package org.sui.ide.formatter

import com.intellij.formatting.Alignment
import com.intellij.formatting.SpacingBuilder
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import org.sui.ide.formatter.impl.createSpacingBuilder
import org.sui.lang.MoveLanguage

data class MvFmtContext(
    val commonSettings: CommonCodeStyleSettings,
    val spacingBuilder: SpacingBuilder,
    val sharedAlignment: Alignment? = null,
) {
    companion object {
        fun create(settings: CodeStyleSettings): MvFmtContext {
            val commonSettings = settings.getCommonSettings(MoveLanguage)
            return MvFmtContext(
                commonSettings = commonSettings,
                spacingBuilder = createSpacingBuilder(commonSettings),
                sharedAlignment = null
            )
        }
    }

}
