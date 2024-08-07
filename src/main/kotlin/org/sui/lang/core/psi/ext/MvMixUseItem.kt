package org.sui.lang.core.psi.ext

//val MvMixUseItem.itemUseSpeck: MvMixUseSpeck
//    get() = ancestorStrict() ?: error("MvUseItem outside MvItemUseSpeck")
//
//val MvMixUseItem.annotationItem: MvElement
//    get() {
//        val parent = this.parent
//        if (parent is MvUseItemGroup && parent.useItemList.size != 1) return this
//        return useStmt
//    }
//
//val MvMixUseItem.useStmt: MvUseStmt
//    get() =
//        ancestorStrict() ?: error("always has MvUseStmt as ancestor")
//
//val MvMixUseItem.moduleName: String
//    get() = this.mixPathItem.moduleRef.referenceName.toString()

//class MvMixUseItemReferenceElement(
//    element: MvMixUseItem
//) : MvPolyVariantReferenceCached<MvMixUseItem>(element) {
//
//    override fun multiResolveInner(): List<MvNamedElement> {
////        val fqModuleRef = element.itemUseSpeck.mo
//        val module = element.moduleRef.reference?.resolve() as MvModule? ?: return emptyList()
//
//        val ns = setOf(
//            Namespace.TYPE,
//            Namespace.NAME,
//            Namespace.FUNCTION,
//            Namespace.SCHEMA,
//            Namespace.CONST
//        )
//        val vs = Visibility.visibilityScopesForElement(element.moduleRef)
//
//        // import has MAIN+VERIFY, and TEST if it or any of the parents has test
//        val useItemScopes = mutableSetOf(NamedItemScope.MAIN, NamedItemScope.VERIFY)
//
//        // gather scopes for all parents up to MvUseStmt
//        var scopedElement: MvElement? = element
//        while (scopedElement != null) {
//            useItemScopes.addAll(scopedElement.itemScopes)
//            scopedElement = scopedElement.parent as? MvElement
//        }
//
//        val contextScopeInfo =
//            ContextScopeInfo(
//                letStmtScope = LetStmtScope.EXPR_STMT,
//                refItemScopes = useItemScopes,
//            )
//        val name = if(element.name == null)"" else element.name!!
//        return resolveModuleItem(
//            module,
//            name,
//            ns,
//            vs,
//            contextScopeInfo
//        )
//    }
//
//}

//abstract class MvMixUseItemMixin(node: ASTNode) : MvNamedElementImpl(node),
//    MvMixUseItem {
//    override fun getName(): String? {
//        val name = super.getText()
//
//        if (name?.contains("::") == true) {
//            return name.split("::").last()
//        }
//        return name
//    }

//    override val referenceName:String? get() = this.text
//    override val referenceNameElement: PsiElement get() = identifier
//
//    override fun getReference() = MvMixUseItemReferenceElement(this)

//}
