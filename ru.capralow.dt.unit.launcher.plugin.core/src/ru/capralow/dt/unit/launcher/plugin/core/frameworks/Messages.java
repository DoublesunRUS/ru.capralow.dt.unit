package ru.capralow.dt.unit.launcher.plugin.core.frameworks;

import org.eclipse.osgi.util.NLS;

final class Messages extends NLS {
	private static final String BUNDLE_NAME = "ru.capralow.dt.unit.launcher.plugin.core.frameworks.messages";

	public static String FrameworkUtils_Unable_to_delete_framework_file_0;

	public static String FrameworkUtils_Internal_class;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
