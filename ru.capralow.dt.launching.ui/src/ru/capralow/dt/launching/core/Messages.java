package ru.capralow.dt.launching.core;

import org.eclipse.osgi.util.NLS;

final class Messages extends NLS {
	private static final String BUNDLE_NAME = "ru.capralow.dt.launching.core.messages";

	public static String InternalClass_Warning;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
