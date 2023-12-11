package org.sui.lang.core.psi

val MvTypeAnnotationOwner.type: MvType? get() = this.typeAnnotation?.type

interface MvTypeAnnotationOwner : MvElement {

    val typeAnnotation: MvTypeAnnotation?
}
