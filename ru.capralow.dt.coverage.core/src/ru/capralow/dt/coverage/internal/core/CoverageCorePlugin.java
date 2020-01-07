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
 * Adapted by Alexander A. Kapralov
 *
 ******************************************************************************/
package ru.capralow.dt.coverage.internal.core;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

import com._1c.g5.wiring.InjectorAwareServiceRegistrator;
import com._1c.g5.wiring.ServiceInitialization;
import com.google.inject.Guice;
import com.google.inject.Injector;

import ru.capralow.dt.coverage.core.ICorePreferences;
import ru.capralow.dt.coverage.core.ISessionManager;

/**
 * Bundle activator for the 1Unit Coverage core.
 */
public class CoverageCorePlugin extends Plugin {

	public static final String ID = "ru.capralow.dt.coverage.core"; //$NON-NLS-1$

	private ICorePreferences preferences = ICorePreferences.DEFAULT;

	private static CoverageCorePlugin instance;

	private Injector injector;

	private ISessionManager sessionManager;

	private BslCoverageLoader coverageLoader;

	public synchronized Injector getInjector() {
		if (injector == null)
			injector = createInjector();

		return injector;
	}

	public static IStatus createErrorStatus(String message, Throwable throwable) {
		return new Status(IStatus.ERROR, ID, 0, message, throwable);
	}

	private Injector createInjector() {
		try {
			return Guice.createInjector(new ExternalDependenciesModule(getInstance()));

		} catch (Exception e) {
			String msg = MessageFormat.format(CoreMessages.Failed_to_create_injector_for_0,
					getInstance().getBundle().getSymbolicName());
			log(createErrorStatus(msg, e));
			return injector;

		}
	}

	public static void log(IStatus status) {
		getInstance().getLog().log(status);
	}

	private InjectorAwareServiceRegistrator registrator;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		sessionManager = new SessionManager();

		coverageLoader = new BslCoverageLoader(sessionManager);

		registrator = new InjectorAwareServiceRegistrator(context, this::getInjector);

		ServiceInitialization.schedule(() -> registrator.activateManagedService(CoverageManager.class));

		instance = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		instance = null;

		registrator.deactivateManagedServices(this);

		coverageLoader.dispose();
		coverageLoader = null;

		sessionManager = null;

		super.stop(context);
	}

	public static CoverageCorePlugin getInstance() {
		return instance;
	}

	public void setPreferences(ICorePreferences preferences) {
		this.preferences = preferences;
	}

	public ICorePreferences getPreferences() {
		return this.preferences;
	}

	public ISessionManager getSessionManager() {
		return sessionManager;
	}

	public BslCoverageLoader getBslCoverageLoader() {
		return coverageLoader;
	}
}
