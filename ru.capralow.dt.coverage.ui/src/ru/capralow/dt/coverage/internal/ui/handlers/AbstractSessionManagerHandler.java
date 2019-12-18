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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.HandlerEvent;

import ru.capralow.dt.coverage.core.ICoverageSession;
import ru.capralow.dt.coverage.core.ISessionListener;
import ru.capralow.dt.coverage.core.ISessionManager;

/**
 * Abstract base for handlers that need a {@link ISessionManager} instance.
 */
public abstract class AbstractSessionManagerHandler extends AbstractHandler implements ISessionListener {

	protected final ISessionManager sessionManager;

	protected AbstractSessionManagerHandler(ISessionManager sessionManager) {
		this.sessionManager = sessionManager;
		sessionManager.addSessionListener(this);
	}

	@Override
	public void dispose() {
		sessionManager.removeSessionListener(this);
	}

	public void sessionAdded(ICoverageSession addedSession) {
		fireEnabledChanged();
	}

	public void sessionRemoved(ICoverageSession removedSession) {
		fireEnabledChanged();
	}

	public void sessionActivated(ICoverageSession session) {
		fireEnabledChanged();
	}

	private void fireEnabledChanged() {
		fireHandlerChanged(new HandlerEvent(this, true, false));
	}

}
