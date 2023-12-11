/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.sui.ide.newProject

import com.intellij.ide.util.projectWizard.AbstractNewProjectStep
import com.intellij.ide.util.projectWizard.ProjectSettingsStepBase
import com.intellij.platform.DirectoryProjectGenerator

class SuiProjectConfigStep(generator: DirectoryProjectGenerator<SuiProjectConfig>) :
    ProjectSettingsStepBase<SuiProjectConfig>(
        generator,
        AbstractNewProjectStep.AbstractCallback()
    )
