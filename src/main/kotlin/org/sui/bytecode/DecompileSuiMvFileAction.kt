package org.sui.bytecode

import com.intellij.notification.NotificationType.ERROR
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAwareAction
import org.sui.bytecode.SuiBytecodeNotificationProvider.DecompilationModalTask
import org.sui.cli.settings.getSuiCli
import org.sui.ide.MoveIcons
import org.sui.ide.notifications.showBalloon
import org.sui.openapiext.openFile
import org.sui.stdext.unwrapOrElse
import java.util.*

class DecompileSuiMvFileAction : DumbAwareAction("Decompile .mv File", null, MoveIcons.SUI_LOGO) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getData(CommonDataKeys.PSI_FILE)?.virtualFile ?: return
        val decompilationTask = DecompilationModalTask.forVirtualFile(project, file)
            ?: run {
                project.showBalloon("Error with decompilation process", "Sui CLI is not configured", ERROR)
                return
            }
        val decompiledFile = decompilationTask.runWithProgress()
            .unwrapOrElse {
                project.showBalloon("Error with decompilation process", it, ERROR)
                return
            }
        project.openFile(decompiledFile)
    }

    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.PSI_FILE)
        val presentation = e.presentation
        val enabled =
            (file != null
                    && Objects.nonNull(file.virtualFile) && !(file.virtualFile.fileSystem.isReadOnly)
                    && file.fileType == SuiBytecodeFileType
                    && e.getData(CommonDataKeys.PROJECT)?.getSuiCli() != null)
        presentation.isEnabledAndVisible = enabled
    }
}