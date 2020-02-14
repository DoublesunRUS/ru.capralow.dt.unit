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
package ru.capralow.dt.coverage.core;

import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.emf.common.util.URI;

import com._1c.g5.v8.dt.profiling.core.IProfilingResult;

/**
 * A coverage session is the result of a coverage run (or multiple merged runs)
 * or coverage data imported from an external source. It is an immutable
 * container for all data necessary to
 *
 * <ul>
 * <li>provide coverage highlighting in Java editors,</li>
 * <li>populate the coverage view and</li>
 * <li>export coverage reports.</li>
 * </ul>
 *
 * This interface is not intended to be implemented by clients.
 *
 * @see CoverageTools#createCoverageSession(String, IClassFiles[],
 *      org.eclipse.core.runtime.IPath[], ILaunchConfiguration)
 */
public interface ICoverageSession extends IAdaptable, IExecutionDataSource {

	/**
	 * Returns a readable description for this coverage session.
	 *
	 * @return readable description
	 */
	String getDescription();

	/**
	 * Returns the set of package fragment roots defining the scope of this session.
	 *
	 * @return session scope as set of {@link IPackageFragmentRoot}
	 */
	Set<URI> getScope();

	/**
	 * If this session was the result of a Eclipse launch this method returns the
	 * respective launch configuration. Otherwise <code>null</code> is returned.
	 *
	 * @return launch configuration or <code>null</code>
	 */
	ILaunchConfiguration getLaunchConfiguration();

	IProfilingResult getProfilingResult();

}
