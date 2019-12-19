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
package ru.capralow.dt.coverage.core.analysis;

/**
 * Callback interface implemented by clients that want to be informed, when the
 * current Java model coverage has changes.
 */
public interface IBslCoverageListener {

	/**
	 * Called when the current coverage data has changed.
	 */
	public void coverageChanged();

}
