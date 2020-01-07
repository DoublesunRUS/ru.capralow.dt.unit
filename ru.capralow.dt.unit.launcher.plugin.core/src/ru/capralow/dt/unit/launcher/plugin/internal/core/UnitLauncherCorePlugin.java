package ru.capralow.dt.unit.launcher.plugin.internal.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class UnitLauncherCorePlugin extends AbstractUIPlugin {
	public static final String ID = "ru.capralow.dt.unit.launcher.plugin.ui"; //$NON-NLS-1$

	private static UnitLauncherCorePlugin instance;

	public static IStatus createErrorStatus(String message) {
		return new Status(IStatus.ERROR, ID, 0, message, (Throwable) null);
	}

	public static IStatus createErrorStatus(String message, int code) {
		return new Status(IStatus.ERROR, ID, code, message, (Throwable) null);
	}

	public static IStatus createErrorStatus(String message, int code, Throwable throwable) {
		return new Status(IStatus.ERROR, ID, code, message, throwable);
	}

	public static IStatus createErrorStatus(String message, Throwable throwable) {
		return new Status(IStatus.ERROR, ID, 0, message, throwable);
	}

	public static UnitLauncherCorePlugin getInstance() {
		return instance;
	}

	public static void log(IStatus status) {
		getInstance().getLog().log(status);
	}

	@Override
	public void start(BundleContext сontext) throws Exception {
		super.start(сontext);

		instance = this;
	}

	@Override
	public void stop(BundleContext сontext) throws Exception {
		instance = null;

		super.stop(сontext);
	}

}
