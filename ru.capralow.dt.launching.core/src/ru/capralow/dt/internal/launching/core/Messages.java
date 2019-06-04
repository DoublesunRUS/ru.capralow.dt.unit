package ru.capralow.dt.internal.launching.core;

import org.eclipse.osgi.util.NLS;

final class Messages extends NLS {
	private static final String BUNDLE_NAME = "ru.capralow.dt.internal.launching.core.messages";

	public static String RuntimeUnitLauncherLaunchDelegate_Failed_to_get_framework_params_from_bundle_0_1;
	public static String RuntimeUnitLauncherLaunchDelegate_Failed_to_read_framework_params_0;
	public static String RuntimeUnitLauncherLaunchDelegate_Failed_to_save_framework_params_0;
	public static String RuntimeUnitLauncherLaunchDelegate_Incorrect_launch_configuration;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
