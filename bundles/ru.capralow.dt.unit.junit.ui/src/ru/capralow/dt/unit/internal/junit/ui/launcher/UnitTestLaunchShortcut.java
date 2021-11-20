/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui.launcher;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.core.platform.IConfigurationProject;
import com._1c.g5.v8.dt.core.platform.IDependentProject;
import com._1c.g5.v8.dt.core.platform.IExtensionProject;
import com._1c.g5.v8.dt.core.platform.IExternalObjectProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.debug.core.IDebugConfigurationAttributes;
import com._1c.g5.v8.dt.platform.services.core.runtimes.execution.IRuntimeComponentTypes;
import com.google.common.base.Strings;

import ru.capralow.dt.unit.internal.junit.launcher.JUnitLaunchConfigurationConstants;
import ru.capralow.dt.unit.internal.junit.ui.JUnitUiPlugin;

/**
 * @author Aleksandr Kapralov
 *
 */
public class UnitTestLaunchShortcut
    extends AbstractUnitTestLaunchShortcut
{

    private static void setUnitTestSettings(ILaunchConfigurationWorkingCopy configuration, IV8Project v8Project)
    {
        configuration.setAttribute(JUnitLaunchConfigurationConstants.RUN_EXTENSION_TESTS, true);

        configuration.setAttribute(JUnitLaunchConfigurationConstants.EXTENSION_PROJECT_TO_TEST,
            v8Project.getProject().getName());
    }

    @Override
    protected ILaunchConfigurationWorkingCopy createLaunchConfiguration(IProject project, EObject object, String mode)
        throws CoreException
    {
        IV8Project v8Project = v8projectManager.getProject(project);

        if (!(v8Project instanceof IExtensionProject))
        {
            throw new NullPointerException(Messages.UnitTestLaunchShortcut_Wrong_project_exception);
        }

        IProject baseProject = getAppropriateBaseProject(v8Project);

        ILaunchConfigurationWorkingCopy workingCopy = super.createLaunchConfiguration(baseProject, object, mode);
        setUnitTestSettings(workingCopy, v8Project);

        return workingCopy;
    }

    @Override
    protected IProject getAppropriateBaseProject(IV8Project v8project)
    {
        Collection projects;
        IProject baseProject;
        if (v8project instanceof IConfigurationProject)
        {
            return v8project.getProject();
        }
        if (v8project instanceof IDependentProject
            && (baseProject = ((IDependentProject)v8project).getParentProject()) != null)
        {
            return baseProject;
        }
        if (v8project instanceof IExternalObjectProject
            && (projects = this.v8projectManager.getProjects(IConfigurationProject.class)).size() == 1)
        {
            return ((IConfigurationProject)projects.iterator().next()).getProject();
        }
        return v8project.getProject();
    }

    @Override
    protected String getLaunchConfigurationSelectionTitle()
    {
        return Messages.UnitTestLaunchShortcut_Title;
    }

    @Override
    protected String getLaunchConfigurationTypeId()
    {
        return JUnitUiPlugin.ID + ".UnitTestLaunch"; //$NON-NLS-1$
    }

    @Override
    protected String getNameSuffix()
    {
        return Messages.UnitTestLaunchShortcut_Name_suffix;
    }

    @Override
    protected String getRuntimeComponentTypeId()
    {
        return IRuntimeComponentTypes.THIN_CLIENT;
    }

    @Override
    protected boolean isValid(ILaunchConfiguration configuration, String mode) throws CoreException
    {
        String extensionProjectToTest =
            configuration.getAttribute(JUnitLaunchConfigurationConstants.EXTENSION_PROJECT_TO_TEST, (String)null);
        final boolean isExtensionValid = !Strings.isNullOrEmpty(extensionProjectToTest);

        final boolean runtimeUseAuto =
            configuration.getAttribute(IDebugConfigurationAttributes.RUNTIME_INSTALLATION_USE_AUTO, false);
        final String runtime =
            configuration.getAttribute(IDebugConfigurationAttributes.RUNTIME_INSTALLATION, (String)null);
        final boolean isRuntimeValid =
            runtimeUseAuto || runtime != null && this.resolvableRuntimeInstallationManager.deserialize(runtime) != null;

//        String externalObjectDumpPath =
//            configuration.getAttribute(JUnitLaunchConfigurationConstants.EXTERNAL_OBJECT_DUMP_PATH, (String)null);
//        String externalObjectStartupOptions =
//            configuration.getAttribute(JUnitLaunchConfigurationConstants.EXTERNAL_OBJECT_STARTUP_OPTIONS,
        //(String)null);
//        Boolean isExternalObjectValid =
//            !Strings.isNullOrEmpty(externalObjectDumpPath) && !Strings.isNullOrEmpty(externalObjectStartupOptions);

        return isExtensionValid && isRuntimeValid && isApplicationValid(configuration, mode)
            && super.isValid(configuration, mode);
    }
}
