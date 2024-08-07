package org.sui.cli.sdks

import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.ControlFlowException
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.projectRoots.impl.jdkDownloader.JdkDownloaderLogger
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.component1
import com.intellij.openapi.util.component2
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.download.DownloadableFileService
import com.intellij.util.io.Decompressor
import org.sui.ide.notifications.MvNotifications
import java.io.*
import java.util.zip.GZIPInputStream

class DownloadSuiSdkTask(
    private val suiSdk: SuiSdk,
    private val onFinish: (SuiSdk) -> Unit
) :
    Task.Modal(null, "Sui SDK Installer", true) {

    override fun run(indicator: ProgressIndicator) {
        indicator.text = "Installing Sui SDK v${suiSdk.version}..."

        indicator.text2 = "Fetching ${suiSdk.githubArchiveUrl}"
        val tmpDownloadDir = File(FileUtil.getTempDirectory(), "sui-clis")

        val archiveFileName = suiSdk.githubArchiveFileName
        val tmpExtractionDir =
            tmpDownloadDir.resolve(
                FileUtil.getNameWithoutExtension(suiSdk.githubArchiveFileName)
            )

        val url = suiSdk.githubArchiveUrl
        try {
            val tmpDownloadFile: File
            try {
                val downloadService = DownloadableFileService.getInstance()
                val downloader = downloadService.createDownloader(
                    listOf(
                        downloadService.createFileDescription(url, suiSdk.githubArchiveFileName)
                    ),
                    "Download Sui SDK"
                )
                val (file, _) = downloader.download(tmpDownloadDir).first()
                tmpDownloadFile = file
            } catch (e: IOException) {
                throw RuntimeException(
                    "Failed to download $archiveFileName from $url. ${e.message}",
                    e
                )
            }

            indicator.isIndeterminate = true
            indicator.text = "Installing Sui SDK..."

            indicator.text2 = "Unpacking $archiveFileName"
            try {
                tmpExtractionDir.mkdir()

                val tarFile = File(tmpExtractionDir, tmpDownloadFile.nameWithoutExtension) // 假设输出目录已存在

                // 将 .tgz 文件解压成 .tar 文件
                GZIPInputStream(FileInputStream(tmpDownloadFile)).use { gis ->
                    BufferedOutputStream(FileOutputStream(tarFile)).use { bos ->
                        gis.copyTo(bos)
                    }
                }
                Decompressor.Tar(tarFile)
                    .entryFilter { indicator.checkCanceled(); true }
                    .extract(tmpExtractionDir)
            } catch (t: Throwable) {
                if (t is ControlFlowException) throw t
                throw RuntimeException(
                    "Failed to extract $tmpDownloadFile. ${t.message}",
                    t
                )
            }

            val extractedFile = tmpExtractionDir.resolve(if (SystemInfo.isWindows) "sui.exe" else "sui")
            try {
                FileUtil.copy(extractedFile, suiSdk.targetFile)
            } catch (t: Throwable) {
                if (t is ControlFlowException) throw t
                throw RuntimeException(
                    "Failed to copy from $extractedFile to ${suiSdk.targetFile}. ${t.message}",
                    t
                )
            }

            onFinish(suiSdk)

        } catch (t: Throwable) {
            //if we were cancelled in the middle or failed, let's clean up
            JdkDownloaderLogger.logDownload(false)

            // Create and display a notification
            MvNotifications.pluginNotifications().createNotification(
                "Sui SDK Download Failed",
                "Failed to download and install Sui SDK v${suiSdk.version}. ${t.message}",
                NotificationType.ERROR
            ).notify(project)

            throw t
        } finally {
            runCatching { FileUtil.delete(tmpExtractionDir) }
        }
    }
}
