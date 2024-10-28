package org.sui.ide.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import org.sui.ide.inspections.fixes.RemoveAcquiresFix
import org.sui.ide.presentation.fullnameNoArgs
import org.sui.ide.presentation.declaringModule
import org.sui.lang.core.psi.*
import org.sui.lang.core.psi.ext.MvCallable
import org.sui.lang.core.types.infer.AcquireTypesOwnerVisitor
import org.sui.lang.core.types.infer.acquiresContext
import org.sui.lang.core.types.infer.inference
import org.sui.lang.core.types.infer.loweredType
import org.sui.lang.core.types.ty.TyUnknown
import org.sui.lang.moveProject


class MvUnusedAcquiresTypeInspection: MvLocalInspectionTool() {
    override fun buildMvVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): MvVisitor =
        object: MvVisitor() {
            override fun visitAcquiresType(o: MvAcquiresType) {
                val function = o.parent as? MvFunction ?: return
                val currentModule = function.module ?: return
                val acquiresContext = o.moveProject?.acquiresContext ?: return
                val inference = function.inference(false)

                val callAcquiresTypes = mutableSetOf<String>()
                val visitor = object: AcquireTypesOwnerVisitor() {
                    override fun visitAcquireTypesOwner(acqTypesOwner: MvAcquireTypesOwner) {
                        val types =
                            when (acqTypesOwner) {
                                is MvCallable -> acquiresContext.getCallTypes(acqTypesOwner, inference)
                                is MvIndexExpr -> acquiresContext.getIndexExprTypes(acqTypesOwner, inference)
                                else -> error("when is exhaustive")
                            }
                        callAcquiresTypes.addAll(types.map { it.fullnameNoArgs() })
                    }
                }
                visitor.visitElement(function)

                val unusedTypeIndices = mutableListOf<Int>()
                val visitedTypes = mutableSetOf<String>()
                for ((i, acqPathType) in function.acquiresPathTypes.withIndex()) {
                    val acqItemTy = acqPathType.loweredType(false)
                    if (acqItemTy is TyUnknown) continue

                    val tyItemModule = acqItemTy.declaringModule() ?: continue
                    if (tyItemModule != currentModule) {
                        unusedTypeIndices.add(i)
                        continue
                    }

                    // check for duplicates
                    val tyFullName = acqItemTy.fullnameNoArgs()
                    if (tyFullName in visitedTypes) {
                        unusedTypeIndices.add(i)
                        continue
                    }
                    visitedTypes.add(tyFullName)

                    if (tyFullName !in callAcquiresTypes) {
                        unusedTypeIndices.add(i)
                        continue
                    }
                }
                if (unusedTypeIndices.size == function.acquiresPathTypes.size) {
                    // register whole acquiresType
                    holder.registerUnusedAcquires(o)
                    return
                }
                for (idx in unusedTypeIndices) {
                    holder.registerUnusedAcquires(function.acquiresPathTypes[idx])
                }
            }
        }

    private fun ProblemsHolder.registerUnusedAcquires(ref: PsiElement) {
        this.registerProblem(
            ref,
            "Unused acquires clause",
            ProblemHighlightType.LIKE_UNUSED_SYMBOL,
            RemoveAcquiresFix(ref)
        )
    }
}
