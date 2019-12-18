/*******************************************************************************
 * Copyright (c) 2006, 2019 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *
 * Adapted by Alexander Kapralov
 *
 ******************************************************************************/
package ru.capralow.dt.coverage.core.launching;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com._1c.g5.v8.dt.bsl.model.Module;

import ru.capralow.dt.coverage.core.ScopeUtils;

/**
 * Laucher for the Eclipse runtime workbench.
 */
public class EDTLauncher extends CoverageLauncher {

	protected static final String EDT_NATURE = "com._1c.g5.v8.dt.core.V8ConfigurationNature"; //$NON-NLS-1$

	/*
	 * The overall scope are all plug-in projects in the workspace.
	 */
	public Set<Module> getOverallScope(ILaunchConfiguration configuration) throws CoreException {
		final IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
		final Set<Module> result = new HashSet<>();
		for (final IJavaProject project : model.getJavaProjects()) {
			if (project.getProject().hasNature(EDT_NATURE)) {
				result.addAll(Arrays.asList(project.getPackageFragmentRoots()));
			}
		}
		return ScopeUtils.filterUnsupportedEntries(result);
	}

}
