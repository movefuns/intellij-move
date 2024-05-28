package org.sui.ide.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import org.sui.ide.inspections.fixes.AddAcquiresFix
import org.sui.ide.presentation.fullnameNoArgs
import org.sui.lang.core.psi.*
import org.sui.lang.core.psi.ext.isInline
import org.sui.lang.core.types.infer.acquiresContext
import org.sui.lang.core.types.infer.inference
import org.sui.lang.core.types.ty.TyStruct
import org.sui.lang.core.types.ty.TyTypeParameter
import org.sui.lang.moveProject

class MvMissingAcquiresInspection : MvLocalInspectionTool() {

    override val isSyntaxOnly: Boolean get() = true

    override fun buildMvVisitor(holder: ProblemsHolder, isOnTheFly: Boolean) =
        object : MvVisitor() {
            override fun visitCallExpr(callExpr: MvCallExpr) {
                val outerFunction = callExpr.containingFunction ?: return
                if (outerFunction.isInline) return

                val acquiresContext = callExpr.moveProject?.acquiresContext ?: return
                val inference = outerFunction.inference(false)

                val existingTypes = acquiresContext.getFunctionTypes(outerFunction)
                val existingTypeNames =
                    existingTypes.map { it.fullnameNoArgs() }.toSet()

                val callExprTypes = acquiresContext.getCallTypes(callExpr, inference)

                val currentModule = outerFunction.module ?: return
                val missingTypes =
                    callExprTypes.mapNotNull { acqTy ->
                        when (acqTy) {
                            is TyTypeParameter ->
                                acqTy.origin.takeIf { tyOrigin -> existingTypes.all { tyOrigin != it } }
                            is TyStruct -> {
                                val belongsToTheSameModule = acqTy.item.containingModule == currentModule
                                if (
                                    belongsToTheSameModule
                                    && acqTy.fullnameNoArgs() !in existingTypeNames
                                ) {
                                    acqTy.item
                                } else {
                                    null
                                }
                            }
                            else -> null
                        }
                    }

//                val itemTyVars = outerFunction.tyInfers
//                val missingItems = inference.getAcquiredTypes(callExpr, outerSubst = itemTyVars)
////                    .map { it.substituteOrUnknown(typeParameters) }
//                    .mapNotNull { ty ->
//                        when (ty) {
//                            is TyTypeParameter -> if (!declaredItems.any { it == ty.origin }) ty.origin else null
//                            is TyStruct -> {
//                                val notAcquired = ty.item.containingModule == currentModule
//                                        && !declaredItems.any { it == ty.item }
//                                if (notAcquired) ty.item else null
//                            }
//                            else -> null
//                        }
//                    }
                if (missingTypes.isNotEmpty()) {
                    val name = outerFunction.name ?: return
                    val missingNames = missingTypes.mapNotNull { it.name }
                    holder.registerProblem(
                        callExpr,
                        "Function '$name' is not marked as 'acquires ${missingNames.joinToString()}'",
                        ProblemHighlightType.GENERIC_ERROR,
                        AddAcquiresFix(outerFunction, missingNames)
                    )
                }
            }

//            override fun visitLetStmt(letStmt: MvLetStmt) {
//                if (!letStmt.hasMut()) {
//                    holder.registerProblem(
//                        letStmt,
//                        "The 'let' statement should use the 'mut' keyword",
//                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING
//                    )
//                }
//            }
        }
}
