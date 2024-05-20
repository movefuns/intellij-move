package org.sui.ide.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import org.sui.lang.core.psi.MvModule
import org.sui.lang.core.psi.MvVisitor
import org.sui.lang.core.psi.ext.addressRef

class InvalidModuleDeclarationInspection : MvLocalInspectionTool() {
    override val isSyntaxOnly: Boolean get() = true

    override fun buildMvVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): MvVisitor {
        return object : MvVisitor() {
            override fun visitModule(mod: MvModule) {
                val identifier = mod.identifier ?: return
                if (mod.addressRef() == null) {
                    holder.registerProblem(
                        identifier,
                        "Invalid module declaration. The module does not have a specified address / address block.",
                        ProblemHighlightType.ERROR
                    )
                }
            }
        }
    }
}
