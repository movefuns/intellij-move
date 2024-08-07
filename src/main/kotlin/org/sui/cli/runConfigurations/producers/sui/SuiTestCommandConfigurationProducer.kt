package org.sui.cli.runConfigurations.producers.sui

import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileSystemItem
import org.sui.cli.MoveProject
import org.sui.cli.runConfigurations.SuiCommandLine
import org.sui.cli.runConfigurations.producers.CommandConfigurationProducerBase
import org.sui.cli.runConfigurations.producers.SuiCommandLineFromContext
import org.sui.cli.runConfigurations.sui.SuiCommandConfigurationType
import org.sui.cli.runConfigurations.sui.cmd.SuiCommandConfigurationFactory
import org.sui.cli.settings.moveSettings
import org.sui.lang.MoveFile
import org.sui.lang.core.psi.MvFunction
import org.sui.lang.core.psi.MvModule
import org.sui.lang.core.psi.containingModule
import org.sui.lang.core.psi.ext.findMoveProject
import org.sui.lang.core.psi.ext.hasTestAttr
import org.sui.lang.core.psi.ext.hasTestFunctions
import org.sui.lang.moveProject
import org.toml.lang.psi.TomlFile

class SuiTestCommandConfigurationProducer : CommandConfigurationProducerBase() {

    override fun getConfigurationFactory() =
        SuiCommandConfigurationFactory(SuiCommandConfigurationType.getInstance())

    override fun fromLocation(location: PsiElement, climbUp: Boolean): SuiCommandLineFromContext? {
        return when {
            location is MoveFile -> {
                val module = location.modules().firstOrNull { it.hasTestFunctions() } ?: return null
                findTestModule(module, climbUp)
            }

            location is TomlFile && location.name == "Move.toml" -> {
                val moveProject = location.findMoveProject() ?: return null
                findTestProject(location, moveProject)
            }

            location is PsiDirectory -> {
                val moveProject = location.findMoveProject() ?: return null
                if (
                    location.virtualFile == moveProject.currentPackage.contentRoot
                    || location.virtualFile == moveProject.currentPackage.testsFolder
                ) {
                    findTestProject(location, moveProject)
                } else {
                    null
                }
            }

            else -> findTestFunction(location, climbUp) ?: findTestModule(location, climbUp)
        }
    }

    private fun findTestFunction(psi: PsiElement, climbUp: Boolean): SuiCommandLineFromContext? {
        val fn = findElement<MvFunction>(psi, climbUp) ?: return null
        if (!fn.hasTestAttr) return null

        val modName = fn.containingModule?.name ?: return null
        val functionName = fn.name ?: return null

        val confName = "Test $modName::$functionName"

        val arguments = buildList {
            addAll(arrayOf("$modName::$functionName"))
            addAll(cliFlagsFromProjectSettings(psi.project))
        }

        val moveProject = fn.moveProject ?: return null
        val rootPath = moveProject.contentRootPath ?: return null
        return SuiCommandLineFromContext(
            fn,
            confName,
            SuiCommandLine(
                "move test",
                arguments,
                workingDirectory = rootPath,
                environmentVariables = initEnvironmentVariables(psi.project)
            )
        )
    }

    private fun findTestModule(psi: PsiElement, climbUp: Boolean): SuiCommandLineFromContext? {
        val mod = findElement<MvModule>(psi, climbUp) ?: return null
        if (!mod.hasTestFunctions()) return null

        val modName = mod.name ?: return null
        val confName = "Test $modName"

        val arguments = buildList {
            addAll(arrayOf(modName))
            addAll(cliFlagsFromProjectSettings(psi.project))
        }

        val moveProject = mod.moveProject ?: return null
        val rootPath = moveProject.contentRootPath ?: return null
        return SuiCommandLineFromContext(
            mod,
            confName,
            SuiCommandLine(
                "move test",
                arguments,
                workingDirectory = rootPath,
                environmentVariables = initEnvironmentVariables(psi.project)
            )
        )
    }

    private fun findTestProject(
        location: PsiFileSystemItem,
        moveProject: MoveProject
    ): SuiCommandLineFromContext? {
        val packageName = moveProject.currentPackage.packageName
        val rootPath = moveProject.contentRootPath ?: return null

        val confName = "Test $packageName"
        val arguments = cliFlagsFromProjectSettings(location.project)

        return SuiCommandLineFromContext(
            location,
            confName,
            SuiCommandLine(
                "move test",
                arguments,
                workingDirectory = rootPath,
                environmentVariables = initEnvironmentVariables(location.project)
            )
        )
    }

    private fun initEnvironmentVariables(project: Project): EnvironmentVariablesData {
        val environmentMap = linkedMapOf<String, String>()
//        if (project.moveSettings.addCompilerV2CLIFlags) {
//            environmentMap[Consts.MOVE_COMPILER_V2_ENV] = "true"
//        }
        return EnvironmentVariablesData.create(environmentMap, true)
    }

    private fun cliFlagsFromProjectSettings(project: Project): List<String> =
        buildList {
            if (project.moveSettings.skipFetchLatestGitDeps) {
                add("--skip-fetch-latest-git-deps")
            }
//            if (project.moveSettings.dumpStateOnTestFailure) {
//                add("--dump")
//            }
//            if (project.moveSettings.addCompilerV2CLIFlags) {
//                addAll(arrayOf("--compiler-version", "v2"))
//                addAll(arrayOf("--language-version", "2.0"))
//            }
        }
}