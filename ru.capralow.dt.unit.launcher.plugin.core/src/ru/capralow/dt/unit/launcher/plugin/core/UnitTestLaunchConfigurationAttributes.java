package ru.capralow.dt.unit.launcher.plugin.core;

public final class UnitTestLaunchConfigurationAttributes {
	public static final String RUN_EXTENSION_TESTS = "ru.capralow.dt.launching.core.ATTR_RUN_EXTENSION_TESTS"; //$NON-NLS-1$
	public static final String RUN_MODULE_TESTS = "ru.capralow.dt.launching.core.ATTR_RUN_MODULE_TESTS"; //$NON-NLS-1$
	public static final String RUN_TAG_TESTS = "ru.capralow.dt.launching.core.ATTR_RUN_TAG_TESTS"; //$NON-NLS-1$

	public static final String EXTENSION_PROJECT_TO_TEST = "ru.capralow.dt.launching.core.ATTR_EXTENSION_PROJECT_TO_TEST"; //$NON-NLS-1$
	public static final String EXTENSION_MODULE_TO_TEST = "ru.capralow.dt.launching.core.ATTR_EXTENSION_MODULE_TO_TEST"; //$NON-NLS-1$
	public static final String EXTENSION_TAG_TO_TEST = "ru.capralow.dt.launching.core.ATTR_EXTENSION_TAG_TO_TEST"; //$NON-NLS-1$

	public static final String EXTERNAL_OBJECT_DUMP_PATH = "ru.capralow.dt.launching.core.ATTR_EXTERNAL_OBJECT_DUMP_PATH"; //$NON-NLS-1$
	public static final String EXTERNAL_OBJECT_STARTUP_OPTIONS = "ru.capralow.dt.launching.core.ATTR_EXTERNAL_OBJECT_STARTUP_OPTIONS"; //$NON-NLS-1$

	private UnitTestLaunchConfigurationAttributes() {
		throw new IllegalStateException(Messages.UnitTestLaunchConfigurationAttributes_Internal_class);
	}
}
