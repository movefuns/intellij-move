package org.sui.cli

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.GlobalSearchScopes
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import org.sui.cli.manifest.MoveToml
import org.sui.cli.manifest.SuiConfigYaml
import org.sui.lang.core.types.Address
import org.sui.lang.core.types.AddressLit
import org.sui.lang.toMoveFile
import org.sui.lang.toNioPathOrNull
import org.sui.openapiext.common.checkUnitTestMode
import org.sui.openapiext.contentRoots
import org.sui.stdext.chain
import org.sui.stdext.iterateMoveVirtualFiles
import org.sui.stdext.wrapWithList
import java.nio.file.Path

data class MoveProject(
    val project: Project,
    val currentPackage: MovePackage,
    val dependencies: List<Pair<MovePackage, RawAddressMap>>,
) : UserDataHolderBase() {

    val contentRoot: VirtualFile get() = this.currentPackage.contentRoot
    val contentRootPath: Path? get() = this.currentPackage.contentRoot.toNioPathOrNull()

    fun movePackages(): Sequence<MovePackage> = currentPackage.wrapWithList().chain(depPackages())
    fun depPackages(): List<MovePackage> = dependencies.map { it.first }.reversed()

    fun sourceFolders(): List<VirtualFile> {
        val folders = currentPackage.moveFolders().toMutableList()

        val depFolders = dependencies.asReversed().flatMap { it.first.moveFolders() }
        folders.addAll(depFolders)

        return folders
    }

    fun addresses(): PackageAddresses {
        return CachedValuesManager.getManager(this.project).getCachedValue(this) {
            val packageName = this.currentPackage.packageName

            val cumulativeAddresses = PackageAddresses(mutableAddressMap(), placeholderMap())
            for ((depPackage, subst) in this.dependencies) {
                cumulativeAddresses.extendWith(depPackage.addresses())
                cumulativeAddresses.applySubstitution(subst, packageName)
            }
            cumulativeAddresses.extendWith(this.currentPackage.addresses())

            CachedValueProvider.Result.create(
                cumulativeAddresses,
                PsiModificationTracker.MODIFICATION_COUNT
            )
        }
    }

    fun addressValues(): AddressMap {
        return addresses().values
    }

    fun currentPackageAddresses(): AddressMap {
        val addresses = this.currentPackage.addresses()
        val map = mutableAddressMap()
        map.putAll(addresses.values)
        map.putAll(addresses.placeholdersAsValues())
        return map
    }

    fun getNamedAddressValue(name: String): String? = addressValues()[name]?.value

    fun getNamedAddress(name: String): Address.Named? {
        val value = getNamedAddressValue(name) ?: return null
        return Address.Named(name, value, this)
    }

    fun getAddressNamesForValue(addressValue: String): List<String> {
        val addressLit = AddressLit(addressValue)
        val names = mutableListOf<String>()
        for ((name, value) in addresses().values.entries) {
            val canonicalValue = value.literal.canonical()
            if (canonicalValue == addressLit.canonical()) {
                names.add(name)
            }
        }
        return names
    }

    fun searchScope(): GlobalSearchScope {
        var searchScope = GlobalSearchScope.EMPTY_SCOPE
        for (folder in sourceFolders()) {
            val dirScope = GlobalSearchScopes.directoryScope(project, folder, true)
            searchScope = searchScope.uniteWith(dirScope)
        }
        return searchScope
    }

    val suiConfigYaml: SuiConfigYaml? get() = this.currentPackage.suiConfigYaml

    val profiles: Set<String> = this.suiConfigYaml?.profiles.orEmpty()

    fun processMoveFiles(processFile: (org.sui.lang.MoveFile) -> Boolean) {
        val folders = sourceFolders()
        var stopped = false
        for (folder in folders) {
            if (stopped) break
            folder.iterateMoveVirtualFiles {
                val moveFile = it.toMoveFile(project) ?: return@iterateMoveVirtualFiles true
                val continueForward = processFile(moveFile)
                stopped = !continueForward
                continueForward
            }
        }
    }

    override fun toString(): String {
        return "MoveProject(" +
                "root=${this.contentRoot.path}, " +
                "deps=${this.depPackages().map { it.contentRoot.path }})"
    }

    companion object {
        fun forTests(project: Project): MoveProject {
            checkUnitTestMode()
            val contentRoot = project.contentRoots.first()
            val moveToml = MoveToml(project)
            val movePackage = MovePackage(project, contentRoot, moveToml, null)
            return MoveProject(
                project,
                movePackage,
                dependencies = emptyList()
            )
        }
    }
}
