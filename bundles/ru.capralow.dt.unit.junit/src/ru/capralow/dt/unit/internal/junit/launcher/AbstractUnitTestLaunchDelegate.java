package ru.capralow.dt.unit.internal.junit.launcher;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.common.LocaleUtil;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IConfigurationProject;
import com._1c.g5.v8.dt.core.platform.IExternalObjectProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.debug.core.IDebugConfigurationAttributes;
import com._1c.g5.v8.dt.debug.core.IDebugConstants;
import com._1c.g5.v8.dt.debug.core.model.IRuntimeDebugClientTarget;
import com._1c.g5.v8.dt.launching.core.ApplicationPublicationKind;
import com._1c.g5.v8.dt.launching.core.DebugSessionCheckRequest;
import com._1c.g5.v8.dt.launching.core.DebugSessionCheckResponse;
import com._1c.g5.v8.dt.launching.core.ILaunchConfigurationAttributes;
import com._1c.g5.v8.dt.launching.core.launchconfigurations.AbstractRuntimeDebugAwareLaunchDelegate;
import com._1c.g5.v8.dt.launching.core.launchconfigurations.ClientTypeSelectionSupport;
import com._1c.g5.v8.dt.launching.core.launchconfigurations.ExternalObjectHelper;
import com._1c.g5.v8.dt.platform.services.core.dump.IExternalObjectDumpSupport;
import com._1c.g5.v8.dt.platform.services.core.infobases.IInfobaseAccessManager;
import com._1c.g5.v8.dt.platform.services.core.infobases.IInfobaseAccessSettings;
import com._1c.g5.v8.dt.platform.services.core.runtimes.RuntimeInstallations;
import com._1c.g5.v8.dt.platform.services.core.runtimes.environments.IResolvableRuntimeInstallation;
import com._1c.g5.v8.dt.platform.services.core.runtimes.environments.MatchingRuntimeNotFound;
import com._1c.g5.v8.dt.platform.services.core.runtimes.execution.IRuntimeComponentManager;
import com._1c.g5.v8.dt.platform.services.core.runtimes.execution.IRuntimeComponentTypes;
import com._1c.g5.v8.dt.platform.services.core.runtimes.execution.RuntimeExecutionArguments;
import com._1c.g5.v8.dt.platform.services.model.InfobaseAccess;
import com._1c.g5.v8.dt.platform.services.model.InfobaseReference;
import com._1c.g5.v8.dt.platform.services.model.RuntimeInstallation;
import com._1c.g5.v8.dt.platform.version.Version;
import com.e1c.g5.dt.applications.ExecutionContext;
import com.e1c.g5.dt.applications.IApplication;
import com.e1c.g5.dt.applications.IApplicationManager;
import com.e1c.g5.dt.applications.PublishKind;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import ru.capralow.dt.unit.internal.junit.JUnitPlugin;

public class AbstractUnitTestLaunchDelegate
    extends AbstractRuntimeDebugAwareLaunchDelegate
{
    @Inject
    private IRuntimeComponentManager runtimeComponentManager;
    @Inject
    private IInfobaseAccessManager infobaseAccessManager;
    @Inject
    private IApplicationManager applicationManager;
    @Inject
    private IExternalObjectDumpSupport externalObjectDumpSupport;

    private static final String DEBUG = "debug"; //$NON-NLS-1$

    @Override
    public void doLaunch(final ILaunchConfiguration configuration, final String mode, final ILaunch launch,
        final IProgressMonitor monitor) throws CoreException
    {
        final IV8Project v8project = getProject(configuration);
        final IProject project = v8project.getProject();
        final var progress = SubMonitor.convert(monitor, DEBUG.equals(mode) ? 60 : 50);
        var context = new ExecutionContext();
        final String clientTypeId = ClientTypeSelectionSupport.getExecutionClientTypeId(configuration,
            (IResolvableRuntimeInstallation)null, (InfobaseReference)null, runtimeComponentManager);
        final ApplicationPublicationKind publicationKind =
            IRuntimeComponentTypes.WEB_CLIENT.equalsIgnoreCase(clientTypeId) ? ApplicationPublicationKind.INFOBASE
                : ApplicationPublicationKind.NONE;
        final var applicationPrepareResult =
            getOrPrepareApplication(configuration, launch, mode, project, publicationKind, progress.newChild(1));
        if (applicationPrepareResult.getApplication().isEmpty())
        {
            return;
        }
        final IApplication application = applicationPrepareResult.getApplication().get();
        if (applicationPrepareResult.getUrlAccess().isPresent())
        {
            context.setProperty("launchUrl", //$NON-NLS-1$
                applicationPrepareResult.getUrlAccess()
                    .get()
                    .getUrl()
                    .orElseThrow(() -> new CoreException(JUnitPlugin.createErrorStatus(
                        Messages.AbstractUnitTestLaunchDelegate_Cannot_get_application_publication_URL))));
        }
        progress.worked(19);
        if (DEBUG.equals(mode))
        {
            final IStatus checkDebugSessionStatus = checkDebugSession(project, application, launch, configuration);
            if (!checkDebugSessionStatus.isOK())
            {
                handleErrorStatus(checkDebugSessionStatus, progress.newChild(1), launch);
                return;
            }
        }
        final IStatus updateApplicationStatus =
            updateApplication(project, application, PublishKind.INCREMENTAL, progress.newChild(29));
        if (!updateApplicationStatus.isOK())
        {
            handleErrorStatus(updateApplicationStatus, progress.newChild(1), launch);
            return;
        }
        if (monitor.isCanceled())
        {
            return;
        }
        final RuntimeExecutionArguments arguments =
            buildExecutionArguments(configuration, v8project, null, progress.newChild(1));
        context.setProperty("activeLaunch", launch); //$NON-NLS-1$
        context = applicationManager.launch(application, mode, context, progress);
        context.setProperty("clientType", clientTypeId); //$NON-NLS-1$
        context.setProperty("clientArguments", arguments); //$NON-NLS-1$
        if (DEBUG.equals(mode))
        {
            final Optional<URL> debugServerUrl = context.getProperty("debugUrl"); //$NON-NLS-1$
            if (debugServerUrl.isPresent())
            {
                arguments.setDebugServerUrl(debugServerUrl.get());
                if (launch.getDebugTargets().length == 0)
                {
                    final Optional<IDebugTarget> debugTarget = context.getProperty("debugTarget"); //$NON-NLS-1$
                    debugTarget.ifPresent(launch::addDebugTarget);
                }
            }
            else
            {
                final var debugUrl = getDebugUrl(configuration, launch, applicationPrepareResult);
                attachDebugTarget(configuration, launch, application, debugUrl, getInstallation(configuration).get(),
                    progress.newChild(9));
                arguments.setDebugServerUrl(debugUrl);
            }
        }
        final Optional<Process> process = applicationManager.open(application, context, progress);
        if (process.isPresent())
        {
            DebugPlugin.newProcess(launch, process.get(),
                formatProcessName(process.get().toHandle().info().command().get()),
                buildProcessArguments(configuration));
        }
    }

    protected RuntimeExecutionArguments buildExecutionArguments(final ILaunchConfiguration configuration,
        final IV8Project v8project, final InfobaseReference infobase, final IProgressMonitor monitor)
        throws CoreException
    {
        final var arguments = new RuntimeExecutionArguments();
        final boolean useInfobaseAccessUser =
            configuration.getAttribute(ILaunchConfigurationAttributes.LAUNCH_USER_USE_INFOBASE_ACCESS, false);
        if (useInfobaseAccessUser && infobase != null)
        {
            final IInfobaseAccessSettings settings = infobaseAccessManager.getSettings(infobase);
            arguments.setAccess(settings.access());
            arguments.setUsername(settings.userName());
            arguments.setPassword(settings.password());
        }
        else
        {
            final InfobaseAccess access =
                configuration.getAttribute(ILaunchConfigurationAttributes.LAUNCH_OS_INFOBASE_ACCESS, true)
                    ? InfobaseAccess.OS : InfobaseAccess.INFOBASE;
            arguments.setAccess(access);
            if (access == InfobaseAccess.INFOBASE)
            {
                arguments.setUsername(
                    configuration.getAttribute(ILaunchConfigurationAttributes.LAUNCH_USER_NAME, (String)null));
                arguments.setPassword(
                    configuration.getAttribute(ILaunchConfigurationAttributes.LAUNCH_USER_PASSWORD, (String)null));
            }
        }
        arguments.setDataSeparation(
            configuration.getAttribute(ILaunchConfigurationAttributes.DATA_SEPARATION, (String)null));
        arguments
            .setStartupOption(configuration.getAttribute(ILaunchConfigurationAttributes.STARTUP_OPTION, (String)null));
        arguments.setSlowConnection(configuration.getAttribute(ILaunchConfigurationAttributes.SLOW_CONNECTION, false));
        arguments.setEmulateDelay(configuration.getAttribute(ILaunchConfigurationAttributes.EMULATE_DELAY, false));
        arguments.setCallDelay(getDouble(configuration.getAttribute(ILaunchConfigurationAttributes.CALL_DELAY, 0)));
        arguments.setDataSendingDelay(
            getDouble(configuration.getAttribute(ILaunchConfigurationAttributes.DATA_SENDING_DELAY, 0)));
        arguments.setDataReceivingDelay(
            getDouble(configuration.getAttribute(ILaunchConfigurationAttributes.DATA_RECEIVING_DELAY, 0)));
        arguments.setDisableStartupMessages(
            configuration.getAttribute(ILaunchConfigurationAttributes.DO_NOT_DISPLAY_WARNINGS, false));
        arguments
            .setDisplayPerformance(configuration.getAttribute(ILaunchConfigurationAttributes.SHOW_PERFORMANCE, false));
        final boolean technicialSpecialistMode =
            configuration.getAttribute(ILaunchConfigurationAttributes.SHOW_ALL_FUNCTIONS, false);
        if (v8project.getVersion().isGreaterThan(Version.V8_3_16))
        {
            arguments.setTechnicalSpecialistMode(technicialSpecialistMode);
        }
        else
        {
            arguments.setDisplayAllFunctions(technicialSpecialistMode);
        }
        final String name = configuration.getAttribute(ILaunchConfigurationAttributes.LAUNCH_PWA_APP_NAME, ""); //$NON-NLS-1$
        if (StringUtils.isNotBlank(name))
        {
            arguments.setPwaAppName(configuration.getAttribute(ILaunchConfigurationAttributes.LAUNCH_PWA_APP_NAME, "")); //$NON-NLS-1$
        }
        final String logFile = configuration.getAttribute(ILaunchConfigurationAttributes.LOG_FILE, (String)null);
        if (logFile != null)
        {
            arguments.setLogFile(new File(logFile));
        }
        arguments.setClear(configuration.getAttribute(ILaunchConfigurationAttributes.DO_NOT_CLEAR_LOG, false));
        final String sessionLocale =
            configuration.getAttribute(ILaunchConfigurationAttributes.SESSION_LOCALE, (String)null);
        if (!Strings.isNullOrEmpty(sessionLocale))
        {
            arguments.setSessionLocale(LocaleUtil.createLocale(sessionLocale));
        }
        final String interfaceLanguage =
            configuration.getAttribute(ILaunchConfigurationAttributes.INTERFACE_LANGUAGE, (String)null);
        if (interfaceLanguage != null)
        {
            arguments.setInterfaceLanguage(interfaceLanguage);
        }
        final String externalObjectProjectName =
            configuration.getAttribute(IDebugConfigurationAttributes.EXTERNAL_OBJECT_PROJECT_NAME, (String)null);
        final IV8Project externalProject =
            (externalObjectProjectName == null) ? null : getProject(externalObjectProjectName);
        if (externalProject instanceof IExternalObjectProject)
        {
            final IExternalObjectProject externalObjectProject = (IExternalObjectProject)externalProject;
            if (externalObjectDumpSupport.isEnabled(externalObjectProject.getProject()))
            {
                final String externalObjectName =
                    configuration.getAttribute(IDebugConfigurationAttributes.EXTERNAL_OBJECT_NAME, (String)null);
                final String externalObjectType =
                    configuration.getAttribute(IDebugConfigurationAttributes.EXTERNAL_OBJECT_TYPE, (String)null);
                final EObject externalObject = ExternalObjectHelper.getExternalObject(externalObjectProject,
                    externalObjectName, externalObjectType);
                if (externalObject != null)
                {
                    arguments.setExternalObjectDumpPath(externalObjectDumpSupport
                        .getDump(externalObjectProject.getProject(), externalObject, true, monitor));
                }
                else
                {
                    JUnitPlugin.log(JUnitPlugin.createErrorStatus(MessageFormat.format(
                        Messages.AbstractUnitTestLaunchDelegate_External_object_with_name__0__not_found_in_project__1,
                        externalObjectName, externalObjectProjectName)));
                }
            }
            else
            {
                JUnitPlugin.log(JUnitPlugin.createErrorStatus(MessageFormat.format(
                    Messages.AbstractUnitTestLaunchDelegate_External_dump_generation_is_disabled_for_project__0,
                    externalObjectProjectName)));
            }
        }
        return arguments;
    }

    private Map<String, String> buildProcessArguments(final ILaunchConfiguration configuration) throws CoreException
    {
        final Map<String, String> result =
            new HashMap<>(Maps.transformValues(configuration.getAttributes(), Object::toString));
        result.put(IDebugConstants.ATTRIBUTE_HIDE_DEBUG_TARGET, Boolean.toString(true));
        return result;
    }

    private IStatus checkDebugSession(final IProject project, final IApplication application, final ILaunch launch,
        final ILaunchConfiguration configuration) throws CoreException
    {
        final List<ILaunch> sameSameDebugSession = new ArrayList<>();
        ILaunch[] launches;
        for (int length = (launches = DebugPlugin.getDefault().getLaunchManager().getLaunches()).length,
            i = 0; i < length; ++i)
        {
            final ILaunch candidate = launches[i];
            if (candidate != launch && !candidate.isTerminated() && DEBUG.equals(candidate.getLaunchMode())
                && hasSameDebugSession(configuration, project, application, candidate))
            {
                sameSameDebugSession.add(candidate);
            }
        }
        if (sameSameDebugSession.isEmpty())
        {
            return Status.OK_STATUS;
        }
        final IStatus debugSessionAlreadyStarted =
            JUnitPlugin.createErrorStatus(Messages.AbstractUnitTestLaunchDelegate_Debug_session_already_started, 1003);
        final DebugSessionCheckResponse response = (DebugSessionCheckResponse)this
            .handleStatus(debugSessionAlreadyStarted, (Object)new DebugSessionCheckRequest(project, application));
        switch (response)
        {
        case RESTART_APPLICATION:
            {
                for (final ILaunch candidate2 : sameSameDebugSession)
                {
                    if (!candidate2.isTerminated())
                    {
                        try
                        {
                            Arrays.stream(candidate2.getDebugTargets()).forEach(candidate2::removeDebugTarget);
                            candidate2.terminate();
                        }
                        catch (DebugException e)
                        {
                            JUnitPlugin.log(JUnitPlugin.createErrorStatus(e.getMessage(), e));
                        }
                    }
                }
                return Status.OK_STATUS;
            }
        case LAUNCH_ANYWAY:
            {
                return Status.OK_STATUS;
            }
        case CANCEL:
            {
                return Status.CANCEL_STATUS;
            }
        default:
            {
                throw new IllegalStateException(MessageFormat.format("Unexpected result \"{0}\"", response)); //$NON-NLS-1$
            }
        }
    }

    private String formatProcessName(final String processName)
    {
        final String timestamp = DateFormat.getDateTimeInstance(2, 2).format(new Date());
        return String.format("%s (%s)", processName, timestamp); //$NON-NLS-1$
    }

    private double getDouble(final int intRepresentedDouble)
    {
        return intRepresentedDouble / 100.0;
    }

    private boolean hasSameDebugSession(final ILaunchConfiguration configuration, final IProject project,
        final IApplication application, final ILaunch candidate) throws CoreException
    {
        final IDebugTarget debugTarget = candidate.getDebugTarget();
        final ILaunchConfiguration candidateConfiguration = candidate.getLaunchConfiguration();
        return debugTarget instanceof IRuntimeDebugClientTarget
            && Objects.equals(project, debugTarget.getAdapter(IProject.class))
            && Objects.equals(configuration.getType().getIdentifier(), candidateConfiguration.getType().getIdentifier())
            && Objects.equals(application.getId(),
                candidateConfiguration.getAttribute(IDebugConfigurationAttributes.APPLICATION_ID, (String)null));
    }

    @Override
    protected IStatus isValid(final ILaunchConfiguration configuration, final String mode) throws CoreException
    {
        final IV8Project project = getProject(configuration);
        if (!(project instanceof IConfigurationProject))
        {
            return JUnitPlugin.createErrorStatus(Messages.AbstractUnitTestLaunchDelegate_Incorrect_project, 1001);
        }
        final String externalProjectName =
            configuration.getAttribute(IDebugConfigurationAttributes.EXTERNAL_OBJECT_PROJECT_NAME, (String)null);
        final String externalObjectName =
            configuration.getAttribute(IDebugConfigurationAttributes.EXTERNAL_OBJECT_NAME, (String)null);
        final String externalObjectType =
            configuration.getAttribute(IDebugConfigurationAttributes.EXTERNAL_OBJECT_TYPE, (String)null);
        final IV8Project v8project = (externalProjectName == null) ? null : getProject(externalProjectName);
        if (v8project instanceof IExternalObjectProject && externalObjectType != null)
        {
            final IExternalObjectProject externalObjectProject = (IExternalObjectProject)v8project;
            final EObject externalObject =
                ExternalObjectHelper.getExternalObject(externalObjectProject, externalObjectName, externalObjectType);
            if ((externalObjectProject != null || externalObjectName != null) && externalObject == null)
            {
                return JUnitPlugin.createErrorStatus(Messages.AbstractUnitTestLaunchDelegate_Incorrect_external_object,
                    1001);
            }
        }
        final String clientTypeId =
            configuration.getAttribute(ILaunchConfigurationAttributes.CLIENT_TYPE, (String)null);
        if (!IRuntimeComponentTypes.WEB_CLIENT.equalsIgnoreCase(clientTypeId))
        {
            final Optional<IResolvableRuntimeInstallation> resolvable = getInstallation(configuration);
            if (resolvable.isPresent())
            {
                try
                {
                    final RuntimeInstallation installation = resolvable.get().get();
                    if (RuntimeInstallations.isFile(installation.getLocation()) && clientTypeId != null
                        && !runtimeComponentManager.supportsExecution(installation, clientTypeId))
                    {
                        final String message = MessageFormat.format(
                            Messages.AbstractUnitTestLaunchDelegate_1C_Enterprise__0__has_no__1__installed,
                            installation.getName(), runtimeComponentManager.getType(clientTypeId).getName());
                        return JUnitPlugin.createErrorStatus(message, 1000);
                    }
                }
                catch (MatchingRuntimeNotFound e)
                {
                    return createMatchingRuntimeNotFoundErrorStatus(1001, e);
                }
            }
        }
        return Status.OK_STATUS;
    }
}
