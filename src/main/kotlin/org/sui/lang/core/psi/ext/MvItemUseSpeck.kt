package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvItemUseSpeck

fun MvItemUseSpeck.names(): List<String> =
    this.useItemGroup?.names ?: listOfNotNull(this.useItem?.name)
