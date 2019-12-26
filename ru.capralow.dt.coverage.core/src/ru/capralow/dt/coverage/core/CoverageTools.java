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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.jacoco.core.analysis.ICoverageNode;

import ru.capralow.dt.coverage.core.analysis.IBslCoverageListener;
import ru.capralow.dt.coverage.core.analysis.IBslModelCoverage;
import ru.capralow.dt.coverage.core.launching.ICoverageLaunch;
import ru.capralow.dt.coverage.internal.core.CoverageCorePlugin;
import ru.capralow.dt.coverage.internal.core.SessionExporter;
import ru.capralow.dt.coverage.internal.core.SessionImporter;

/**
 * For central access to the tools provided by the coverage core plug-in this
 * class offers several static methods.
 */
public final class CoverageTools {

	/**
	 * The launch mode used for coverage sessions.
	 */
	public static final String LAUNCH_MODE = "coverage"; //$NON-NLS-1$

	/**
	 * Returns the global session manager.
	 *
	 * @return global session manager
	 */
	public static ISessionManager getSessionManager() {
		return CoverageCorePlugin.getInstance().getSessionManager();
	}

	/**
	 * Convenience method that tries to adapt the given object to ICoverageNode,
	 * i.e. find coverage information from the active session.
	 *
	 * @param object
	 *            Object to adapt
	 * @return adapter or <code>null</code>
	 */
	public static ICoverageNode getCoverageInfo(Object object) {
		if (object instanceof IAdaptable) {
			return ((IAdaptable) object).getAdapter(ICoverageNode.class);

		} else {
			IAdapterManager manager = Platform.getAdapterManager();
			return manager.getAdapter(object, ICoverageNode.class);

		}
	}

	public static IBslModelCoverage getBslModelCoverage() {
		return CoverageCorePlugin.getInstance().getBslCoverageLoader().getBslModelCoverage();
	}

	public static void addBslCoverageListener(IBslCoverageListener l) {
		CoverageCorePlugin.getInstance().getBslCoverageLoader().addBslCoverageListener(l);
	}

	public static void removeBslCoverageListener(IBslCoverageListener l) {
		CoverageCorePlugin.getInstance().getBslCoverageLoader().removeBslCoverageListener(l);
	}

	public static ISessionExporter getExporter(ICoverageSession session) {
		return new SessionExporter(session);
	}

	public static ISessionImporter getImporter() {
		return new SessionImporter(getSessionManager());
	}

	/**
	 * Sets a {@link ICorePreferences} instance which will be used by the EclEmma
	 * core to query preference settings if required.
	 *
	 * @param preferences
	 *            callback object for preference settings
	 */
	public static void setPreferences(ICorePreferences preferences) {
		CoverageCorePlugin.getInstance().setPreferences(preferences);
	}

	/**
	 * Determines all current coverage launches which are running.
	 *
	 * @return list of running coverage launches
	 */
	public static List<ICoverageLaunch> getRunningCoverageLaunches() {
		final List<ICoverageLaunch> result = new ArrayList<>();
		for (final ILaunch launch : DebugPlugin.getDefault().getLaunchManager().getLaunches()) {
			if (launch instanceof ICoverageLaunch && !launch.isTerminated()) {
				result.add((ICoverageLaunch) launch);
			}
		}
		return result;
	}

	private CoverageTools() {
		// no instances
	}

}
