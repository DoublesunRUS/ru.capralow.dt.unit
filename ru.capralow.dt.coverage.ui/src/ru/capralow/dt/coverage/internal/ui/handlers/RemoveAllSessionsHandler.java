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

import ru.capralow.dt.coverage.core.CoverageTools;

/**
 * Handler to remove all coverage sessions.
 */
public class RemoveAllSessionsHandler extends AbstractSessionManagerHandler {

	public RemoveAllSessionsHandler() {
		super(CoverageTools.getSessionManager());
	}

	@Override
	public boolean isEnabled() {
		return !sessionManager.getSessions().isEmpty();
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		sessionManager.removeAllSessions();
		return null;
	}

}
