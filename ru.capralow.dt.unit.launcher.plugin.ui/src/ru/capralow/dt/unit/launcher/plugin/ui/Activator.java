package ru.capralow.dt.unit.launcher.plugin.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "ru.capralow.dt.unit.launcher.plugin.ui"; //$NON-NLS-1$

	private static Activator plugin;

	private BundleContext bundleContext;

	private Injector injector;

	public static Activator getDefault() {
		return plugin;
	}

	public static void log(IStatus status) {
		plugin.getLog().log(status);
	}

	public static void logError(Throwable throwable) {
		log(createErrorStatus(throwable.getMessage(), throwable));
	}

	public static IStatus createErrorStatus(String message, Throwable throwable) {
		return new Status(IStatus.ERROR, PLUGIN_ID, 0, message, throwable);
	}

	public static IStatus createWarningStatus(String message) {
		return new Status(IStatus.WARNING, PLUGIN_ID, 0, message, null);
	}

	public static IStatus createWarningStatus(final String message, Exception throwable) {
		return new Status(IStatus.WARNING, PLUGIN_ID, 0, message, throwable);
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		super.start(bundleContext);

		this.bundleContext = bundleContext;

		plugin = this;
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		plugin = null;
		super.stop(bundleContext);
	}

	protected BundleContext getContext() {
		return bundleContext;
	}

	public synchronized Injector getInjector() {
		if (injector == null)
			injector = createInjector();

		return injector;
	}

	private Injector createInjector() {
		try {
			return Guice.createInjector(new UnitLauncherExternalDependenciesModule(this));

		} catch (Exception e) {
			String msg = String.format("Не удалось создать injector для \"%1$s\"", getBundle().getSymbolicName());
			log(createErrorStatus(msg, e));
			return null;

		}
	}

}
