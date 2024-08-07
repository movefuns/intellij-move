package org.sui.lang.resolve

import org.intellij.lang.annotations.Language
import org.sui.lang.core.resolve.ref.MvReferenceElement
import org.sui.utils.tests.InlineFile
import org.sui.utils.tests.base.findElementInEditor
import org.sui.utils.tests.resolve.ResolveTestCase

class MultiResolveTest : ResolveTestCase() {
    fun `test struct literal shorthand`() = doTest(
        """
module 0x1::M {
    struct S { val: u8 }
    fun m() {
        let val = 1;
        S { val };
           //^
    }
}
    """
    )

    fun `test schema parameter shorthand`() = doTest(
        """
    module 0x1::M {
        spec module {
            let addr = @0x1;
            include MySchema { addr };
                                //^
        }
        
        spec schema MySchema {
            addr: address;
        }
    }    
    """
    )

    private fun doTest(@Language("Sui Move") code: String) {
        InlineFile(myFixture, code, "main.move")
        val element = myFixture.findElementInEditor<MvReferenceElement>()
        val ref = element.reference ?: error("Failed to get reference for `${element.text}`")
        val variants = ref.multiResolve(false)
        check(variants.size == 2) {
            "Expected 2 variants, got $variants"
        }
    }
}
