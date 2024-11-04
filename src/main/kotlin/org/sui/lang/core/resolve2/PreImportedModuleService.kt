package org.sui.lang.core.resolve2

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiManager
import org.sui.lang.MoveFile
import org.sui.lang.MoveLanguage
import org.sui.lang.core.psi.MvModule
import org.sui.lang.core.psi.MvStruct
import org.sui.lang.core.psi.ext.structs
import org.sui.lang.core.psi.namespaceModule
import org.sui.lang.core.resolve.RsResolveProcessor
import org.sui.lang.core.resolve.process
import org.sui.lang.core.resolve.ref.Namespace
import org.sui.lang.preLoadItems
import org.sui.openapiext.allMoveFiles
import java.util.concurrent.ConcurrentHashMap

@Service(Service.Level.PROJECT)
class PreImportedModuleService(private val project: Project) {
    private val viewProviderCache = ConcurrentHashMap<VirtualFile, FileViewProvider>()

    private fun getOrCreateViewProvider(file: VirtualFile): FileViewProvider {
        return viewProviderCache.computeIfAbsent(file) {
            PsiManager.getInstance(project).findViewProvider(it)!!
        }
    }
    private val preImportedModules: MutableList<MvModule> = mutableListOf()
    private val preImportedItems: MutableList<MvStruct> = mutableListOf()

    init {
        loadPreDefinedModules()
        loadPreDefinedItems()
    }

    private fun loadPreDefinedModules() {
        for (file in project.allMoveFiles()) {
            val viewProvider = getOrCreateViewProvider(file.virtualFile)
            val psiFile = viewProvider.getPsi(MoveLanguage) as? MoveFile ?: continue

            psiFile.modules().forEach { module ->
                if (module.name in PRELOAD_STD_MODULES) {
                    preImportedModules.add(module)
                }

                val address = module.addressRef?.namedAddress?.identifier?.text ?: ""
                if (address == "sui" && module.name in PRELOAD_SUI_MODULES) {
                    preImportedModules.add(module)
                }
            }
        }
    }

    private fun loadPreDefinedItems() {
        for (file in project.allMoveFiles()) {
            file.modules().forEach { module ->
                module.structs()
                    .filter { it.name in PRELOAD_MODULE_ITEMS }
                    .forEach { preImportedItems.add(it) }
            }
        }
    }

    fun processPreImportedModules(ns: Set<Namespace>, processor: RsResolveProcessor): Boolean {
        // deal with pre-imported modules
        for (module in preImportedModules) {
            if (Namespace.MODULE in ns) {
                val name = module.name ?: continue
                if (processor.process(name, module, setOf(Namespace.MODULE))) return true
            }

//            // deal with pre-imported structs
//            for (item in module.structs()) {
//                if (item.name in PRELOAD_MODULE_ITEMS) {
//                    if (item.namespace in ns) {
//                        val name = item.name ?: continue
//                        if (processor.process(name, item, setOf(item.namespace))) return true
//                    }
//                }
//            }
        }

        for (item in preImportedItems) {
            if (item.namespace in ns) {
                val name = item.name ?: continue
                if (processor.process(name, item, setOf(item.namespace))) return true
            }
        }
        return false
    }

    fun processPreImportedItems(ns: Set<Namespace>, processor: RsResolveProcessor): Boolean {
        // deal with pre-imported structs
        for (item in preImportedItems) {
            if (item.namespace in ns) {
                val name = item.name ?: continue
                if (processor.process(name, item, setOf(item.namespace))) return true
            }
        }
        return false
    }

    fun getPreImportedModules(): List<MvModule> = preImportedModules
    fun getPreImportedItems(): List<MvStruct> = preImportedItems

    fun addPreImportedModule(module: MvModule) {
        preImportedModules.add(module)
    }

    fun removePreImportedModule(module: MvModule) {
        preImportedModules.remove(module)
    }

    fun clearPreImportedModules() {
        preImportedModules.clear()
    }

    fun addPreImportedNamedElement(element: MvStruct) {
        preImportedItems.add(element)
    }

    fun removePreImportedNamedElement(element: MvStruct) {
        preImportedItems.remove(element)
    }

    fun clearPreImportedNamedElements() {
        preImportedItems.clear()
    }

    companion object {
        val PRELOAD_STD_MODULES = setOf("vector", "option")
        val PRELOAD_SUI_MODULES = setOf("transfer", "object", "tx_context")
        val PRELOAD_MODULE_ITEMS = setOf("UID", "ID", "TxContext")

        fun getInstance(project: Project): PreImportedModuleService {
            return project.getService(PreImportedModuleService::class.java)
                ?: PreImportedModuleService(project)
        }
    }
}