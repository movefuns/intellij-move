package org.sui.ide.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import org.sui.cli.Consts
import org.sui.ide.inspections.fixes.ChangeAddressNameFix
import org.sui.lang.core.psi.MvModule
import org.sui.lang.core.psi.MvModuleUseSpeck
import org.sui.lang.core.psi.MvVisitor
import org.sui.lang.core.types.Address
import org.sui.lang.core.types.address
import org.sui.lang.moveProject

class SuiAddressByValueImportInspection : MvLocalInspectionTool() {
    override fun buildMvVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): MvVisitor =
        object : MvVisitor() {
            override fun visitModuleUseSpeck(o: MvModuleUseSpeck) {
                val moduleRef = o.fqModuleRef ?: return
                // no error if unresolved by value (default)
                val module = moduleRef.reference?.resolve() as? MvModule ?: return

                val moveProj = moduleRef.moveProject ?: return

                val refAddress = moduleRef.addressRef.address(moveProj) ?: return
                if (refAddress !is Address.Named) return
                if (refAddress.addressLit(moveProj)?.canonical() == Consts.ADDR_PLACEHOLDER) return

                val modAddress = module.address(moveProj) ?: return
                if (modAddress !is Address.Named) return
                if (modAddress.addressLit(moveProj)?.canonical() == Consts.ADDR_PLACEHOLDER) return

                if (!Address.eq(refAddress, modAddress)) {
                    holder.registerProblem(
                        moduleRef,
                        "Module is declared with a different address `${modAddress.name}`",
                        ProblemHighlightType.WEAK_WARNING,
                        ChangeAddressNameFix(moduleRef, modAddress.name),
                    )
                }
            }
        }
}
