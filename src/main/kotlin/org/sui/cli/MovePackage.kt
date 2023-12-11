package org.sui.cli

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.sui.cli.manifest.MoveToml
import org.sui.cli.manifest.SuiConfigYaml
import org.sui.lang.toNioPathOrNull
import org.sui.openapiext.checkReadAccessAllowed
import org.sui.openapiext.pathAsPath
import org.sui.openapiext.resolveExisting
import java.nio.file.Path

data class MovePackage(
    val project: Project,
    val contentRoot: VirtualFile,
    val moveToml: MoveToml,
    val suiConfigYaml: SuiConfigYaml?,
) {
    val packageName = this.moveToml.packageName ?: ""

    val sourcesFolder: VirtualFile? get() = contentRoot.takeIf { it.isValid }?.findChild("sources")
    val testsFolder: VirtualFile? get() = contentRoot.takeIf { it.isValid }?.findChild("tests")

    fun moveFolders(): List<VirtualFile> = listOfNotNull(sourcesFolder, testsFolder)

    fun layoutPaths(): List<Path> {
        val rootPath = contentRoot.takeIf { it.isValid }?.toNioPathOrNull() ?: return emptyList()
        val names = listOf(
            *MvProjectLayout.sourcesDirs,
            MvProjectLayout.testsDir,
            MvProjectLayout.buildDir
        )
        return names.mapNotNull { rootPath.resolveExisting(it) }
    }

    fun addresses(): PackageAddresses {
        val tomlMainAddresses = moveToml.declaredAddresses()
        val tomlDevAddresses = moveToml.declaredAddresses()

        val addresses = mutableAddressMap()
        addresses.putAll(tomlMainAddresses.values)
        // add placeholders defined in this package as address values
        addresses.putAll(tomlMainAddresses.placeholdersAsValues())
        // devs on top
        addresses.putAll(tomlDevAddresses.values)

        return PackageAddresses(addresses, tomlMainAddresses.placeholders)
    }

    companion object {
        fun fromMoveToml(moveToml: MoveToml): MovePackage? {
            checkReadAccessAllowed()
            val contentRoot = moveToml.tomlFile?.parent?.virtualFile ?: return null

            var suiConfigYaml: SuiConfigYaml? = null
            var root: VirtualFile? = contentRoot
            while (true) {
                if (root == null) break
                val candidatePath = root
                    .findChild(".sui")
                    ?.takeIf { it.isDirectory }
                    ?.findChild("config.yaml")
                if (candidatePath != null) {
                    suiConfigYaml = SuiConfigYaml.fromPath(candidatePath.pathAsPath) ?: break
                }
                root = root.parent
            }
            return MovePackage(moveToml.project, contentRoot, moveToml, suiConfigYaml)
        }
    }
}
