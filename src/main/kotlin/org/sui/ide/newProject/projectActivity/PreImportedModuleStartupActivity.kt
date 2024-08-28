package org.sui.ide.newProject.projectActivity

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import org.sui.lang.core.resolve2.PreImportedModuleService

class PreImportedModuleStartupActivity : StartupActivity {
    override fun runActivity(project: Project) {
        project.getService(PreImportedModuleService::class.java)
    }
}