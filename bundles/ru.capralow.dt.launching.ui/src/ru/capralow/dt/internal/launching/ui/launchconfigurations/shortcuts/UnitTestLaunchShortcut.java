/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.internal.launching.ui.launchconfigurations.shortcuts;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.core.platform.IExtensionProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.internal.launching.ui.launchconfigurations.shortcuts.AbstractRuntimeClientLaunchShortcut;
import com._1c.g5.v8.dt.launching.core.ILaunchConfigurationAttributes;
import com.google.common.base.Strings;

import ru.capralow.dt.unit.launcher.UnitTestLaunchConfigurationAttributes;
import ru.capralow.dt.unit.launcher.frameworks.FrameworkUtils;
import ru.capralow.dt.unit.launcher.frameworks.gson.FrameworkSettings;

public class UnitTestLaunchShortcut
    extends AbstractRuntimeClientLaunchShortcut
{

    private static void setUnitTestSettings(ILaunchConfigurationWorkingCopy configuration, IV8Project v8Project)
    {
        configuration.setAttribute(UnitTestLaunchConfigurationAttributes.RUN_EXTENSION_TESTS, true);

        configuration.setAttribute(UnitTestLaunchConfigurationAttributes.EXTENSION_PROJECT_TO_TEST,
            v8Project.getProject().getName());

        String paramsFilePathName = FrameworkUtils.getConfigurationFilesPath(configuration);

        FrameworkSettings frameworkSettings = FrameworkUtils.getFrameworkSettings();
        String startupOption = FrameworkUtils.getFrameworkStartupOptions(frameworkSettings, paramsFilePathName);

        configuration.setAttribute(UnitTestLaunchConfigurationAttributes.EXTERNAL_OBJECT_STARTUP_OPTIONS,
            startupOption);
        configuration.setAttribute(UnitTestLaunchConfigurationAttributes.EXTERNAL_OBJECT_DUMP_PATH,
            paramsFilePathName + FrameworkUtils.FRAMEWORK_FILE_NAME);
    }

    @Override
    protected ILaunchConfigurationWorkingCopy createLaunchConfiguration(IProject project, EObject object, String mode)
        throws CoreException
    {
        IV8Project v8Project = v8projectManager.getProject(project);

        if (!(v8Project instanceof IExtensionProject))
            throw new NullPointerException(Messages.UnitTestLaunchShortcut_Wrong_project_exception);

        IProject baseProject = getAppropriateBaseProject(v8Project);

        ILaunchConfigurationWorkingCopy workingCopy = super.createLaunchConfiguration(baseProject, object, mode);
        setUnitTestSettings(workingCopy, v8Project);

        return workingCopy;
    }

    @Override
    protected String getLaunchConfigurationSelectionTitle()
    {
        return Messages.UnitTestLaunchShortcut_Title;
    }

    @Override
    protected String getLaunchConfigurationTypeId()
    {
        return "ru.capralow.dt.unit.launcher.ui.UnitTestLaunch"; //$NON-NLS-1$
    }

    @Override
    protected String getNameSuffix()
    {
        return Messages.UnitTestLaunchShortcut_Name_suffix;
    }

    @Override
    protected String getRuntimeComponentTypeId()
    {
        return "com._1c.g5.v8.dt.platform.services.core.componentTypes.ThinClient"; //$NON-NLS-1$
    }

    @Override
    protected boolean isValid(ILaunchConfiguration configuration, String mode) throws CoreException
    {
        String launchUrl = configuration.getAttribute(ILaunchConfigurationAttributes.LAUNCH_URL, (String)null);
        boolean isLaunchUrlValid = !Strings.isNullOrEmpty(launchUrl);

        String extensionProjectToTest =
            configuration.getAttribute(UnitTestLaunchConfigurationAttributes.EXTENSION_PROJECT_TO_TEST, (String)null);
        Boolean isExtensionValid = !Strings.isNullOrEmpty(extensionProjectToTest);

        String externalObjectDumpPath =
            configuration.getAttribute(UnitTestLaunchConfigurationAttributes.EXTERNAL_OBJECT_DUMP_PATH, (String)null);
        String externalObjectStartupOptions = configuration
            .getAttribute(UnitTestLaunchConfigurationAttributes.EXTERNAL_OBJECT_STARTUP_OPTIONS, (String)null);
        Boolean isExternalObjectValid =
            !Strings.isNullOrEmpty(externalObjectDumpPath) && !Strings.isNullOrEmpty(externalObjectStartupOptions);

        return isExtensionValid && isExternalObjectValid && (isInfobaseValid(configuration, mode) || isLaunchUrlValid)
            && super.isValid(configuration, mode);
    }
}
