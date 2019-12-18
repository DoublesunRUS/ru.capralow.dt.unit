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
package ru.capralow.dt.coverage.internal.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;

import ru.capralow.dt.coverage.core.CoverageTools;
import ru.capralow.dt.coverage.core.ICoverageSession;

/**
 * Handler to re-launch the currently active coverage session.
 */
public class RelaunchSessionHandler extends AbstractSessionManagerHandler {

	public RelaunchSessionHandler() {
		super(CoverageTools.getSessionManager());
	}

	@Override
	public boolean isEnabled() {
		final ICoverageSession session = sessionManager.getActiveSession();
		return session != null && session.getLaunchConfiguration() != null;
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ICoverageSession session = sessionManager.getActiveSession();
		final ILaunchConfiguration config = session.getLaunchConfiguration();
		DebugUITools.launch(config, CoverageTools.LAUNCH_MODE);
		return null;
	}

}
