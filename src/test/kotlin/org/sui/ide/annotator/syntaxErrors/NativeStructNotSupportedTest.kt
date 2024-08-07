package org.sui.ide.annotator.syntaxErrors

import org.sui.ide.annotator.MvSyntaxErrorAnnotator
import org.sui.utils.tests.annotation.AnnotatorTestCase

class NativeStructNotSupportedTest : AnnotatorTestCase(MvSyntaxErrorAnnotator::class) {
    fun `test native struct is not supported by the vm`() = checkWarnings(
        """
        module 0x1::m {
            <error descr="Native structs aren't supported by the Move VM anymore">native struct</error> S;
        }        
    """
    )
}