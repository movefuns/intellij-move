package org.sui.cli.runConfigurations.producers

import com.intellij.psi.PsiElement
import org.sui.cli.runConfigurations.CliCommandLineArgs

data class CommandLineArgsFromContext(
    val sourceElement: PsiElement,
    val configurationName: String,
    val commandLineArgs: CliCommandLineArgs
)
