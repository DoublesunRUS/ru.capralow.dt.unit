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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;

import com._1c.g5.v8.dt.profiling.core.IProfilingResult;

/**
 * API for importing sessions. This interface is not intended to be implemented
 * by clients. Use {@link CoverageTools#getImporter()} to get an instance.
 */
public interface ISessionImporter {

	/**
	 * Sets the description for the imported session.
	 *
	 * @param description
	 *            textual description of the session
	 */
	void setDescription(String description);

	/**
	 * Sets the source for execution data.
	 *
	 * @param source
	 *            profiling results
	 */

	void setProfilingResult(IProfilingResult source);

	/**
	 * Sets the set of package fragment roots that should be considered for coverage
	 * analysis.
	 *
	 * @param scope
	 *            scope for analysis
	 */
	void setScope(Set<URI> scope);

	/**
	 * A call to this method triggers the actual import process.
	 *
	 * @param monitor
	 *            progress monitor
	 * @throws CoreException
	 *             if something goes wrong during export
	 */
	void importSession(IProgressMonitor monitor) throws CoreException;

}
