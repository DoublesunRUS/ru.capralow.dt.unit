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

import org.eclipse.debug.core.ILaunch;
import org.eclipse.emf.common.util.URI;

/**
 * Extension of the {@link ILaunch} interface to keep specific information for
 * coverage launches.
 */
public interface ICoverageLaunch extends ILaunch {

	/**
	 * Returns the collection of {@link URI} considered as the scope for this
	 * launch.
	 *
	 * @return package fragment roots for this launch
	 */
	public Set<URI> getScope();
}
