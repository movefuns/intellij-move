package org.sui.bytecode

import com.intellij.openapi.fileTypes.FileType
import org.sui.ide.MoveIcons

object SuiBytecodeFileType : FileType {
    override fun getIcon() = MoveIcons.SUI_LOGO
    override fun getName() = "SUI_BYTECODE"
    override fun getDefaultExtension() = "mv"
    override fun getDescription() = "Sui Move bytecode"
    override fun getDisplayName() = "Sui Move bytecode"
    override fun isBinary(): Boolean = true
}