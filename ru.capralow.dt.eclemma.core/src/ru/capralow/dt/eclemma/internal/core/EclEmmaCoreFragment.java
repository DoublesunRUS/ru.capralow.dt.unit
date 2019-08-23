package ru.capralow.dt.eclemma.internal.core;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.eclemma.internal.core.EclEmmaCorePlugin;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class EclEmmaCoreFragment {
	public static final String ID = "ru.capralow.dt.eclemma.core"; //$NON-NLS-1$

	private static Injector injector;

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

	public static EclEmmaCorePlugin getDefault() {
		return EclEmmaCorePlugin.getInstance();
	}

	public static synchronized Injector getInjector() {
		if (injector == null)
			injector = createInjector();

		return injector;
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	private static Injector createInjector() {
		try {
			return Guice.createInjector(new ExternalDependenciesModule(getDefault()));

		} catch (Exception e) {
			String msg = MessageFormat.format(Messages.EclEmmaCorePlugin_Failed_to_create_injector_for_0,
					getDefault().getBundle().getSymbolicName());
			log(createErrorStatus(msg, e));
			return null;

		}
	}

}
