package org.sui.lang.core.psi.ext

import org.sui.lang.core.psi.MvSchema
import org.sui.lang.core.psi.MvSchemaLit
import org.sui.lang.core.psi.MvSchemaLitField

val MvSchemaLit.schema: MvSchema? get() = this.path.reference?.resolveWithAliases() as? MvSchema

val MvSchemaLit.fields: List<MvSchemaLitField> get() = schemaFieldsBlock?.schemaLitFieldList.orEmpty()

val MvSchemaLit.fieldNames: List<String> get() = fields.map { it.referenceName }
