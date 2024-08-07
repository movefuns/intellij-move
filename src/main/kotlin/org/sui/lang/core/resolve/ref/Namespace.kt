package org.sui.lang.core.resolve.ref

import com.intellij.psi.SmartPsiElementPointer
import org.sui.cli.MovePackage
import org.sui.cli.containingMovePackage
import org.sui.lang.core.psi.MvElement
import org.sui.lang.core.psi.MvModule
import org.sui.lang.core.psi.containingFunction
import org.sui.lang.core.psi.containingModule
import org.sui.lang.core.psi.ext.FunctionVisibility
import org.sui.lang.core.psi.ext.asSmartPointer
import org.sui.lang.core.psi.ext.visibility

sealed class Visibility {
    object Public : Visibility()
    object PublicScript : Visibility()
    class PublicFriend(val currentModule: SmartPsiElementPointer<MvModule>) : Visibility()
    data class PublicPackage(val originPackage: MovePackage) : Visibility()
    object Internal : Visibility()

    companion object {
        fun local(): Set<Visibility> = setOf(Public, Internal)
        fun none(): Set<Visibility> = setOf()

        fun visibilityScopesForElement(element: MvElement): Set<Visibility> {
            val vs = mutableSetOf<Visibility>(Public)
            val containingModule = element.containingModule
            if (containingModule != null) {
                vs.add(PublicFriend(containingModule.asSmartPointer()))
            }
            val containingFun = element.containingFunction
            if (containingModule == null
                || (containingFun?.visibility == FunctionVisibility.PUBLIC_SCRIPT)
            ) {
                vs.add(PublicScript)
            }
            val containingMovePackage = element.containingMovePackage
            if (containingMovePackage != null) {
                vs.add(PublicPackage(containingMovePackage))
            }
            return vs
        }
    }
}

enum class Namespace {
    NAME,
    FUNCTION,
    TYPE,
    SCHEMA,
    MODULE,
    CONST;

    companion object {
        fun all(): Set<Namespace> {
            return setOf(NAME, FUNCTION, TYPE, SCHEMA, MODULE, CONST)
        }

        fun none(): Set<Namespace> = setOf()
    }
}
