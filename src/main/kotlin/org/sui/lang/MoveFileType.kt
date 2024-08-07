package org.sui.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import org.sui.ide.MoveIcons

object MoveFileType : LanguageFileType(MoveLanguage) {
    override fun getIcon() = MoveIcons.SUI_LOGO
    override fun getName() = "Sui Move"
    override fun getDefaultExtension() = "move"
    override fun getDescription() = "Move Language file"
}
