package org.sui.cli.runConfigurations.producers

import com.intellij.psi.PsiElement
import org.sui.cli.runConfigurations.SuiCommandLine

data class SuiCommandLineFromContext(
    val sourceElement: PsiElement,
    val configurationName: String,
    val commandLine: SuiCommandLine
)
