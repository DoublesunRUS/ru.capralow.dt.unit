package ru.capralow.dt.internal.launching.ui.launchconfigurations.shortcuts;

import org.eclipse.osgi.util.NLS;

final class Messages extends NLS {
	private static final String BUNDLE_NAME = "ru.capralow.dt.internal.launching.ui.launchconfigurations.shortcuts.messages"; //$NON-NLS-1$

	public static String UnitTestLaunchShortcut_Title;
	public static String UnitTestLaunchShortcut_Name_suffix;

	public static String UnitTestLaunchShortcut_Wrong_project_exception;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
