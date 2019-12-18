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
package ru.capralow.dt.coverage.core;

/**
 * Callback interface for changes of the session manager. This interface is
 * intended to be implemented by clients that want to get notifications.
 *
 * @see ISessionManager#addSessionListener(ISessionListener)
 * @see ISessionManager#removeSessionListener(ISessionListener)
 */
public interface ISessionListener {

	/**
	 * Called when a session has been added.
	 *
	 * @param addedSession
	 *            added session
	 */
	public void sessionAdded(ICoverageSession addedSession);

	/**
	 * Called when a session has been removed.
	 *
	 * @param removedSession
	 *            removes session
	 */
	public void sessionRemoved(ICoverageSession removedSession);

	/**
	 * Called when a new session has been activated or the last session has been
	 * removed. In this case <code>null</code> is passed as a parameter.
	 *
	 * @param session
	 *            activated session or <code>null</code>
	 */
	public void sessionActivated(ICoverageSession session);

}
