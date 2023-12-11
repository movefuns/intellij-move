package org.sui.cli

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.psi.PsiDocumentManager
import org.sui.cli.manifest.MoveToml
import org.sui.cli.manifest.TomlDependency
import org.sui.cli.settings.SuiExec
import org.sui.cli.settings.moveSettings
import org.sui.cli.settings.suiPath
import org.sui.lang.toNioPathOrNull
import org.sui.lang.toTomlFile
import org.sui.openapiext.contentRoots
import org.sui.openapiext.resolveExisting
import org.sui.openapiext.toVirtualFile
import org.sui.stdext.iterateFiles
import org.sui.stdext.withExtended
import java.util.concurrent.CompletableFuture

class MoveProjectsSyncTask(
    project: Project,
    private val future: CompletableFuture<List<MoveProject>>
) : Task.Backgroundable(project, "Reloading Move packages", true) {

    override fun run(indicator: ProgressIndicator) {
        indicator.isIndeterminate = true

        fetchDependencies()

        val projects = PsiDocumentManager
            .getInstance(project)
            .commitAndRunReadAction(Computable { loadProjects(project) })
        project.moveSettings.state = project.moveSettings.state.also {
            val version = SuiExec.getVersion(project.suiPath)
            if (version == "") {
                it.isValidExec = false
            } else {
                it.isValidExec = true
            }
        }
        future.complete(projects)
    }

    private fun fetchDependencies() {
    }

    companion object {
        private data class DepId(val rootPath: String)

        fun loadProjects(project: Project): List<MoveProject> {
            val projects = mutableListOf<MoveProject>()
            for (contentRoot in project.contentRoots) {
                contentRoot.iterateFiles({ it.name == Consts.MANIFEST_FILE }) {
                    val rawDepQueue = ArrayDeque<Pair<TomlDependency, RawAddressMap>>()
                    val root = it.parent?.toNioPathOrNull() ?: return@iterateFiles true
                    val tomlFile = it.toTomlFile(project) ?: return@iterateFiles true

                    val moveToml = MoveToml.fromTomlFile(tomlFile, root)
                    rawDepQueue.addAll(moveToml.deps)

                    val rootPackage = MovePackage.fromMoveToml(moveToml) ?: return@iterateFiles true

                    val deps = mutableListOf<Pair<MovePackage, RawAddressMap>>()
                    val visitedDepIds = mutableSetOf(
                        DepId(rootPackage.contentRoot.path)
                    )
                    loadDependencies(project, moveToml, deps, visitedDepIds, true)

                    projects.add(MoveProject(project, rootPackage, deps))
                    true
                }
            }
            return projects
        }

        private fun loadDependencies(
            project: Project,
            rootMoveToml: MoveToml,
            deps: MutableList<Pair<MovePackage, RawAddressMap>>,
            visitedIds: MutableSet<DepId>,
            isRoot: Boolean,
        ) {
            var parsedDeps = rootMoveToml.deps
            if (isRoot) {
                parsedDeps = parsedDeps.withExtended(rootMoveToml.dev_deps)
            }
            for ((dep, addressMap) in parsedDeps) {
                val depRoot = dep.localPath()

                val depId = DepId(depRoot.toString())
                if (depId in visitedIds) continue

                val depTomlFile = depRoot
                    .resolveExisting(Consts.MANIFEST_FILE)
                    ?.toVirtualFile()
                    ?.toTomlFile(project) ?: continue
                val depMoveToml = MoveToml.fromTomlFile(depTomlFile, depRoot)

                // first try to parse MovePackage from dependency, no need for nested if parent is invalid
                val depPackage = MovePackage.fromMoveToml(depMoveToml) ?: continue

                // parse all nested dependencies with their address maps
                visitedIds.add(depId)
                loadDependencies(project, depMoveToml, deps, visitedIds, false)

                deps.add(Pair(depPackage, addressMap))
            }
        }
    }
}
