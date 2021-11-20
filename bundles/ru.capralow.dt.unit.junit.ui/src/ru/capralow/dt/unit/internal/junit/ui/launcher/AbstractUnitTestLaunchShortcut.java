/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui.launcher;

import java.util.Collection;
import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IConfigurationProject;
import com._1c.g5.v8.dt.core.platform.IDependentProject;
import com._1c.g5.v8.dt.core.platform.IExternalObjectProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.debug.core.IDebugConfigurationAttributes;
import com._1c.g5.v8.dt.debug.ui.launchconfigurations.shortcuts.AbstractLaunchShortcut;
import com._1c.g5.v8.dt.launching.core.ILaunchConfigurationAttributes;
import com._1c.g5.v8.dt.launching.core.ILaunchConfigurationTypes;
import com._1c.g5.v8.dt.launching.core.launchconfigurations.ClientTypeSelectionSupport;
import com._1c.g5.v8.dt.launching.core.launchconfigurations.ExternalObjectHelper;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com._1c.g5.v8.dt.platform.services.core.ExternalObjectExtractor;
import com._1c.g5.v8.dt.platform.services.core.runtimes.environments.IResolvableRuntimeInstallation;
import com._1c.g5.v8.dt.platform.services.core.runtimes.environments.MatchingRuntimeNotFound;
import com._1c.g5.v8.dt.platform.services.core.runtimes.execution.IRuntimeComponentManager;
import com._1c.g5.v8.dt.platform.services.model.InfobaseReference;
import com._1c.g5.v8.dt.platform.services.model.RuntimeInstallation;
import com.e1c.g5.dt.applications.IApplication;
import com.e1c.g5.dt.applications.infobases.IInfobaseApplication;
import com.google.common.base.Strings;
import com.google.inject.Inject;

import ru.capralow.dt.unit.internal.junit.ui.JUnitUiPlugin;

public abstract class AbstractUnitTestLaunchShortcut
    extends AbstractLaunchShortcut
{
    @Inject
    private ExternalObjectExtractor externalObjectExtractor;
    @Inject
    private IRuntimeComponentManager runtimeComponentManager;

    private boolean matchObjects(final EObject o1, final EObject o2)
    {
        if (o1 == o2)
        {
            return true;
        }
        if (o1 == null || o2 == null)
        {
            return false;
        }
        if (o1.getClass() != o2.getClass())
        {
            return false;
        }
        if (o1 instanceof IBmObject)
        {
            return o1.equals(o2);
        }
        return EcoreUtil.getURI(o1).equals(EcoreUtil.getURI(o2));
    }

    @Override
    protected ILaunchConfigurationWorkingCopy createLaunchConfiguration(IProject project, final EObject object,
        final String mode) throws CoreException
    {
        final IV8Project v8project = v8projectManager.getProject(project);
        if (v8project instanceof IDependentProject)
        {
            project = getAppropriateBaseProject(v8project);
        }
        final ILaunchConfigurationWorkingCopy workingCopy = super.createLaunchConfiguration(project, object, mode);
        if (!(v8project instanceof IExternalObjectProject))
        {
            return workingCopy;
        }
        final IExternalObjectProject externalObjectProject = (IExternalObjectProject)v8project;
        if (object != null)
        {
            final MdObject externalObject = externalObjectExtractor.getExternalObject(object);
            if (externalObject != null)
            {
                if (JUnitUiPlugin.availableForLaunch(externalObject))
                {
                    setExternalObjectSettings(workingCopy, externalObjectProject.getProject(), externalObject);
                }
                return workingCopy;
            }
        }
        final MdObject externalObject = getExternalObject(externalObjectProject);
        if (externalObject != null)
        {
            setExternalObjectSettings(workingCopy, externalObjectProject.getProject(), externalObject);
            return workingCopy;
        }
        return null;
    }

    protected IProject getAppropriateBaseProject(final IV8Project v8project)
    {
        if (v8project instanceof IConfigurationProject)
        {
            return v8project.getProject();
        }
        if (v8project instanceof IDependentProject)
        {
            final IProject baseProject = ((IDependentProject)v8project).getParentProject();
            if (baseProject != null)
            {
                return baseProject;
            }
        }
        if (v8project instanceof IExternalObjectProject)
        {
            final Collection<IConfigurationProject> projects =
                v8projectManager.getProjects(IConfigurationProject.class);
            if (projects.size() == 1)
            {
                return projects.iterator().next().getProject();
            }
        }
        return v8project.getProject();
    }

    protected String getExecutionClientTypeId(final ILaunchConfiguration configuration, final IProject project,
        final IResolvableRuntimeInstallation resolvable) throws CoreException
    {
        final String applicationId =
            configuration.getAttribute(IDebugConfigurationAttributes.APPLICATION_ID, (String)null);
        if (!Strings.isNullOrEmpty(applicationId))
        {
            final Optional<IApplication> application = applicationManager.getApplication(project, applicationId);
            if (application.isPresent() && application.get() instanceof IInfobaseApplication)
            {
                return ClientTypeSelectionSupport.getExecutionClientTypeId(configuration, resolvable,
                    ((IInfobaseApplication)application.get()).getInfobase(), runtimeComponentManager);
            }
        }
        return ClientTypeSelectionSupport.getExecutionClientTypeId(configuration, resolvable, (InfobaseReference)null,
            runtimeComponentManager);
    }

    protected MdObject getExternalObject(final IExternalObjectProject externalObjectProject)
    {
        final Collection<MdObject> externalObjects =
            JUnitUiPlugin.availableForLaunch(externalObjectProject.getExternalObjects());
        if (externalObjects.size() == 1)
        {
            return externalObjects.iterator().next();
        }
        if (externalObjects.size() > 1)
        {
            final ILabelProvider labelProvider = new WorkbenchLabelProvider();
            ElementListSelectionDialog dialog =
                new ElementListSelectionDialog(JUnitUiPlugin.getActiveWorkbenchShell(), labelProvider);
            dialog.setElements(externalObjects.toArray(new MdObject[externalObjects.size()]));
            dialog.setTitle(Messages.AbstractRuntimeClientLaunchShortcut_External_Object_Selection);
            dialog.setMessage(Messages.AbstractRuntimeClientLaunchShortcut_Select_an_external_object_to_launch);
            dialog.setMultipleSelection(false);
            final int result = dialog.open();
            labelProvider.dispose();
            if (result == 0)
            {
                return (MdObject)dialog.getFirstResult();
            }
        }
        return null;
    }

    protected EObject getExternalObject(final IExternalObjectProject project, final String externalObjectName,
        final String externalObjectType)
    {
        if (project != null && externalObjectName != null && externalObjectType != null)
        {
            return project.getExternalObjects()
                .stream()
                .filter(object -> externalObjectName.equals(object.getName())
                    && externalObjectType.equals(ExternalObjectHelper.getClassName(object)))
                .findFirst()
                .orElse(null);
        }
        return null;
    }

    @Override
    protected String getLaunchConfigurationTypeId()
    {
        return ILaunchConfigurationTypes.RUNTIME_CLIENT;
    }

    protected abstract String getRuntimeComponentTypeId();

    @Override
    protected boolean isValid(final ILaunchConfiguration configuration, final String mode) throws CoreException
    {
        final boolean runtimeUseAuto =
            configuration.getAttribute(IDebugConfigurationAttributes.RUNTIME_INSTALLATION_USE_AUTO, false);
        final String runtime =
            configuration.getAttribute(IDebugConfigurationAttributes.RUNTIME_INSTALLATION, (String)null);
        final boolean isRuntimeValid =
            runtimeUseAuto || runtime != null && resolvableRuntimeInstallationManager.deserialize(runtime) != null;
        return isRuntimeValid && super.isValid(configuration, mode);
    }

    @Override
    protected boolean matches(IProject project, final IApplication application, EObject object,
        final ILaunchConfiguration candidate, final boolean forLaunch)
    {
        try
        {
            String resolvableAsString =
                candidate.getAttribute(IDebugConfigurationAttributes.RUNTIME_INSTALLATION, (String)null);
            final IResolvableRuntimeInstallation resolvable = (resolvableAsString == null) ? null
                : resolvableRuntimeInstallationManager.deserialize(resolvableAsString);
            final String externalProjectName =
                candidate.getAttribute(IDebugConfigurationAttributes.EXTERNAL_OBJECT_PROJECT_NAME, (String)null);
            final String externalObjectName =
                candidate.getAttribute(IDebugConfigurationAttributes.EXTERNAL_OBJECT_NAME, (String)null);
            final String externalObjectType =
                candidate.getAttribute(IDebugConfigurationAttributes.EXTERNAL_OBJECT_TYPE, (String)null);
            final IV8Project v8project = v8projectManager.getProject(project);
            final boolean candiateHasExternalObject = externalProjectName != null && externalObjectType != null;
            boolean candidateHasCorrectExternalObject = !candiateHasExternalObject;
            if (v8project instanceof IExternalObjectProject)
            {
                if (!candiateHasExternalObject)
                {
                    return false;
                }
                if (object == null)
                {
                    object = getExternalObject((IExternalObjectProject)v8project);
                }
            }
            if (v8project instanceof IDependentProject)
            {
                final IProject baseProject = getAppropriateBaseProject(v8project);
                if (baseProject != null)
                {
                    project = baseProject;
                }
            }
            if (object != null && candiateHasExternalObject)
            {
                final MdObject launchingExternalObject = externalObjectExtractor.getExternalObject(object);
                if (launchingExternalObject != null && JUnitUiPlugin.availableForLaunch(launchingExternalObject))
                {
                    final IV8Project externalObjectProject = v8projectManager.getProject(externalProjectName);
                    if (externalObjectProject instanceof IExternalObjectProject)
                    {
                        final EObject externalObject = getExternalObject((IExternalObjectProject)externalObjectProject,
                            externalObjectName, externalObjectType);
                        candidateHasCorrectExternalObject = matchObjects(launchingExternalObject, externalObject);
                    }
                }
            }
            if (!candidateHasCorrectExternalObject)
            {
                return false;
            }
            final RuntimeInstallation installation =
                (resolvable == null) ? null : resolvable.get(new String[] { getRuntimeComponentTypeId() });
            boolean runtimeSupportsClientType = installation == null
                || runtimeComponentManager.supportsExecution(installation, getRuntimeComponentTypeId());
            if (!forLaunch && resolvable != null)
            {
                runtimeSupportsClientType &=
                    getRuntimeComponentTypeId().equals(getExecutionClientTypeId(candidate, project, resolvable));
            }
            return super.matches(project, application, object, candidate, forLaunch) && runtimeSupportsClientType;
        }
        catch (MatchingRuntimeNotFound ignored)
        {
            return false;
        }
        catch (CoreException e)
        {
            JUnitUiPlugin.log(e);
            return false;
        }
    }

    @Override
    protected ILaunchConfigurationWorkingCopy prepareConfiguration(final ILaunchConfiguration launchConfiguration,
        final IApplication application) throws CoreException
    {
        final ILaunchConfigurationWorkingCopy result = super.prepareConfiguration(launchConfiguration, application);
        result.setAttribute(ILaunchConfigurationAttributes.CLIENT_AUTO_SELECT, false);
        result.setAttribute(ILaunchConfigurationAttributes.CLIENT_TYPE, getRuntimeComponentTypeId());
        return result;
    }

    @Override
    protected void setDefaults(final ILaunchConfigurationWorkingCopy configuration, final IProject project)
        throws CoreException
    {
        super.setDefaults(configuration, project);
        configuration.setAttribute(ILaunchConfigurationAttributes.CLIENT_AUTO_SELECT, false);
        configuration.setAttribute(ILaunchConfigurationAttributes.CLIENT_TYPE, getRuntimeComponentTypeId());
        configuration.setAttribute(ILaunchConfigurationAttributes.LAUNCH_USER_USE_INFOBASE_ACCESS, true);
        configuration.setAttribute(ILaunchConfigurationAttributes.CALL_DELAY, 145);
        configuration.setAttribute(ILaunchConfigurationAttributes.DATA_SENDING_DELAY, 45);
        configuration.setAttribute(ILaunchConfigurationAttributes.DATA_RECEIVING_DELAY, 15);
        configuration.setAttribute(ILaunchConfigurationAttributes.DO_NOT_DISPLAY_WARNINGS, true);
        configuration.setAttribute(ILaunchConfigurationAttributes.SHOW_PERFORMANCE, true);
        configuration.setAttribute(ILaunchConfigurationAttributes.SHOW_ALL_FUNCTIONS, true);
    }

    protected void setExternalObjectSettings(final ILaunchConfigurationWorkingCopy configuration,
        final IProject externalObjectProject, final MdObject externalObject)
    {
        configuration.setAttribute(IDebugConfigurationAttributes.EXTERNAL_OBJECT_PROJECT_NAME,
            externalObjectProject.getName());
        configuration.setAttribute(IDebugConfigurationAttributes.EXTERNAL_OBJECT_NAME, externalObject.getName());
        configuration.setAttribute(IDebugConfigurationAttributes.EXTERNAL_OBJECT_TYPE,
            ExternalObjectHelper.getClassName(externalObject));
    }

    @Override
    protected boolean shouldSave(final ILaunchConfiguration configuration, final String mode) throws CoreException
    {
        return configuration.isWorkingCopy() && ((ILaunchConfigurationWorkingCopy)configuration).getParent() == null;
    }
}
