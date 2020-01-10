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
package ru.capralow.dt.coverage.internal.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import ru.capralow.dt.coverage.core.ICorePreferences;

/**
 * Constants and initializer for the preference store.
 */
public class UIPreferences extends AbstractPreferenceInitializer {

	public static final String PREF_SHOW_COVERAGE_VIEW = CoverageUIPlugin.ID + ".show_coverage_view"; //$NON-NLS-1$

	public static final String PREF_RESET_ON_DUMP = CoverageUIPlugin.ID + ".reset_on_dump"; //$NON-NLS-1$

	public static final String PREF_ACTICATE_NEW_SESSIONS = CoverageUIPlugin.ID + ".activate_new_sessions"; //$NON-NLS-1$

	public static final String PREF_DEFAULT_SCOPE_SOURCE_FOLDERS_ONLY = CoverageUIPlugin.ID
			+ ".default_scope_source_folders_only"; //$NON-NLS-1$

	public static final String PREF_DEFAULT_SCOPE_SAME_PROJECT_ONLY = CoverageUIPlugin.ID
			+ ".default_scope_same_project_only"; //$NON-NLS-1$

	public static final String PREF_DEFAULT_SCOPE_FILTER = CoverageUIPlugin.ID + ".default_scope_filter"; //$NON-NLS-1$

	public static final String PREF_AUTO_REMOVE_SESSIONS = CoverageUIPlugin.ID + ".auto_remove_sessions"; //$NON-NLS-1$

	public static final String PREF_AGENT_INCLUDES = CoverageUIPlugin.ID + ".agent_includes"; //$NON-NLS-1$

	public static final String PREF_AGENT_EXCLUDES = CoverageUIPlugin.ID + ".agent_excludes"; //$NON-NLS-1$

	public static final String PREF_AGENT_EXCLCLASSLOADER = CoverageUIPlugin.ID + ".agent_exclclassloader"; //$NON-NLS-1$

	public static final ICorePreferences CORE_PREFERENCES = new ICorePreferences() {
		@Override
		public boolean getActivateNewSessions() {
			return getPreferenceStore().getBoolean(PREF_ACTICATE_NEW_SESSIONS);
		}

		@Override
		public boolean getAutoRemoveSessions() {
			return getPreferenceStore().getBoolean(PREF_AUTO_REMOVE_SESSIONS);
		}

		@Override
		public boolean getDefaultScopeSourceFoldersOnly() {
			return getPreferenceStore().getBoolean(PREF_DEFAULT_SCOPE_SOURCE_FOLDERS_ONLY);
		}

		@Override
		public boolean getDefaultScopeSameProjectOnly() {
			return getPreferenceStore().getBoolean(PREF_DEFAULT_SCOPE_SAME_PROJECT_ONLY);
		}

		@Override
		public String getDefaultScopeFilter() {
			return getPreferenceStore().getString(PREF_DEFAULT_SCOPE_FILTER);
		}

		@Override
		public String getAgentIncludes() {
			return getPreferenceStore().getString(PREF_AGENT_INCLUDES);
		}

		@Override
		public String getAgentExcludes() {
			return getPreferenceStore().getString(PREF_AGENT_EXCLUDES);
		}

		@Override
		public String getAgentExclClassloader() {
			return getPreferenceStore().getString(PREF_AGENT_EXCLCLASSLOADER);
		}
	};

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore pref = getPreferenceStore();
		pref.setDefault(PREF_SHOW_COVERAGE_VIEW, true);
		pref.setDefault(PREF_RESET_ON_DUMP, false);
		pref.setDefault(PREF_ACTICATE_NEW_SESSIONS, ICorePreferences.DEFAULT.getActivateNewSessions());
		pref.setDefault(PREF_AUTO_REMOVE_SESSIONS, ICorePreferences.DEFAULT.getAutoRemoveSessions());
		pref.setDefault(PREF_DEFAULT_SCOPE_SOURCE_FOLDERS_ONLY,
				ICorePreferences.DEFAULT.getDefaultScopeSourceFoldersOnly());
		pref.setDefault(PREF_DEFAULT_SCOPE_SAME_PROJECT_ONLY,
				ICorePreferences.DEFAULT.getDefaultScopeSameProjectOnly());
		pref.setDefault(PREF_DEFAULT_SCOPE_FILTER, ICorePreferences.DEFAULT.getDefaultScopeFilter());
		pref.setDefault(PREF_AGENT_INCLUDES, ICorePreferences.DEFAULT.getAgentIncludes());
		pref.setDefault(PREF_AGENT_EXCLUDES, ICorePreferences.DEFAULT.getAgentExcludes());
		pref.setDefault(PREF_AGENT_EXCLCLASSLOADER, ICorePreferences.DEFAULT.getAgentExclClassloader());
	}

	private static IPreferenceStore getPreferenceStore() {
		return CoverageUIPlugin.getInstance().getPreferenceStore();
	}

}
