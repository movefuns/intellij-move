package org.sui.lang.core.psi.ext

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import org.sui.lang.core.psi.MvModuleSpec
import org.sui.lang.core.stubs.MvModuleSpecStub
import org.sui.lang.core.stubs.MvStubbedElementImpl

abstract class MvModuleSpecMixin : MvStubbedElementImpl<MvModuleSpecStub>, MvModuleSpec {

    constructor(node: ASTNode) : super(node)

    constructor(stub: MvModuleSpecStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)
}
