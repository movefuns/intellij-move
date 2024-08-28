package org.sui.lang.core.resolve2

import com.intellij.openapi.project.Project
import org.sui.lang.core.psi.MvModule
import org.sui.lang.core.psi.MvStruct
import org.sui.lang.preLoadItems
import org.sui.openapiext.allMoveFiles

//@Service(Service.Level.PROJECT)
class PreImportedModuleService(private val project: Project) {

    private val preImportedModules: MutableList<MvModule> = mutableListOf()
    private val preImportedItems: MutableList<MvStruct> = mutableListOf()

    init {
        addPreLoadModulesFromFile()
        addPreLoadItemsFromFile()
    }

    private fun addPreLoadItemsFromFile() {
        for (file in project.allMoveFiles()) {
            preImportedModules.addAll(file.preloadModules())
        }
        println(preImportedModules)
    }

    private fun addPreLoadModulesFromFile() {
        for (file in project.allMoveFiles()) {
            preImportedItems.addAll(file.preLoadItems())
        }
        println(preImportedItems)
    }

    fun getPreImportedModules(): List<MvModule> = preImportedModules
    fun getPreImportedItems(): List<MvStruct> = preImportedItems

    // 新增方法来修改 preImportedModules
    fun addPreImportedModule(module: MvModule) {
        preImportedModules.add(module)
    }

    fun removePreImportedModule(module: MvModule) {
        preImportedModules.remove(module)
    }

    fun clearPreImportedModules() {
        preImportedModules.clear()
    }

    // 新增方法来修改 preImportedNamedElements
    fun addPreImportedNamedElement(element: MvStruct) {
        preImportedItems.add(element)
    }

    fun removePreImportedNamedElement(element: MvStruct) {
        preImportedItems.remove(element)
    }

    fun clearPreImportedNamedElements() {
        preImportedItems.clear()
    }
}