package org.sui.lang.core.psi.ext

import com.intellij.ide.projectView.PresentationData
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.stubs.IStubElementType
import org.sui.ide.MoveIcons
import org.sui.lang.core.psi.MvEnum
import org.sui.lang.core.psi.MvModule
import org.sui.lang.core.psi.generics
import org.sui.lang.core.psi.tyTypeParams
import org.sui.lang.core.stubs.MvEnumStub
//import org.sui.lang.core.stubs.MvEnumStub
import org.sui.lang.core.stubs.MvModuleStub
import org.sui.lang.core.stubs.MvStubbedNamedElementImpl
import org.sui.lang.core.types.ItemQualName
import org.sui.lang.core.types.ty.TyEnum
import javax.swing.Icon


val MvEnum.module: MvModule
    get() {
        val moduleStub = greenStub?.parentStub as? MvModuleStub
        if (moduleStub != null) {
            return moduleStub.psi
        }
        val moduleBlock = this.parent
        return moduleBlock.parent as MvModule
    }

abstract class MvEnumMixin : MvStubbedNamedElementImpl<MvEnumStub>, MvEnum {

    constructor(node: ASTNode) : super(node)

    constructor(stub: MvEnumStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override val qualName: ItemQualName?
        get() {
            val itemName = this.name ?: return null
            val moduleFQName = this.module.qualName ?: return null
            return ItemQualName(this, moduleFQName.address, moduleFQName.itemName, itemName)
        }

    override fun declaredType(msl: Boolean): TyEnum {
        return TyEnum(this, this.tyTypeParams, this.generics)
    }

    override fun getIcon(flags: Int): Icon = MoveIcons.ENUM

    override fun getPresentation(): ItemPresentation? {
        val enumName = this.name ?: return null
        return PresentationData(
            enumName,
            this.locationString(true),
            MoveIcons.ENUM,
            null
        )
    }
}