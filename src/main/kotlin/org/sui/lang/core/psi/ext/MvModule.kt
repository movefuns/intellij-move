package org.sui.lang.core.psi.ext

import com.intellij.ide.projectView.PresentationData
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValuesManager.getProjectPsiDependentCache
import org.sui.ide.MoveIcons
import org.sui.ide.annotator.PRELOAD_STD_MODULES
import org.sui.ide.annotator.PRELOAD_SUI_MODULES
import org.sui.lang.core.completion.getOriginalOrSelf
import org.sui.lang.core.psi.*
import org.sui.lang.core.stubs.MvFunctionStub
import org.sui.lang.core.stubs.MvModuleStub
import org.sui.lang.core.stubs.MvStructStub
import org.sui.lang.core.stubs.MvStubbedNamedElementImpl
import org.sui.lang.core.stubs.ext.childrenStubsOfType
import org.sui.lang.core.types.Address
import org.sui.lang.core.types.ItemQualName
import org.sui.lang.core.types.address
import org.sui.lang.index.MvModuleSpecIndex
import org.sui.lang.moveProject
import org.sui.utils.cache
import org.sui.utils.cacheManager
import org.sui.utils.psiCacheResult
import javax.swing.Icon

fun MvModule.hasTestFunctions(): Boolean = this.testFunctions().isNotEmpty()

fun MvModule.addressRef(): MvAddressRef? =
    this.addressRef ?: (this.ancestorStrict<MvAddressDef>())?.addressRef

val MvModule.friendModules: Sequence<MvModule>
    get() {
        return this.friendDeclList
            .asSequence()
            .mapNotNull { it.path?.reference?.resolveFollowingAliases() as? MvModule }
//        return sequence {
//        }
//        val friends = mutableSetOf<MvModule>()
//        for (modulePath in friendModulePaths) {
//            val module = modulePath.reference?.resolveFollowingAliases() as? MvModule ?: continue
//            friends.add(module)
//        }
//        return friends
    }

fun MvModule.allFunctions(): List<MvFunction> {
    val stub = greenStub
    return stub?.childrenStubsOfType<MvFunctionStub>()?.map { it.psi } ?: functionList
}

fun MvModule.allNonTestFunctions(): List<MvFunction> =
//    allFunctions().filter { f -> !f.isTest }
    this.allFunctions().filter { f -> !f.hasTestAttr }
//    getProjectPsiDependentCache(this) {
//    }

fun MvModule.testFunctions(): List<MvFunction> =
    getProjectPsiDependentCache(this) {
        it.allFunctions().filter { f -> f.hasTestAttr }
    }

val MvModule.isBuiltins: Boolean get() = this.name == "builtins" && (this.address(null)?.is0x0 ?: false)
val MvModule.isSpecBuiltins: Boolean
    get() = this.name == "spec_builtins" && (this.address(null)?.is0x0 ?: false)

fun MvModule.builtinFunctions(): List<MvFunction> {
    return getProjectPsiDependentCache(this) {
        val text = """
            /// Removes `T` from address and returns it. 
            /// Aborts if address does not hold a `T`.
            native fun move_from<T: key>(addr: address): T acquires T;
                        
            /// Publishes `T` under `signer.address`. 
            /// Aborts if `signer.address` already holds a `T`.
            native fun move_to<T: key>(acc: &signer, res: T);
                                    
            native fun borrow_global<T: key>(addr: address): &T acquires T;           
                                     
            native fun borrow_global_mut<T: key>(addr: address): &mut T acquires T;
            
            /// Returns `true` if a `T` is stored under address
            native fun exists<T: key>(addr: address): bool;
            
            native fun freeze<S>(mut_ref: &mut S): &S;
        """.trimIndent()
        val builtinFunctions = it.project.psiFactory.functions(text, moduleName = "builtins")
        builtinFunctions.forEach { f -> (f as MvFunctionMixin).builtIn = true }
        builtinFunctions
    }
}

fun MvModule.entryFunctions(): List<MvFunction> = this.allFunctions().filter { it.isEntry }

fun MvModule.viewFunctions(): List<MvFunction> = this.allFunctions().filter { it.isView }

fun MvModule.specInlineFunctions(): List<MvSpecInlineFunction> =
    this.moduleItemSpecList.flatMap { it.specInlineFunctions() }

fun builtinSpecFunction(text: String, project: Project): MvSpecFunction {
    val trimmedText = text.trimIndent()
    return project.psiFactory.specFunction(trimmedText, moduleName = "builtin_spec_functions")
}

fun MvModule.structs(): List<MvStruct> {
    return getProjectPsiDependentCache(this) {
        val stub = it.greenStub
        stub?.childrenStubsOfType<MvStructStub>()?.map { s -> s.psi } ?: it.structList
    }
}

fun MvModule.builtinModules(): List<MvModule> {
    return getProjectPsiDependentCache(this) {
        val project = it.project
        listOf(
            builtinModule("transfer", project),
            builtinModule("object", project),
        )
    }
}

// TODO
fun builtinModule(text: String, project: Project): MvModule {
    val trimmedText = text.trimIndent()
    return project.psiFactory.inlineModule(trimmedText, "", "")
}

fun MvModule.builtinSpecFunctions(): List<MvSpecFunction> {
    return getProjectPsiDependentCache(this) {
        val project = it.project
        listOf(
            builtinSpecFunction("spec native fun max_u8(): num;", project),
            builtinSpecFunction("spec native fun max_u64(): num;", project),
            builtinSpecFunction("spec native fun max_u128(): num;", project),
            builtinSpecFunction("spec native fun global<T: key>(addr: address): T;", project),
            builtinSpecFunction("spec native fun old<T>(_: T): T;", project),
            builtinSpecFunction(
                "spec native fun update_field<S, F, V>(s: S, fname: F, val: V): S;",
                project
            ),
            builtinSpecFunction("spec native fun TRACE<T>(_: T): T;", project),
            // vector functions
            builtinSpecFunction(
                "spec native fun concat<T>(v1: vector<T>, v2: vector<T>): vector<T>;",
                project
            ),
            builtinSpecFunction("spec native fun vec<T>(_: T): vector<T>;", project),
            builtinSpecFunction("spec native fun len<T>(_: vector<T>): num;", project),
            builtinSpecFunction("spec native fun contains<T>(v: vector<T>, e: T): bool;", project),
            builtinSpecFunction("spec native fun index_of<T>(_: vector<T>, _: T): num;", project),
            builtinSpecFunction("spec native fun range<T>(_: vector<T>): range;", project),
            builtinSpecFunction("spec native fun update<T>(_: vector<T>, _: num, _: T): vector<T>;", project),
            builtinSpecFunction("spec native fun in_range<T>(_: vector<T>, _: num): bool;", project),
            builtinSpecFunction("spec native fun int2bv(_: num): bv;", project),
            builtinSpecFunction("spec native fun bv2int(_: bv): num;", project),
        )
    }
}

fun MvModule.specFunctions(): List<MvSpecFunction> = specFunctionList.orEmpty()

fun MvModule.consts(): List<MvConst> = this.constList

fun MvModule.enumVariants(): List<MvEnumVariant> = this.enumList.flatMap { it.variants }

//fun MvModuleBlock.moduleItemSpecs() = this.moduleItemSpecList
////    this.childrenOfType<MvItemSpec>()
////        .filter { it.itemSpecRef?.moduleKw != null }

val MvModuleSpec.moduleItem: MvModule? get() = this.path?.reference?.resolve() as? MvModule

val MvModuleSpecBlock.moduleSpec: MvModuleSpec get() = this.parent as MvModuleSpec

fun MvModuleSpec.moduleItemSpecs(): List<MvModuleItemSpec> =
    this.moduleSpecBlock?.moduleItemSpecList.orEmpty()

fun MvModuleSpec.schemas(): List<MvSchema> = this.moduleSpecBlock?.schemaList.orEmpty()

fun MvModuleSpec.specFunctions(): List<MvSpecFunction> = this.moduleSpecBlock?.specFunctionList.orEmpty()

fun MvModuleSpec.specInlineFunctions(): List<MvSpecInlineFunction> =
    this.moduleItemSpecs().flatMap { it.specInlineFunctions() }

private val MODULE_SPECS_KEY: Key<CachedValue<List<MvModuleSpec>>> =
    Key.create("ALL_MODULE_SPECS_KEY")

fun MvModule.allModuleSpecs(): List<MvModuleSpec> = project.cacheManager.cache(this, MODULE_SPECS_KEY) {
    val specs: List<MvModuleSpec> = run {
        val moveProject = this.moveProject ?: return@run emptyList()
        val moduleName = this.name ?: return@run emptyList()

        val searchScope = moveProject.searchScope()
        // all `spec 0x1::m {}` for the current module
        val allModuleSpecs = MvModuleSpecIndex.getElementsByModuleName(this.project, moduleName, searchScope)
        if (allModuleSpecs.isEmpty()) return@run emptyList()

        allModuleSpecs
            .filter { moduleSpec ->
                val specModule = moduleSpec.moduleItem ?: return@filter false
                isModulesEqual(this, specModule)
            }
            .toList()
    }
    this.psiCacheResult(specs)
}

fun MvModule.allModuleSpecBlocks(): List<MvModuleSpecBlock> {
    return this.allModuleSpecs().mapNotNull { it.moduleSpecBlock }
}

fun isModulesEqual(left: MvModule, right: MvModule): Boolean {
    return left.getOriginalOrSelf() == right.getOriginalOrSelf()
}

fun MvModule.isPreload(): Boolean {
    return this.addressRef?.namedAddress?.text == "sui" && PRELOAD_SUI_MODULES.contains(this.name)
            || this.addressRef?.namedAddress?.text == "std" && PRELOAD_STD_MODULES.contains(this.name)
}

fun MvModule.useModuleItemList(): List<String> {
    val strings: MutableList<String> =
        (this.useStmtList.mapNotNull { it.useSpeck?.path?.text?.split("::")?.getOrNull(1) }
            .toMutableList() ?: mutableListOf()).toMutableList()
    val useGroupSpeckList = this.useStmtList.mapNotNull { it.useSpeck?.useGroup?.useSpeckList }.toMutableList()
    useGroupSpeckList.forEach() { useGroupSpeck ->
        strings += (useGroupSpeck.mapNotNull {
            if (it.path.text?.split("::")?.size!! > 1) {
                it.path.text?.split("::")?.get(0)
            } else {
                null
            }
        })
    }

    return strings
}

abstract class MvModuleMixin: MvStubbedNamedElementImpl<MvModuleStub>,
                              MvModule {

    constructor(node: ASTNode): super(node)

    constructor(stub: MvModuleStub, nodeType: IStubElementType<*, *>): super(stub, nodeType)

    override fun getIcon(flags: Int): Icon = MoveIcons.MODULE

    override fun getPresentation(): ItemPresentation? {
        val name = this.name ?: return null
        val moveProj = this.moveProject
        val locationString = this.address(moveProj)?.text() ?: ""
        return PresentationData(
            name,
            locationString,
            MoveIcons.MODULE,
            null
        )
    }

    override val qualName: ItemQualName?
        get() {
            // from stub
            val moduleName = this.name ?: return null
            val moveProject = this.moveProject
            // from stub
            val address = this.address(moveProject) ?: Address.Value("0x0")
            return ItemQualName(this, address, null, moduleName)
        }
}
