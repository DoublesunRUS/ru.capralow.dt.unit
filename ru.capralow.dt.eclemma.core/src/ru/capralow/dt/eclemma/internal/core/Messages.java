package ru.capralow.dt.eclemma.internal.core;

import org.eclipse.osgi.util.NLS;

final class Messages extends NLS {
	private static final String BUNDLE_NAME = "ru.capralow.dt.eclemma.internal.core.messages"; //$NON-NLS-1$

	public static String EclEmmaCorePlugin_Failed_to_create_injector_for_0;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
