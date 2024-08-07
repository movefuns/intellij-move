package org.sui.cli.toolwindow

import com.intellij.ide.DefaultTreeExpander
import com.intellij.ide.TreeExpander
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.content.ContentFactory
import org.sui.cli.MoveProject
import org.sui.cli.MoveProjectsService
import org.sui.cli.hasMoveProject
import org.sui.cli.moveProjectsService
import javax.swing.JComponent

class SuiToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        if (!project.moveProjectsService.hasAtLeastOneValidProject) {
            project.moveProjectsService
                .scheduleProjectsRefresh("Sui Tool Window opened")
        }

        val toolwindowPanel = SuiToolWindowPanel(project)
        val tab = ContentFactory.getInstance()
            .createContent(toolwindowPanel, "", false)
        toolWindow.contentManager.addContent(tab)
    }

    // TODO: isApplicable() and initializeToolWindow() cannot be copied from intellij-rust in 241,
    //       implement it instead with ExternalToolWindowManager later
}

private class SuiToolWindowPanel(project: Project) : SimpleToolWindowPanel(true, false) {
    private val suiTab = SuiToolWindow(project)

    init {
        toolbar = suiTab.toolbar.component
        suiTab.toolbar.targetComponent = this
        setContent(suiTab.content)
    }

    override fun getData(dataId: String): Any? =
        when {
            SuiToolWindow.SELECTED_MOVE_PROJECT.`is`(dataId) -> suiTab.selectedProject
            PlatformDataKeys.TREE_EXPANDER.`is`(dataId) -> suiTab.treeExpander
            else -> super.getData(dataId)
        }
}

class SuiToolWindow(private val project: Project) {

    val toolbar: ActionToolbar = run {
        val actionManager = ActionManager.getInstance()
        actionManager.createActionToolbar(
            SUI_TOOLBAR_PLACE,
            actionManager.getAction("Move.Sui") as DefaultActionGroup,
            true
        )
    }

    private val projectTree = MoveProjectsTree()
    private val projectStructure = MoveProjectsTreeStructure(projectTree, project)

    val treeExpander: TreeExpander = object : DefaultTreeExpander(projectTree) {
        override fun isCollapseAllVisible(): Boolean = project.hasMoveProject
        override fun isExpandAllVisible(): Boolean = project.hasMoveProject
    }

    val selectedProject: MoveProject? get() = projectTree.selectedProject

    val content: JComponent = ScrollPaneFactory.createScrollPane(projectTree, 0)

    init {
        with(project.messageBus.connect()) {
            subscribe(MoveProjectsService.MOVE_PROJECTS_TOPIC, MoveProjectsService.MoveProjectsListener { _, projects ->
                invokeLater {
                    projectStructure.updateMoveProjects(projects.toList())
                }
            })
        }
        invokeLater {
            projectStructure.updateMoveProjects(project.moveProjectsService.allProjects.toList())
        }
    }

    companion object {
        @JvmStatic
        val SELECTED_MOVE_PROJECT: DataKey<MoveProject> = DataKey.create("SELECTED_MOVE_PROJECT")

        const val SUI_TOOLBAR_PLACE: String = "Sui Toolbar"
    }
}
