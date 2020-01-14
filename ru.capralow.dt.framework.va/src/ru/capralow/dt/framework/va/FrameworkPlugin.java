package ru.capralow.dt.framework.va;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class FrameworkPlugin extends Plugin {
	public static final String ID = "ru.capralow.dt.framework.va"; //$NON-NLS-1$

	private static FrameworkPlugin instance;

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

	public static FrameworkPlugin getInstance() {
		return instance;
	}

	public static void log(IStatus status) {
		getInstance().getLog().log(status);
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		instance = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		instance = null;

		super.stop(context);
	}

}
