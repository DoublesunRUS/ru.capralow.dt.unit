package ru.capralow.dt.unit.launcher.plugin.core;

public final class UnitTestLaunchConfigurationAttributes {
	public static final String RUN_MODULE_TESTS = "ru.capralow.dt.launching.core.ATTR_RUN_MODULE_TESTS";
	public static final String RUN_EXTENSION_TESTS = "ru.capralow.dt.launching.core.ATTR_RUN_EXTENSION_TESTS";

	public static final String EXTENSION_PROJECT_TO_TEST = "ru.capralow.dt.launching.core.ATTR_EXTENSION_PROJECT_TO_TEST";
	public static final String EXTENSION_MODULE_TO_TEST = "ru.capralow.dt.launching.core.ATTR_EXTENSION_MODULE_TO_TEST";

	public static final String FRAMEWORK = "ru.capralow.dt.launching.core.ATTR_FRAMEWORK";

	private UnitTestLaunchConfigurationAttributes() {
		throw new IllegalStateException(Messages.UnitTestLaunchConfigurationAttributes_Internal_class);
	}
}
