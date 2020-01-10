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

import org.jacoco.core.runtime.AgentOptions;

/**
 * Clients may implement this interface to customize the behavior of the EclEmma
 * core plug-in and pass a instance to
 * {@link CoverageTools#setPreferences(ICorePreferences)}. This interface
 * decouples the core e.g. from the UI preferences.
 */
public interface ICorePreferences {

	/**
	 * Default behavior if no customization is set.
	 */
	ICorePreferences DEFAULT = new ICorePreferences() {

		private AgentOptions agentDefaults = new AgentOptions();

		@Override
		public boolean getActivateNewSessions() {
			return true;
		}

		@Override
		public boolean getAutoRemoveSessions() {
			return false;
		}

		@Override
		public boolean getDefaultScopeSourceFoldersOnly() {
			return true;
		}

		@Override
		public boolean getDefaultScopeSameProjectOnly() {
			return false;
		}

		@Override
		public String getDefaultScopeFilter() {
			return "";//$NON-NLS-1$
		}

		@Override
		public String getAgentIncludes() {
			return agentDefaults.getIncludes();
		}

		@Override
		public String getAgentExcludes() {
			return agentDefaults.getExcludes();
		}

		@Override
		public String getAgentExclClassloader() {
			return agentDefaults.getExclClassloader();
		}

	};

	/**
	 * Determines whether new sessions should automatically be activated.
	 *
	 * @return <code>true</code>, if sessions should be activated
	 */
	boolean getActivateNewSessions();

	/**
	 * Determines whether sessions should automatically be removed when their
	 * respective launch is removed from the debug environment.
	 *
	 * @return <code>true</code>, if sessions should be removed
	 */
	boolean getAutoRemoveSessions();

	/**
	 * Specification of the default coverage scope behavior: Analyze source folders
	 * only.
	 *
	 * @return <code>true</code>, if source folders only should be analyzed by
	 *         default
	 */
	boolean getDefaultScopeSourceFoldersOnly();

	/**
	 * Specification of the default coverage scope behavior: Analyze code in the
	 * same project only. This filter works only for launch configuration types that
	 * have a reference to a project.
	 *
	 * @return <code>true</code>, if code in the same project should be analyzed
	 *         only
	 */
	boolean getDefaultScopeSameProjectOnly();

	/**
	 * Returns a comma separated list of match strings that specifies patterns for
	 * class path entries to be in coverage scope by default.
	 *
	 * @return List of match strings
	 */
	String getDefaultScopeFilter();

	/**
	 * Returns the wildcard expression for classes to include.
	 *
	 * @return wildcard expression for classes to include
	 * @see org.jacoco.core.runtime.WildcardMatcher
	 */
	String getAgentIncludes();

	/**
	 * Returns the wildcard expression for classes to exclude.
	 *
	 * @return wildcard expression for classes to exclude
	 * @see org.jacoco.core.runtime.WildcardMatcher
	 */
	String getAgentExcludes();

	/**
	 * Returns the wildcard expression for excluded class loaders.
	 *
	 * @return expression for excluded class loaders
	 * @see org.jacoco.core.runtime.WildcardMatcher
	 */
	String getAgentExclClassloader();

}
