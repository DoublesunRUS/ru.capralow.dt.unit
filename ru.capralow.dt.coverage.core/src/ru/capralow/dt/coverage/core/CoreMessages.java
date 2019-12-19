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

import org.eclipse.osgi.util.NLS;

/**
 * Text messages for the core plug-in.
 */
public class CoreMessages extends NLS {

	private static final String BUNDLE_NAME = "ru.capralow.dt.coverage.core.coremessages";//$NON-NLS-1$

	public static String Internal_class;

	static {
		NLS.initializeMessages(BUNDLE_NAME, CoreMessages.class);
	}

}
