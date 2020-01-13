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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com._1c.g5.v8.dt.core.platform.IConfigurationProject;
import com._1c.g5.v8.dt.core.platform.IExtensionProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.debug.core.IDebugConfigurationAttributes;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;

/**
 * Launcher for local Bsl applications.
 */
public class BslApplicationLauncher extends CoverageLauncher {

	@Inject
	private IV8ProjectManager projectManager;

	@Override
	public Set<URI> getOverallScope(ILaunchConfiguration configuration) throws CoreException {

		String configurationProjectName = configuration.getAttribute(IDebugConfigurationAttributes.PROJECT_NAME,
				(String) null);

		IV8Project v8Project = projectManager.getProject(configurationProjectName);

		if (v8Project == null)
			return Collections.emptySet();

		Configuration v8Configuration = null;
		if (v8Project instanceof IConfigurationProject)
			v8Configuration = ((IConfigurationProject) v8Project).getConfiguration();
		else if (v8Project instanceof IExtensionProject)
			v8Configuration = ((IExtensionProject) v8Project).getConfiguration();

		if (v8Configuration == null)
			return Collections.emptySet();

		List<URI> modules = new ArrayList<>();

		for (CommonModule commonModule : v8Configuration.getCommonModules())
			modules.add((EcoreUtil.getURI(commonModule.getModule())));

		if (v8Project instanceof IConfigurationProject)
			for (IExtensionProject extensionProject : projectManager.getProjects(IExtensionProject.class)) {
				if (extensionProject.getParent().equals(v8Project)) {
					Configuration extensionConfiguration = extensionProject.getConfiguration();
					for (CommonModule commonModule : extensionConfiguration.getCommonModules()) {
						URI moduleURI = EcoreUtil.getURI(commonModule.getModule());
						modules.add(moduleURI);
					}
				}
			}

		return modules.stream().collect(Collectors.toSet());
	}

}
