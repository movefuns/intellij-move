package org.sui.ide.lineMarkers

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import org.sui.ide.MoveIcons
import org.sui.lang.core.psi.MvFunction
import org.sui.lang.core.psi.MvModule
import org.sui.lang.core.psi.ext.allModuleSpecs
import org.sui.lang.core.psi.ext.innerItemSpecs
import org.sui.lang.core.psi.ext.outerItemSpecs
import javax.swing.Icon

class ItemSpecsLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun getName() = "Specifications"
    override fun getIcon(): Icon = MoveIcons.ITEM_SPECS_GUTTER

    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val parent = element.parent
        val targets = when {
            parent is MvModule && parent.identifier == element -> parent.allModuleSpecs()
            parent is MvFunction && parent.identifier == element -> {
                parent.innerItemSpecs() + parent.outerItemSpecs()
            }
            else -> return
        }
        if (targets.isEmpty()) return

        val builder = NavigationGutterIconBuilder
            .create(MoveIcons.ITEM_SPECS_GUTTER)
            .setTargets(targets)
            .setTooltipText("Has specifications")
        result.add(builder.createLineMarkerInfo(element))
    }
}
