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

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;

import com._1c.g5.v8.dt.profiling.core.IProfilingResult;

/**
 * The session manager holds a list of currently available sessions. One of the
 * sessions in the list may be the active session, which is the one that is used
 * to attach coverage information to Java elements.
 *
 * This interface is not intended to be implemented or extended by clients.
 *
 * @see ru.capralow.dt.coverage.core.CoverageTools#getSessionManager()
 */
public interface ISessionManager {

	/**
	 * Adds the given session to this session manager. If the session is already
	 * part of this session manager the method has no effect. If the optional launch
	 * parameter is not <code>null</code> the key is internally assigned to this
	 * session for later removal.
	 *
	 * @param session
	 *            the new session
	 * @param activate
	 *            if <code>true</code> the session will also be activated
	 * @param launch
	 *            launch this session will be assigned to or <code>null</code>
	 */
	void addSession(ICoverageSession session, boolean activate, ILaunch launch);

	/**
	 * Merges the given sessions into a new one with the given name, then adds and
	 * activates the new session. All merged sessions will be removed from the
	 * session manager.
	 *
	 * @param sessions
	 *            sessions to merge
	 * @param description
	 *            for new session
	 * @param monitor
	 *            progress monitor
	 * @return the new session
	 */
	ICoverageSession mergeSessions(Collection<ICoverageSession> sessions, String description, IProgressMonitor monitor)
			throws CoreException;

	/**
	 * Removes the given session. If the session is not in included in this session
	 * manager this method has no effect. If the removed session was the active
	 * session, the most recently added session becomes active.
	 *
	 * @param session
	 *            session to remove
	 */
	void removeSession(ICoverageSession session);

	/**
	 * Removes all sessions that has been assigned to the given launch. If there is
	 * no session for the key this method has no effect. If the removed session was
	 * the active session, the most recently added session becomes active.
	 *
	 * @see #addSession(ICoverageSession, boolean, Object)
	 * @param launch
	 *            launch of the sessions to remove
	 */
	void removeSessionsFor(ILaunch launch);

	/**
	 * Removes all registered sessions.
	 */
	void removeAllSessions();

	/**
	 * Returns all sessions that have been registered with this session manager.
	 *
	 * @see #addSession(ICoverageSession, boolean, Object)
	 * @return list of registered session
	 */
	List<ICoverageSession> getSessions();

	/**
	 * Activates the given session. If the session is not in included in this
	 * session manager this method has no effect.
	 *
	 * @param session
	 *            session to activate
	 */
	void activateSession(ICoverageSession session);

	/**
	 * Returns the active session or <code>null</code> if there is no session.
	 *
	 * @return active session or <code>null</null>
	 */
	ICoverageSession getActiveSession();

	/**
	 * Triggers a reload of the active session. If there is no active session this
	 * method has no effect.
	 */
	void refreshActiveSession();

	/**
	 * Adds the given session listener unless it has been added before.
	 *
	 * @param listener
	 *            session listener to add
	 */
	void addSessionListener(ISessionListener listener);

	/**
	 * Removes the given session listener. If the listener has not been added before
	 * this method has no effect.
	 *
	 * @param listener
	 *            session listener to remove
	 */
	void removeSessionListener(ISessionListener listener);

	boolean profilingResultAnalyzed(IProfilingResult newProfilingResult);

}
