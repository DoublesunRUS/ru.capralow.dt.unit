/*******************************************************************************
 * Copyright (c) 2006, 2019 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *		Marc R. Hoffmann - initial API and implementation
 *
 * Adapted by Alexander A. Kapralov
 *
 ******************************************************************************/

package ru.capralow.dt.coverage.core.launching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.core.platform.IExtensionProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;

import ru.capralow.dt.unit.launcher.plugin.core.frameworks.FrameworkUtils;

/**
 * Launcher for local Java applications.
 */
public class BslApplicationLauncher extends CoverageLauncher {

	@Inject
	IV8ProjectManager projectManager;

	public Set<Module> getOverallScope(ILaunchConfiguration configuration) throws CoreException {

		IProject project = FrameworkUtils.getConfigurationProject(configuration, projectManager);
		if (project == null)
			return Collections.emptySet();

		IV8Project v8Project = projectManager.getProject(project);

		Configuration v8Configuration = null;
		if (v8Project instanceof IExtensionProject)
			v8Configuration = ((IExtensionProject) v8Project).getConfiguration();

		if (v8Configuration == null)
			return Collections.emptySet();

		List<Module> modules = new ArrayList<>();

		for (CommonModule commonModule : v8Configuration.getCommonModules()) {
			modules.add(commonModule.getModule());
		}
		return modules.stream().collect(Collectors.toSet());
	}

}
