package org.sui.cli.sentryReporter

import org.sui.cli.MoveProject
import org.sui.cli.manifest.TomlDependency
import org.sui.openapiext.getTable
import org.sui.openapiext.getTablesByFirstSegment
import org.sui.openapiext.syntheticLibraries

@Suppress("PropertyName")
data class MoveTomlContext(
    val name: String,
    val dependencies_parsed: List<TomlDependency>,
    val dependencies_raw: List<String>,
)

data class SyntheticLibraryContext(val roots: List<String>)

data class MoveProjectContext(
    val moveToml: MoveTomlContext,
    val syntheticLibraries: List<SyntheticLibraryContext>
) {
    companion object {
        fun from(moveProject: MoveProject): MoveProjectContext {
            val tomlFile = moveProject.currentPackage.manifestTomlFile

            val rawDeps = mutableListOf<String>()
            val depsTable = tomlFile.getTable("dependencies")
            if (depsTable != null) {
                rawDeps.add(depsTable.text)
            }
            for (depInlineTable in tomlFile.getTablesByFirstSegment("dependencies")) {
                rawDeps.add(depInlineTable.text)
            }

            val moveTomt = MoveTomlContext(
                name = moveProject.currentPackage.packageName,
                dependencies_raw = rawDeps,
                dependencies_parsed = moveProject.currentPackage.moveToml.deps.map { it.first },
            )
            val syntheticLibraries =
                moveProject.project.syntheticLibraries
                    .map { SyntheticLibraryContext(it.allRoots.map { it.path }) }
            return MoveProjectContext(moveTomt, syntheticLibraries)
        }
    }
}