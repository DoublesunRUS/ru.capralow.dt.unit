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

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import com._1c.g5.v8.dt.bsl.model.Module;

/**
 * The launch delegate for coverage configurations.
 */
public interface ICoverageLauncher extends ILaunchConfigurationDelegate2 {

	/**
	 * Determines all {@link IPackageFragmentRoot}s that are part of the given
	 * launch configuration.
	 *
	 * @param configuration
	 *            launch configuration to determine overall scope
	 *
	 * @return overall scope as set of {@link IPackageFragmentRoot} elements
	 *
	 * @throws CoreException
	 */
	public Set<Module> getOverallScope(ILaunchConfiguration configuration) throws CoreException;

}
