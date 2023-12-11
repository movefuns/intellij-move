package org.sui.ide.newProject

import com.intellij.ide.util.PsiNavigationSupport
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.sui.cli.runConfigurations.sui.SuiCliExecutor.Companion.GeneratedFilesHolder
import org.sui.openapiext.common.isHeadlessEnvironment

fun Project.openFile(file: VirtualFile) = openFiles(GeneratedFilesHolder(file))

fun Project.openFiles(files: GeneratedFilesHolder) = invokeLater {
    if (!isHeadlessEnvironment) {
        val navigation = PsiNavigationSupport.getInstance()
        navigation.createNavigatable(this, files.manifest, -1).navigate(false)
    }
}
