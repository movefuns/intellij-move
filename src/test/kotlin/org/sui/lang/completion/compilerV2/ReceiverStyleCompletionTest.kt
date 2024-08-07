package org.sui.lang.completion.compilerV2

import org.sui.ide.inspections.fixes.CompilerV2Feat.RECEIVER_STYLE_FUNCTIONS
import org.sui.utils.tests.CompilerV2Features
import org.sui.utils.tests.completion.CompletionTestCase

@CompilerV2Features(RECEIVER_STYLE_FUNCTIONS)
class ReceiverStyleCompletionTest : CompletionTestCase() {
    @CompilerV2Features()
    fun `test no function completion if compiler v1`() = checkNoCompletion(
        """
        module 0x1::main {
            struct S { field: u8 }
            fun receiver(self: &S): u8 {}
            fun main(s: S) {
                s.rece/*caret*/
            }
        }        
    """
    )

    fun `test function completion`() = doSingleCompletion(
        """
        module 0x1::main {
            struct S { field: u8 }
            fun receiver(self: &S): u8 {}
            fun main(s: S) {
                s.rece/*caret*/
            }
        }        
    """, """
        module 0x1::main {
            struct S { field: u8 }
            fun receiver(self: &S): u8 {}
            fun main(s: S) {
                s.receiver()/*caret*/
            }
        }        
    """
    )

    fun `test function completion with assignment`() = doSingleCompletion(
        """
        module 0x1::main {
            struct S { field: u8 }
            fun receiver<Z>(self: &S): Z {}
            fun main(s: S) {
                let f: u8 = s.rece/*caret*/
            }
        }        
    """, """
        module 0x1::main {
            struct S { field: u8 }
            fun receiver<Z>(self: &S): Z {}
            fun main(s: S) {
                let f: u8 = s.receiver()/*caret*/
            }
        }        
    """
    )

    fun `test function completion from another module`() = doSingleCompletion(
        """
        module 0x1::m {
            struct S { field: u8 }
            public fun receiver(self: &S): u8 {}
        }
        module 0x1::main {
            use 0x1::m::S;
            fun main(s: S) {
                s.rece/*caret*/
            }
        }        
    """, """
        module 0x1::m {
            struct S { field: u8 }
            public fun receiver(self: &S): u8 {}
        }
        module 0x1::main {
            use 0x1::m::S;
            fun main(s: S) {
                s.receiver()/*caret*/
            }
        }        
    """
    )

    fun `test function completion type annotation required`() = doSingleCompletion(
        """
        module 0x1::main {
            struct S { field: u8 }
            fun receiver<Z>(self: &S): Z {}
            fun main(s: S) {
                s.rece/*caret*/;
            }
        }        
    """, """
        module 0x1::main {
            struct S { field: u8 }
            fun receiver<Z>(self: &S): Z {}
            fun main(s: S) {
                s.receiver::</*caret*/>();
            }
        }        
    """
    )

    fun `test function completion type annotation required with angle brackets present`() = doSingleCompletion(
        """
        module 0x1::main {
            struct S { field: u8 }
            fun receiver<Z>(self: &S): Z {}
            fun main(s: S) {
                s.rece/*caret*/::<>()
            }
        }        
    """, """
        module 0x1::main {
            struct S { field: u8 }
            fun receiver<Z>(self: &S): Z {}
            fun main(s: S) {
                s.receiver::</*caret*/>()
            }
        }        
    """
    )
}