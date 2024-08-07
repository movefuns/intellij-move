package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvFunctionParameter
import org.sui.utils.SignatureUtils

fun List<MvFunctionParameter>.joinToSignature(): String {
    val parameterPairs = this.map { Pair(it.bindingPat.name, it.typeAnnotation?.type?.text) }
    return SignatureUtils.joinParameters(parameterPairs)
    }
