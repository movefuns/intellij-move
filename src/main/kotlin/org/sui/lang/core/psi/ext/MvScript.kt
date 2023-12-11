package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvConst
import org.sui.lang.core.psi.MvFunction
import org.sui.lang.core.psi.MvScript

fun MvScript.allFunctions(): List<MvFunction> = scriptBlock?.functionList.orEmpty()

fun MvScript.consts(): List<MvConst> = scriptBlock?.constList.orEmpty()

//fun MvScriptDef.builtinFunctions(): List<MvFunction> {
//    return listOf(
//        createBuiltinFunction("native fun assert(_: bool, err: u64);", project)
//    )
//}

//abstract class MvScriptMixin(node: ASTNode) : MvElementImpl(node),
//                                              MvScript
