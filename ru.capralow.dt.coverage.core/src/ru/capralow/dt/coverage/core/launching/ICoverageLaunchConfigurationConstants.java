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
 ******************************************************************************/
package ru.capralow.dt.coverage.core.launching;

import ru.capralow.dt.coverage.internal.core.CoverageCorePlugin;

/**
 * Constants for coverage specific launch configuration entries.
 */
public interface ICoverageLaunchConfigurationConstants {

	/**
	 * List of Java element ids pointing to package fragment roots that form the
	 * scope of a coverage launch. If unspecified a default scope is calculated
	 * based on the launch type and preferences..
	 */
	public static final String ATTR_SCOPE_IDS = CoverageCorePlugin.ID + ".SCOPE_IDS"; //$NON-NLS-1$

}
