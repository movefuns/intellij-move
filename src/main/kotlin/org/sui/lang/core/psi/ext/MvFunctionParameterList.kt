package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvFunctionParameterList
import org.sui.utils.SignatureUtils

val MvFunctionParameterList.parametersText: String
    get() {
        return SignatureUtils.joinParameters(this.functionParameterList.map {
            Pair(it.bindingPat.name, it.typeAnnotation?.type?.text)
        })
    }
