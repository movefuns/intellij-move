package org.sui.cli.toolwindow

import com.intellij.diagnostic.PluginException
import com.intellij.execution.Location
import com.intellij.execution.PsiLocation
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import org.sui.cli.MoveProject
import org.sui.lang.core.psi.MvFunction
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.SwingUtilities
import javax.swing.tree.DefaultMutableTreeNode

class MoveEntrypointMouseAdapter : MouseAdapter() {

    override fun mouseClicked(e: MouseEvent) {
        if (e.clickCount < 2 || !SwingUtilities.isLeftMouseButton(e)) return

        val tree = e.source as? MoveProjectsTree ?: return
        val node = tree.selectionModel.selectionPath
            ?.lastPathComponent as? DefaultMutableTreeNode ?: return
        val selectedProject = tree.selectedProject?.project ?: return
        val function = when (val userObject = node.userObject) {
            is MoveProjectsTreeStructure.MoveSimpleNode.Entrypoint -> userObject.function
            is MoveProjectsTreeStructure.MoveSimpleNode.View -> userObject.function
            else -> return
        }
        val functionLocation =
            try {
                PsiLocation.fromPsiElement(function) ?: return
            } catch (e: PluginException) {
                // TODO: figure out why this exception is raised
                return
            }

        navigateTo(functionLocation, selectedProject)
    }

    /**
     * Navigates to the specified location in the project.
     *
     * @param location the location to navigate to
     * @param project  the project to navigate in
     */
    private fun navigateTo(location: Location<MvFunction>, project: Project) {
        val psiElement = location.psiElement
        val psiFile = psiElement.containingFile
        val virtualFile = psiFile.virtualFile

        val document = PsiDocumentManager.getInstance(project).getDocument(psiFile)
        val startOffset = psiElement.textOffset

        if (document != null) {
            val line = document.getLineNumber(startOffset)
            val column = startOffset - document.getLineStartOffset(line)
            val descriptor = OpenFileDescriptor(project, virtualFile, line, column)
            FileEditorManager.getInstance(project).openTextEditor(descriptor, true)
        }
    }
}
