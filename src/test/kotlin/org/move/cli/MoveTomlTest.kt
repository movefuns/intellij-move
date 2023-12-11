package org.sui.cli

import org.sui.cli.manifest.MoveToml
import org.sui.openapiext.toPsiFile
import org.sui.openapiext.toVirtualFile
import org.sui.utils.TestSuiProjectRootServiceImpl
import org.sui.utils.rootService
import org.sui.utils.tests.MvTestBase
import org.sui.utils.tests.base.TestCase
import org.toml.lang.psi.TomlFile
import java.nio.file.Paths

class MoveTomlTest : MvTestBase() {
    fun `test parse move package`() {
        val moveProjectRoot = Paths.get(TestCase.testResourcesPath).resolve("move_toml_project")
        (project.rootService as TestSuiProjectRootServiceImpl).modifyPath(moveProjectRoot)

        val manifestPath = moveProjectRoot.resolve(Consts.MANIFEST_FILE)
        val tomlFile = manifestPath.toVirtualFile()?.toPsiFile(project) as TomlFile

        val moveToml = MoveToml.fromTomlFile(tomlFile, moveProjectRoot)
        check(moveToml.packageTable?.name == "move_toml")
        check(moveToml.packageTable?.version == "0.1.0")
        check(moveToml.packageTable?.authors.orEmpty().isEmpty())
        check(moveToml.packageTable?.license == null)

        check(moveToml.addresses.size == 2)
        check(moveToml.addresses["Std"]!!.first == "0x1")
        check(moveToml.addresses["DiemFramework"]!!.first == "0xB1E55ED")

        check(moveToml.deps.size == 1)

        val movePackage = MovePackage.fromMoveToml(moveToml)!!
        check(movePackage.suiConfigYaml?.profiles == setOf("default", "emergency"))
    }
}
