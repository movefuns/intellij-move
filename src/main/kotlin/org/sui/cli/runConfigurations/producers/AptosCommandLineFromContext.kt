package org.sui.cli.runConfigurations.producers

import com.intellij.psi.PsiElement
import org.sui.cli.runConfigurations.AptosCommandLine

data class AptosCommandLineFromContext(
    val sourceElement: PsiElement,
    val configurationName: String,
    val commandLine: AptosCommandLine
)
