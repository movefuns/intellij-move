package org.sui.lang.index

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import org.sui.lang.core.psi.MvModuleSpec
import org.sui.lang.core.stubs.impl.MvFileStub
import org.sui.openapiext.checkCommitIsNotInProgress
import org.sui.openapiext.getElements

class MvModuleSpecIndex : StringStubIndexExtension<MvModuleSpec>() {
    override fun getVersion(): Int = MvFileStub.Type.stubVersion
    override fun getKey(): StubIndexKey<String, MvModuleSpec> = KEY

    companion object {
        val KEY: StubIndexKey<String, MvModuleSpec> =
            StubIndexKey.createIndexKey("org.sui.index.ModuleSpecIndex")

        fun getElementsByModuleName(
            project: Project,
            moduleName: String,
            scope: GlobalSearchScope = GlobalSearchScope.allScope(project)
        ): Collection<MvModuleSpec> {
            checkCommitIsNotInProgress(project)
            return getElements(KEY, moduleName, project, scope)
        }
    }
}
