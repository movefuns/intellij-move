package org.sui.ide.utils.imports

import org.sui.lang.core.psi.MvQualNamedElement
import org.sui.lang.core.types.ItemQualName

data class ImportCandidate(val element: MvQualNamedElement, val qualName: ItemQualName)
