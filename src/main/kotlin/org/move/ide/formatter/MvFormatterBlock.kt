package org.move.ide.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.formatter.common.AbstractBlock
import org.move.ide.formatter.impl.isDelimitedBlock
import org.move.ide.formatter.impl.isWhitespaceOrEmpty

class MvFormatterBlock(
    node: ASTNode,
    wrap: Wrap?,
    alignment: Alignment?,
    private val indent: Indent?
) : AbstractBlock(node, wrap, alignment) {
    override fun getSpacing(child1: Block?, child2: Block): Spacing? = null
    override fun isLeaf(): Boolean = node.firstChildNode == null

    override fun getIndent(): Indent? = indent

    override fun buildChildren(): List<Block> {
        return node.getChildren(null)
            .filter { !it.isWhitespaceOrEmpty() }
            .map { childNode: ASTNode ->
                MvFormatterBlock(
                    node = childNode,
                    alignment = null,
                    indent = computeIndent(childNode),
                    wrap = null
                )
            }
    }

    override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
        val indent = when {
            node.isDelimitedBlock -> Indent.getNormalIndent()
            else -> Indent.getNoneIndent()
        }
        return ChildAttributes(indent, null)
    }

    private fun computeIndent(child: ASTNode): Indent? {
        val parentType = node.elementType
        val parentPsi = node.psi
        val childType = child.elementType
        val childPsi = child.psi
        return when {
            else -> Indent.getNoneIndent()
        }
    }
}