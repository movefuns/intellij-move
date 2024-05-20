package org.sui.openapiext

import com.intellij.openapi.diagnostic.Logger
import org.sui.openapiext.common.isUnitTestMode

fun Logger.debugInProduction(message: String) {
    if (isUnitTestMode) {
        this.warn(message)
    } else {
        this.debug(message)
    }
}
