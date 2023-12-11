package org.sui.lang.core.stubs

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.tree.IStubFileElementType
import org.sui.lang.MoveLanguage
import org.sui.lang.core.psi.MvElement

abstract class MvStubElementType<StubT : StubElement<*>, PsiT : MvElement>(
    debugName: String,
) : IStubElementType<StubT, PsiT>(debugName, MoveLanguage) {

    final override fun getExternalId(): String = "move.${super.toString()}"

    override fun indexStub(stub: StubT, sink: IndexSink) {}
}

fun createStubIfParentIsStub(node: ASTNode): Boolean {
    val parent = node.treeParent
    val parentType = parent.elementType
    return (parentType is IStubElementType<*, *> && parentType.shouldCreateStub(parent)) ||
            parentType is IStubFileElementType<*>
}
