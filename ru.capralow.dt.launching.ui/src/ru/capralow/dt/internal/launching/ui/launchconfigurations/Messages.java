package ru.capralow.dt.internal.launching.ui.launchconfigurations;

import org.eclipse.osgi.util.NLS;

final class Messages extends NLS {
	private static final String BUNDLE_NAME = "ru.capralow.dt.internal.launching.ui.launchconfigurations.messages";

	public static String UnitTestLaunchTab_RunModuleTests;
	public static String UnitTestLaunchTab_ExtensionModule_to_Test;
	public static String UnitTestLaunchTab_RunExtensionTests;
	public static String UnitTestLaunchTab_ExtensionProject_to_Test;

	public static String UnitTestLaunchTab_Framework;

	public static String UnitTestLaunchTab_WrongProjectClass;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
