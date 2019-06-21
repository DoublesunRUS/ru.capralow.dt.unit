package ru.capralow.dt.internal.launching.ui.launchconfigurations.shortcuts;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.core.platform.IExtensionProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.debug.core.IDebugConfigurationAttributes;
import com._1c.g5.v8.dt.internal.launching.ui.launchconfigurations.shortcuts.AbstractRuntimeClientLaunchShortcut;
import com._1c.g5.v8.dt.launching.core.ILaunchConfigurationAttributes;
import com.google.common.base.Strings;

import ru.capralow.dt.unit.launcher.plugin.core.UnitTestLaunchConfigurationAttributes;
import ru.capralow.dt.unit.launcher.plugin.core.frameworks.FrameworkUtils;
import ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.TestFramework;

public class UnitTestLaunchShortcut extends AbstractRuntimeClientLaunchShortcut {

	private void setUnitTestSettings(ILaunchConfigurationWorkingCopy configuration, IV8Project v8Project) {
		configuration.setAttribute(UnitTestLaunchConfigurationAttributes.RUN_EXTENSION_TESTS, true);

		configuration.setAttribute(UnitTestLaunchConfigurationAttributes.EXTENSION_PROJECT_TO_TEST,
				v8Project.getProject().getName());

		Collection<TestFramework> frameworks = FrameworkUtils.getFrameworks();
		if (frameworks == null)
			throw new NullPointerException(Messages.UnitTestLaunchShortcut_No_frameworks_exception);

		String framework = ((TestFramework) frameworks.toArray()[0]).getName();

		String paramsFilePathName = FrameworkUtils.getConfigurationFilesPath(configuration);

		String startupOption = "StartFeaturePlayer;VBParams=$StartupOptionsPath$"; //$NON-NLS-1$
		startupOption = startupOption.replace("$StartupOptionsPath$", //$NON-NLS-1$
				paramsFilePathName + FrameworkUtils.PARAMS_FILE_NAME);

		configuration.setAttribute(UnitTestLaunchConfigurationAttributes.FRAMEWORK, framework);
		configuration.setAttribute(ILaunchConfigurationAttributes.STARTUP_OPTION, startupOption);
		configuration.setAttribute(UnitTestLaunchConfigurationAttributes.EXTERNAL_OBJECT_DUMP_PATH,
				paramsFilePathName + FrameworkUtils.FRAMEWORK_FILE_NAME);
	}

	@Override
	protected ILaunchConfigurationWorkingCopy createLaunchConfiguration(IProject project, EObject object, String mode)
			throws CoreException {
		IV8Project v8Project = v8projectManager.getProject(project);

		if (!(v8Project instanceof IExtensionProject))
			throw new NullPointerException(Messages.UnitTestLaunchShortcut_Wrong_project_exception);

		project = getAppropriateBaseProject(v8Project);

		ILaunchConfigurationWorkingCopy workingCopy = super.createLaunchConfiguration(project, object, mode);
		setUnitTestSettings(workingCopy, v8Project);

		return workingCopy;
	}

	protected String getLaunchConfigurationSelectionTitle() {
		return Messages.UnitTestLaunchShortcut_Title;
	}

	@Override
	protected String getLaunchConfigurationTypeId() {
		return "ru.capralow.dt.unit.launcher.plugin.ui.UnitTestLaunch"; //$NON-NLS-1$
	}

	@Override
	protected String getNameSuffix() {
		return Messages.UnitTestLaunchShortcut_Name_suffix;
	}

	@Override
	protected String getRuntimeComponentTypeId() {
		return "com._1c.g5.v8.dt.platform.services.core.componentTypes.ThinClient"; //$NON-NLS-1$
	}

	@Override
	protected boolean isValid(ILaunchConfiguration configuration, String mode) throws CoreException {
		boolean isProjectValid = configuration.getAttribute(IDebugConfigurationAttributes.PROJECT_NAME,
				(String) null) != null;
		String runtime = configuration.getAttribute(IDebugConfigurationAttributes.RUNTIME_INSTALLATION, (String) null);
		String launchUrl = configuration.getAttribute(ILaunchConfigurationAttributes.LAUNCH_URL, (String) null);
		boolean isRuntimeValid = runtime != null && resolvableRuntimeInstallationManager.deserialize(runtime) != null;
		boolean isLaunchUrlValid = !Strings.isNullOrEmpty(launchUrl);

		return isProjectValid && isRuntimeValid && (isInfobaseValid(configuration, mode) || isLaunchUrlValid);
	}
}