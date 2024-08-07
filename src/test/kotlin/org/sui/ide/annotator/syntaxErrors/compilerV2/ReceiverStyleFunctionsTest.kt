package org.sui.ide.annotator.syntaxErrors.compilerV2

import org.sui.ide.annotator.MvSyntaxErrorAnnotator
import org.sui.ide.inspections.fixes.CompilerV2Feat.RECEIVER_STYLE_FUNCTIONS
import org.sui.utils.tests.CompilerV2Features
import org.sui.utils.tests.annotation.AnnotatorTestCase

class ReceiverStyleFunctionsTest : AnnotatorTestCase(MvSyntaxErrorAnnotator::class) {
    @CompilerV2Features()
    fun `test cannot use receiver style functions in compiler v1`() = checkWarnings(
        """
        module 0x1::m {
            struct S { field: u8 }
            fun receiver(self: &S): u8 { self.field }
            fun call(s: S) {
                s.<error descr="receiver-style functions are not supported in Aptos Move V1">receiver()</error>;
            }
        }        
    """
    )

    @CompilerV2Features(RECEIVER_STYLE_FUNCTIONS)
    fun `test receiver style functions in compiler v2`() = checkWarnings(
        """
        module 0x1::m {
            struct S { field: u8 }
            fun receiver(self: &S): u8 { self.field }
            fun call(s: S) {
                s.receiver();
            }
        }        
    """
    )
}