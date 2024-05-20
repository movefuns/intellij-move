package org.sui.cli.runConfigurations

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.sui.cli.Consts
import org.sui.cli.settings.aptos.AptosExec
import org.sui.cli.settings.sui.SuiExec
import org.sui.openapiext.*
import org.sui.openapiext.common.isUnitTestMode
import org.sui.stdext.RsResult
import org.sui.stdext.unwrapOrElse

sealed class InitProjectCli {
    abstract fun init(
        project: Project,
        parentDisposable: Disposable,
        rootDirectory: VirtualFile,
        packageName: String,
    ): MvProcessResult<VirtualFile>

    data class Aptos(val aptosExec: AptosExec) : InitProjectCli() {
        override fun init(
            project: Project,
            parentDisposable: Disposable,
            rootDirectory: VirtualFile,
            packageName: String
        ): MvProcessResult<VirtualFile> {
            if (!isUnitTestMode) {
                checkIsBackgroundThread()
            }
            val commandLine = CliCommandLineArgs(
                "move",
                listOf(
                    "init",
                    "--name", packageName,
                    "--assume-yes"
                ),
                workingDirectory = project.rootPath
            )
            val aptosPath = this.aptosExec.toPathOrNull() ?: error("unreachable")
            commandLine.toGeneralCommandLine(aptosPath)
                .execute(parentDisposable)
                .unwrapOrElse { return RsResult.Err(it) }
            fullyRefreshDirectory(rootDirectory)

            val manifest =
                checkNotNull(rootDirectory.findChild(Consts.MANIFEST_FILE)) { "Can't find the manifest file" }
            return RsResult.Ok(manifest)
        }
    }

    data class Sui(val suiExec: SuiExec) : InitProjectCli() {
        override fun init(
            project: Project,
            parentDisposable: Disposable,
            rootDirectory: VirtualFile,
            packageName: String
        ): MvProcessResult<VirtualFile> {
            if (!isUnitTestMode) {
                checkIsBackgroundThread()
            }
            val commandLine = CliCommandLineArgs(
                "move",
                listOf(
                    "new", packageName,
                    "--path", "."
                ),
                workingDirectory = project.rootPath
            )
            val suiPath = this.suiExec.toPathOrNull() ?: error("unreachable")
            commandLine.toGeneralCommandLine(suiPath)
                .execute(parentDisposable)
                .unwrapOrElse { return RsResult.Err(it) }
            fullyRefreshDirectory(rootDirectory)

            val manifest =
                checkNotNull(rootDirectory.findChild(Consts.MANIFEST_FILE)) { "Can't find the manifest file" }
            return RsResult.Ok(manifest)
        }
    }
}
