/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.internal.launching.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.antlr.stringtemplate.StringTemplate;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.emf.ecore.EObject;
import org.osgi.framework.Bundle;

import com._1c.g5.v8.dt.common.LocaleUtil;
import com._1c.g5.v8.dt.common.Pair;
import com._1c.g5.v8.dt.core.platform.IExternalObjectProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.debug.core.IDebugConfigurationAttributes;
import com._1c.g5.v8.dt.debug.core.model.IRuntimeDebugClientTarget;
import com._1c.g5.v8.dt.internal.launching.core.LaunchingPlugin;
import com._1c.g5.v8.dt.internal.launching.core.launchconfigurations.RuntimeClientLaunchDelegate;
import com._1c.g5.v8.dt.launching.core.DebugSessionAlreadyStartedResponse;
import com._1c.g5.v8.dt.launching.core.ILaunchConfigurationAttributes;
import com._1c.g5.v8.dt.launching.core.PublicationResult;
import com._1c.g5.v8.dt.launching.core.launchconfigurations.ClientTypeSelectionSupport;
import com._1c.g5.v8.dt.launching.core.launchconfigurations.ExternalObjectHelper;
import com._1c.g5.v8.dt.platform.services.core.dump.IExternalObjectDumpSupport;
import com._1c.g5.v8.dt.platform.services.core.infobases.IInfobaseAccessManager;
import com._1c.g5.v8.dt.platform.services.core.infobases.IInfobaseAccessSettings;
import com._1c.g5.v8.dt.platform.services.core.publication.IPublicationManager;
import com._1c.g5.v8.dt.platform.services.core.publication.WebServerAccessException;
import com._1c.g5.v8.dt.platform.services.core.runtimes.environments.IResolvableRuntimeInstallation;
import com._1c.g5.v8.dt.platform.services.core.runtimes.environments.IResolvableRuntimeInstallationManager;
import com._1c.g5.v8.dt.platform.services.core.runtimes.environments.MatchingRuntimeNotFound;
import com._1c.g5.v8.dt.platform.services.core.runtimes.execution.ILaunchableRuntimeComponent;
import com._1c.g5.v8.dt.platform.services.core.runtimes.execution.IRuntimeClientLauncher;
import com._1c.g5.v8.dt.platform.services.core.runtimes.execution.IRuntimeComponent;
import com._1c.g5.v8.dt.platform.services.core.runtimes.execution.IRuntimeComponentExecutor;
import com._1c.g5.v8.dt.platform.services.core.runtimes.execution.IRuntimeComponentManager;
import com._1c.g5.v8.dt.platform.services.core.runtimes.execution.IRuntimeComponentType;
import com._1c.g5.v8.dt.platform.services.core.runtimes.execution.IUrlOpenClientLauncher;
import com._1c.g5.v8.dt.platform.services.core.runtimes.execution.RuntimeExecutionArguments;
import com._1c.g5.v8.dt.platform.services.core.runtimes.execution.RuntimeExecutionException;
import com._1c.g5.v8.dt.platform.services.model.AppArch;
import com._1c.g5.v8.dt.platform.services.model.Arch;
import com._1c.g5.v8.dt.platform.services.model.InfobaseAccess;
import com._1c.g5.v8.dt.platform.services.model.InfobaseReference;
import com._1c.g5.v8.dt.platform.services.model.InfobaseType;
import com._1c.g5.v8.dt.platform.services.model.Publication;
import com._1c.g5.v8.dt.platform.services.model.RuntimeInstallation;
import com._1c.g5.v8.dt.platform.services.model.WebServerConnectionString;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.CharSource;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.inject.Inject;

import ru.capralow.dt.unit.launcher.UnitTestLaunchConfigurationAttributes;
import ru.capralow.dt.unit.launcher.frameworks.FrameworkUtils;
import ru.capralow.dt.unit.launcher.frameworks.gson.FrameworkSettings;

public class RuntimeUnitLauncherLaunchDelegate
    extends RuntimeClientLaunchDelegate
{

    private static final String DEBUG = "debug"; //$NON-NLS-1$

    private static Map<String, String> buildProcessArguments(final ILaunchConfiguration configuration)
        throws CoreException
    {
        return Maps.transformValues(configuration.getAttributes(), input -> input.toString());
    }

    private static String formatProcessName(final String processName)
    {
        final String timestamp = DateFormat.getDateTimeInstance(2, 2).format(new Date());
        return String.format("%s (%s)", processName, timestamp); //$NON-NLS-1$
    }

    private static double getDouble(final int intRepresentedDouble)
    {
        return intRepresentedDouble / 100.0;
    }

    private static CharSource getFileInputSupplier(URL resourceUrl)
    {
        return Resources.asCharSource(resourceUrl, StandardCharsets.UTF_8);
    }

    private static String getLaunchUrl(final ILaunchConfiguration configuration) throws CoreException
    {
        return configuration.getAttribute(ILaunchConfigurationAttributes.LAUNCH_URL, (String)null);
    }

    private static boolean matchesByArch(final RuntimeInstallation installation, final InfobaseReference infobase)
    {
        final AppArch infobaseArch = infobase.getAppArch();
        final Arch installationArch = installation.getArch();
        return (infobaseArch != AppArch.X86 || installationArch != Arch.X86_64)
            && (infobaseArch != AppArch.X86_64 || installationArch == Arch.X86_64);
    }

    private static String readContents(CharSource source)
    {
        try (Reader reader = source.openBufferedStream())
        {
            return CharStreams.toString(reader);

        }
        catch (IOException | NullPointerException e)
        {
            LaunchingPlugin.log(LaunchingPlugin.createErrorStatus(e.getMessage(), e));
            return ""; //$NON-NLS-1$

        }
    }

    private static boolean saveFrameworkToFile(ILaunchConfiguration configuration)
    {
        Bundle bundle = FrameworkUtils.getFrameworkBundle();
        try
        {
            FrameworkSettings frameworkSettings = FrameworkUtils.getFrameworkSettings();
            String frameworkEpfName = FrameworkUtils.getFrameworkEpfName(frameworkSettings);

            URL frameworkParamsBundleUrl =
                FileLocator.find(bundle, new Path(FrameworkUtils.FRAMEWORK_FILES_ROOT_PATH + frameworkEpfName), null);
            URL frameworkParamsUrl = FileLocator.toFileURL(frameworkParamsBundleUrl);

            if (frameworkParamsUrl == null)
            {
                String msg = MessageFormat.format(
                    Messages.RuntimeUnitLauncherLaunchDelegate_Failed_to_get_framework_from_bundle_0_1,
                    bundle.getSymbolicName(), frameworkEpfName);
                LaunchingPlugin.log(LaunchingPlugin.createErrorStatus(msg, new IOException()));
                return false;
            }
            File file = URIUtil.toFile(URIUtil.toURI(frameworkParamsUrl));

            if (!file.exists())
            {
                String msg = MessageFormat.format(Messages.RuntimeUnitLauncherLaunchDelegate_Failed_to_read_framework_0,
                    file.toString());
                LaunchingPlugin.log(LaunchingPlugin.createErrorStatus(msg, new IOException()));
                return false;
            }

            String frameworkFilePathName = FrameworkUtils.getConfigurationFilesPath(configuration);
            Files.copy(file, new File(frameworkFilePathName + FrameworkUtils.FRAMEWORK_FILE_NAME));

        }
        catch (IOException | URISyntaxException e)
        {
            String msg = MessageFormat.format(Messages.RuntimeUnitLauncherLaunchDelegate_Failed_to_save_framework_0,
                FrameworkUtils.FRAMEWORK_FILE_NAME);
            LaunchingPlugin.log(LaunchingPlugin.createErrorStatus(msg, e));
            return false;

        }

        return true;
    }

    @Inject
    private IV8ProjectManager projectManager;

    @Inject
    private IResolvableRuntimeInstallationManager resolvableRuntimeInstallationManager;

    @Inject
    private IRuntimeComponentManager runtimeComponentManager;

    @Inject
    private IInfobaseAccessManager infobaseAccessManager;

    @Inject
    private IPublicationManager publicationManager;

    @Inject
    private IExternalObjectDumpSupport externalObjectDumpSupport;

    @Override
    public void doLaunch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
        throws CoreException
    {

        if (!saveParamsToFile(configuration))
            return;

        if (!saveFrameworkToFile(configuration))
            return;

        doSuperLaunch(configuration, mode, launch, monitor);
    }

    private RuntimeExecutionArguments buildExecutionArgumentsEx(final ILaunchConfiguration configuration,
        final InfobaseReference infobase, final IProgressMonitor monitor) throws CoreException
    {
        final RuntimeExecutionArguments arguments = new RuntimeExecutionArguments();
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
        arguments.setDisplayAllFunctions(
            configuration.getAttribute(ILaunchConfigurationAttributes.SHOW_ALL_FUNCTIONS, false));
        final String logFile = configuration.getAttribute(ILaunchConfigurationAttributes.LOG_FILE, (String)null);
        if (logFile != null)
        {
            arguments.setLogFile(new File(logFile));
        }
        arguments.setClear(configuration.getAttribute(ILaunchConfigurationAttributes.DO_NOT_CLEAR_LOG, false));
        final String sessionLocale =
            configuration.getAttribute(ILaunchConfigurationAttributes.SESSION_LOCALE, (String)null);
        if (sessionLocale != null)
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
        final IV8Project v8project = (externalObjectProjectName == null) ? null : getProject(externalObjectProjectName);
        if (v8project instanceof IExternalObjectProject)
        {
            final IExternalObjectProject externalObjectProject = (IExternalObjectProject)v8project;
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
                    LaunchingPlugin.log(LaunchingPlugin.createErrorStatus(MessageFormat.format(
                        Messages.RuntimeClientLaunchDelegate_External_object_with_name__0__not_found_in_project__1,
                        externalObjectName, externalObjectProjectName)));
                }
            }
            else
            {
                LaunchingPlugin.log(LaunchingPlugin.createErrorStatus(MessageFormat.format(
                    Messages.RuntimeClientLaunchDelegate_External_dump_generation_is_disabled_for_project__0,
                    externalObjectProjectName)));
            }
        }

        final String externalObjectDumpPath =
            configuration.getAttribute(UnitTestLaunchConfigurationAttributes.EXTERNAL_OBJECT_DUMP_PATH, (String)null);
        if (externalObjectDumpPath != null)
        {
            File file = new File(externalObjectDumpPath);
            arguments.setExternalObjectDumpPath(file.toPath());
        }

        final String externalObjectStartupOptions = configuration
            .getAttribute(UnitTestLaunchConfigurationAttributes.EXTERNAL_OBJECT_STARTUP_OPTIONS, (String)null);
        String startupOptions = configuration.getAttribute(ILaunchConfigurationAttributes.STARTUP_OPTION, (String)null);
        if (Strings.isNullOrEmpty(startupOptions))
            startupOptions = externalObjectStartupOptions;
        else
            startupOptions = externalObjectStartupOptions + ";" + startupOptions; //$NON-NLS-1$
        arguments.setStartupOption(startupOptions);

        return arguments;
    }

    private IStatus checkDebugSessionEx(final IProject project, final ILaunch launch) throws CoreException
    {
        final List<ILaunch> sameProject = new ArrayList<>();
        ILaunch[] launches = DebugPlugin.getDefault().getLaunchManager().getLaunches();
        for (int length = launches.length, i = 0; i < length; ++i)
        {
            final ILaunch candidate = launches[i];
            if (candidate != launch && !candidate.isTerminated() && DEBUG.equals(candidate.getLaunchMode()))
            {
                final IDebugTarget debugTarget = candidate.getDebugTarget();
                if (debugTarget instanceof IRuntimeDebugClientTarget
                    && Objects.equals(project, debugTarget.getAdapter(IProject.class)))
                {
                    sameProject.add(candidate);
                }
            }
        }
        if (sameProject.isEmpty())
        {
            return Status.OK_STATUS;
        }
        final IStatus debugSessionAlreadyStarted =
            LaunchingPlugin.createErrorStatus(Messages.RuntimeClientLaunchDelegate_Debug_session_already_started, 1003);
        final DebugSessionAlreadyStartedResponse response = handleStatus(debugSessionAlreadyStarted, (Object)project);
        switch (response)
        {
        case RESTART_APPLICATION:
            for (final ILaunch candidate2 : sameProject)
            {
                if (!candidate2.isTerminated())
                {
                    try
                    {
                        candidate2.terminate();
                    }
                    catch (DebugException e)
                    {
                        LaunchingPlugin.log(LaunchingPlugin.createErrorStatus(e.getMessage(), e));
                    }
                }
            }
            return Status.OK_STATUS;

        case LAUNCH_ANYWAY:
            return Status.OK_STATUS;

        case CANCEL:
            return Status.CANCEL_STATUS;

        default:
            throw new IllegalStateException(MessageFormat.format("Unexpected result \"{0}\"", response)); //$NON-NLS-1$

        }
    }

    private void doSuperLaunch(final ILaunchConfiguration configuration, final String mode, final ILaunch launch,
        final IProgressMonitor monitor) throws CoreException
    {
        final IProject project = getProject(configuration).getProject();
        final String launchUrl = getLaunchUrl(configuration);
        final InfobaseReference infobase = getInfobase(configuration);
        final IResolvableRuntimeInstallation resolvable = getInstallationEx(configuration);
        final boolean deployBeforeLaunch =
            configuration.getAttribute(ILaunchConfigurationAttributes.DEPLOY_BEFORE_LAUNCH, true);
        final boolean deployFull =
            configuration.getAttribute(ILaunchConfigurationAttributes.DEPLOY_FULL_CONFIGURATION, false);
        final boolean associateAfterDeploy =
            configuration.getAttribute(ILaunchConfigurationAttributes.ASSOCIATE_AFTER_DEPLOY, false);
        final SubMonitor subMonitor = SubMonitor.convert(monitor, DEBUG.equals(mode) ? 60 : 50);
        if (launchUrl == null)
        {
            if (DEBUG.equals(mode))
            {
                final IStatus checkDebugSessionStatus = checkDebugSessionEx(project, launch);
                if (!checkDebugSessionStatus.isOK())
                {
                    handleErrorStatus(checkDebugSessionStatus, subMonitor.newChild(1), launch);
                    return;
                }
            }
            final IStatus infobaseExistenceStatus = checkInfobaseExistence(infobase, subMonitor.newChild(1));
            if (!infobaseExistenceStatus.isOK())
            {
                handleErrorStatus(infobaseExistenceStatus, subMonitor.newChild(1), launch);
                return;
            }
            if (deployBeforeLaunch)
            {
                final IStatus infobaseUpdateStatus = updateInfobaseDatabase(project, infobase, deployFull,
                    associateAfterDeploy, subMonitor.newChild(30));
                if (!infobaseUpdateStatus.isOK())
                {
                    handleErrorStatus(infobaseUpdateStatus, subMonitor.newChild(1), launch);
                    return;
                }
            }
            if (monitor.isCanceled())
            {
                return;
            }
            Publication publication = null;
            final boolean needPublication =
                configuration.getAttribute(ILaunchConfigurationAttributes.NEED_INFOBASE_PUBLICATION, false);
            if (needPublication)
            {
                final PublicationResult publishResult = publishInfobase(infobase, resolvable, subMonitor.newChild(10));
                final IStatus publishStatus = publishResult.getStatus();
                publication = publishResult.getPublication();
                if (!publishStatus.isOK())
                {
                    handleErrorStatus(publishStatus, subMonitor.newChild(1), launch);
                    return;
                }
            }
            if (monitor.isCanceled())
            {
                return;
            }
            final RuntimeExecutionArguments arguments =
                buildExecutionArgumentsEx(configuration, infobase, subMonitor.newChild(1));
            if (DEBUG.equals(mode))
            {
                final String debugServerUrl =
                    attachDebugTarget(configuration, launch, resolvable, subMonitor.newChild(10));
                arguments.setDebugServerUrl(debugServerUrl);
            }
            final IStatus launchStatus =
                launchInfobaseClientEx(configuration, launch, infobase, resolvable, arguments, publication);
            if (!launchStatus.isOK())
            {
                handleErrorStatus(launchStatus, subMonitor.newChild(1), launch);
                return;
            }
            if (associateAfterDeploy)
            {
                associate(project, infobase, deployBeforeLaunch);
            }
        }
        else
        {
            final RuntimeExecutionArguments arguments2 =
                buildExecutionArgumentsEx(configuration, infobase, subMonitor.newChild(1));
            if (DEBUG.equals(mode))
            {
                final IStatus checkDebugSessionStatus2 = checkDebugSessionEx(project, launch);
                if (!checkDebugSessionStatus2.isOK())
                {
                    return;
                }
                final String debugServerUrl2 =
                    attachDebugTarget(configuration, launch, resolvable, subMonitor.newChild(10));
                arguments2.setDebugServerUrl(debugServerUrl2);
            }
            final IStatus launchStatus2 =
                launchUrlConnectionClientEx(configuration, launch, launchUrl, resolvable, infobase, arguments2);
            if (!launchStatus2.isOK())
            {
                handleErrorStatus(launchStatus2, subMonitor.newChild(1), launch);
            }
        }
    }

    private String getFeaturesPath(ILaunchConfiguration configuration)
    {
        String featuresPath = ""; //$NON-NLS-1$
        try
        {
            boolean runExtensionTests =
                configuration.getAttribute(UnitTestLaunchConfigurationAttributes.RUN_EXTENSION_TESTS, false);
            boolean runModuleTests =
                configuration.getAttribute(UnitTestLaunchConfigurationAttributes.RUN_MODULE_TESTS, false);
            boolean runTagTests =
                configuration.getAttribute(UnitTestLaunchConfigurationAttributes.RUN_TAG_TESTS, false);
            IProject project = FrameworkUtils.getConfigurationProject(configuration, projectManager);
            String commonModule = FrameworkUtils.getModuleFromConfiguration(configuration, projectManager);
            String tag = FrameworkUtils.getTagFromConfiguration(configuration, projectManager);

            featuresPath = project.getLocation() + "/features/"; //$NON-NLS-1$
            if (runExtensionTests)
                featuresPath += "all/"; //$NON-NLS-1$
            else if (runModuleTests)
                featuresPath += "all/" + commonModule + ".feature"; //$NON-NLS-1$ //$NON-NLS-2$
            else if (runTagTests)
                featuresPath += tag + "/"; //$NON-NLS-1$

        }
        catch (CoreException e)
        {
            LaunchingPlugin.log(LaunchingPlugin
                .createErrorStatus(Messages.RuntimeUnitLauncherLaunchDelegate_Incorrect_launch_configuration, e));

        }

        return featuresPath;
    }

    private IResolvableRuntimeInstallation getInstallationEx(final ILaunchConfiguration configuration)
        throws CoreException
    {
        final String resolvableAsString =
            configuration.getAttribute(IDebugConfigurationAttributes.RUNTIME_INSTALLATION, (String)null);
        return (resolvableAsString == null) ? null
            : resolvableRuntimeInstallationManager.deserialize(resolvableAsString);
    }

    private String getLaunchUrlEx(final InfobaseReference infobase, final Publication publication) throws CoreException
    {
        if (infobase.getInfobaseType() == InfobaseType.WEB)
        {
            final WebServerConnectionString connectionString =
                (WebServerConnectionString)infobase.getConnectionString();
            return connectionString.getUrl();
        }
        if (publication != null)
        {
            try
            {
                return publicationManager.getPublicationUrl(publication.getWebServer(), publication.getName());
            }
            catch (WebServerAccessException e)
            {
                throw new CoreException(
                    LaunchingPlugin.createErrorStatus(Messages.RuntimeClientLaunchDelegate_Error_while_launching, e));
            }
        }
        throw new CoreException(LaunchingPlugin.createErrorStatus("Publication not provided for web client launch")); //$NON-NLS-1$
    }

    private String getProjectPath(ILaunchConfiguration configuration)
    {
        String projectPath = ""; //$NON-NLS-1$
        try
        {
            IProject project = FrameworkUtils.getConfigurationProject(configuration, projectManager);

            projectPath = project.getLocation() + "/"; //$NON-NLS-1$

        }
        catch (CoreException e)
        {
            LaunchingPlugin.log(LaunchingPlugin
                .createErrorStatus(Messages.RuntimeUnitLauncherLaunchDelegate_Incorrect_launch_configuration, e));

        }

        return projectPath;
    }

    private <C extends IRuntimeComponent, E extends IRuntimeComponentExecutor> Pair<C, E> getRuntimeClientComponentEx(
        final IResolvableRuntimeInstallation resolvable, final InfobaseReference infobase, final String clientTypeId)
        throws CoreException
    {
        final IRuntimeComponentType componentType = runtimeComponentManager.getType(clientTypeId);
        if (componentType == null)
        {
            throw new IllegalArgumentException(
                MessageFormat.format("1C:Enterprise runtime component with id {0} not found", clientTypeId)); //$NON-NLS-1$
        }
        try
        {
            final RuntimeInstallation installation = resolvable
                .get(candidate -> runtimeComponentManager.hasComponent(candidate, new String[] { clientTypeId })
                    && matchesByArch(candidate, infobase), new ArchPriorityComparator(infobase));
            if (!runtimeComponentManager.supportsExecution(installation, clientTypeId))
            {
                final String message = MessageFormat.format(
                    Messages.RuntimeClientLaunchDelegate_Platform__0__has_no__1__component_installed,
                    installation.getName(), componentType.getName());
                throw new CoreException(LaunchingPlugin.createErrorStatus(message, 1000, (Throwable)null));
            }
            final Pair<C, E> componentExecutor =
                runtimeComponentManager.getComponentAndExecutor(installation, clientTypeId);
            if (componentExecutor == null)
            {
                throw new IllegalStateException(MessageFormat.format(
                    "No 1C:Enterprise runtime component executor with component type \"{0}\" is registered", //$NON-NLS-1$
                    clientTypeId));
            }
            return componentExecutor;
        }
        catch (MatchingRuntimeNotFound e)
        {
            final String message =
                MessageFormat.format(Messages.RuntimeClientLaunchDelegate_Platform__0__has_no__1__component_installed,
                    resolvable, componentType.getName());
            throw new CoreException(LaunchingPlugin.createErrorStatus(message, 1000, e));
        }
    }

    private IStatus launchInfobaseClientEx(final ILaunchConfiguration configuration, final ILaunch launch,
        final InfobaseReference infobase, final IResolvableRuntimeInstallation installation,
        final RuntimeExecutionArguments arguments, final Publication publication) throws CoreException
    {
        final String clientTypeId = ClientTypeSelectionSupport.getExecutionClientTypeId(configuration, installation,
            infobase, runtimeComponentManager);
        final boolean canRunRuntimeClient = runtimeComponentManager.getTypes(IRuntimeClientLauncher.class)
            .stream()
            .anyMatch(type -> clientTypeId.equals(type.getId()));
        try
        {
            if (!canRunRuntimeClient)
            {
                return launchUrlConnectionClientEx(configuration, launch, getLaunchUrlEx(infobase, publication),
                    installation, infobase, arguments);
            }
            final Pair<ILaunchableRuntimeComponent, IRuntimeClientLauncher> runtimeClient =
                getRuntimeClientComponentEx(installation, infobase, clientTypeId);
            final Pair<String, Process> clientProcess =
                runtimeClient.second.runClient(runtimeClient.first, infobase, arguments);
            DebugPlugin.newProcess(launch, clientProcess.second, formatProcessName(clientProcess.first),
                buildProcessArguments(configuration));
        }
        catch (CoreException e)
        {
            return e.getStatus();
        }
        catch (RuntimeExecutionException e2)
        {
            return LaunchingPlugin.createErrorStatus(Messages.RuntimeClientLaunchDelegate_Error_while_launching, e2);
        }
        return Status.OK_STATUS;
    }

    private IStatus launchUrlConnectionClientEx(final ILaunchConfiguration configuration, final ILaunch launch,
        final String launchUrl, final IResolvableRuntimeInstallation installation, final InfobaseReference infobase,
        final RuntimeExecutionArguments arguments) throws CoreException
    {
        final String clientTypeId = ClientTypeSelectionSupport.getExecutionClientTypeId(configuration, installation,
            (InfobaseReference)null, runtimeComponentManager);
        try
        {
            final Pair<IRuntimeComponent, IUrlOpenClientLauncher> uriOpenClient =
                getRuntimeComponent(installation, clientTypeId);
            final Pair<String, Process> clientProcess =
                uriOpenClient.second.runClient(uriOpenClient.first, launchUrl, infobase, arguments);
            if (clientProcess != null)
            {
                DebugPlugin.newProcess(launch, clientProcess.second, formatProcessName(clientProcess.first),
                    buildProcessArguments(configuration));
            }
            else
            {
                removeLaunch(launch);
            }
        }
        catch (CoreException e)
        {
            return e.getStatus();
        }
        catch (RuntimeExecutionException e2)
        {
            return LaunchingPlugin.createErrorStatus(Messages.RuntimeClientLaunchDelegate_Error_while_launching, e2);
        }
        return Status.OK_STATUS;
    }

    private void parseParamsTemplate(URL frameworkParamsUrl, ILaunchConfiguration configuration) throws IOException
    {
        String paramsFilePathName = FrameworkUtils.getConfigurationFilesPath(configuration);
        String projectPath = getProjectPath(configuration);
        String featuresPath = getFeaturesPath(configuration);

        String templateContent = readContents(getFileInputSupplier(frameworkParamsUrl));
        StringTemplate template = new StringTemplate(templateContent);
        template.setAttribute("ProjectPath", projectPath); //$NON-NLS-1$
        template.setAttribute("FeaturesPath", featuresPath); //$NON-NLS-1$
        template.setAttribute("JUnitPath", paramsFilePathName); //$NON-NLS-1$

        File paramsFilePath = new File(paramsFilePathName);
        if (!paramsFilePath.exists())
            paramsFilePath.mkdirs();

        try (FileOutputStream outputStream = new FileOutputStream(paramsFilePathName + FrameworkUtils.PARAMS_FILE_NAME);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);)

        {
            bufferedWriter.write(template.toString());

        }

    }

    private boolean saveParamsToFile(ILaunchConfiguration configuration)
    {
        Bundle bundle = FrameworkUtils.getFrameworkBundle();
        try
        {
            URL frameworkParamsBundleUrl = FileLocator.find(bundle,
                new Path(FrameworkUtils.FRAMEWORK_FILES_ROOT_PATH + FrameworkUtils.PARAMS_FILE_NAME), null);
            URL frameworkParamsUrl = FileLocator.toFileURL(frameworkParamsBundleUrl);

            if (frameworkParamsUrl == null)
            {
                String msg = MessageFormat.format(
                    Messages.RuntimeUnitLauncherLaunchDelegate_Failed_to_get_framework_params_from_bundle_0_1,
                    bundle.getSymbolicName(), FrameworkUtils.PARAMS_FILE_NAME);
                LaunchingPlugin.log(LaunchingPlugin.createErrorStatus(msg, new IOException()));
                return false;
            }
            File file = URIUtil.toFile(URIUtil.toURI(frameworkParamsUrl));

            if (!file.exists())
            {
                String msg = MessageFormat.format(
                    Messages.RuntimeUnitLauncherLaunchDelegate_Failed_to_read_framework_params_0, file.toString());
                LaunchingPlugin.log(LaunchingPlugin.createErrorStatus(msg, new IOException()));
                return false;
            }

            parseParamsTemplate(frameworkParamsUrl, configuration);

        }
        catch (IOException | URISyntaxException e)
        {
            String msg =
                MessageFormat.format(Messages.RuntimeUnitLauncherLaunchDelegate_Failed_to_save_framework_params_0,
                    FrameworkUtils.PARAMS_FILE_NAME);
            LaunchingPlugin.log(LaunchingPlugin.createErrorStatus(msg, e));
            return false;

        }

        return true;
    }

    @Override
    protected IStatus isValid(ILaunchConfiguration configuration, String mode) throws CoreException
    {
        IStatus parentStatus = super.isValid(configuration, mode);
        if (parentStatus != Status.OK_STATUS)
            return parentStatus;

        String extensionProjectToTest =
            configuration.getAttribute(UnitTestLaunchConfigurationAttributes.EXTENSION_PROJECT_TO_TEST, (String)null);
        boolean isExtensionValid = !Strings.isNullOrEmpty(extensionProjectToTest);

        String extensionModuleToTest =
            configuration.getAttribute(UnitTestLaunchConfigurationAttributes.EXTENSION_MODULE_TO_TEST, (String)null);
        boolean runModuleTests =
            configuration.getAttribute(UnitTestLaunchConfigurationAttributes.RUN_MODULE_TESTS, false);
        boolean isModuleValid = !Strings.isNullOrEmpty(extensionModuleToTest) || !runModuleTests;

        String extensionTagToTest =
            configuration.getAttribute(UnitTestLaunchConfigurationAttributes.EXTENSION_TAG_TO_TEST, (String)null);
        boolean runTagTests = configuration.getAttribute(UnitTestLaunchConfigurationAttributes.RUN_TAG_TESTS, false);
        boolean isTagValid = !Strings.isNullOrEmpty(extensionTagToTest) || !runTagTests;

        String externalObjectDumpPath =
            configuration.getAttribute(UnitTestLaunchConfigurationAttributes.EXTERNAL_OBJECT_DUMP_PATH, (String)null);
        String externalObjectStartupOptions = configuration
            .getAttribute(UnitTestLaunchConfigurationAttributes.EXTERNAL_OBJECT_STARTUP_OPTIONS, (String)null);
        boolean isExternalObjectValid =
            !Strings.isNullOrEmpty(externalObjectDumpPath) && !Strings.isNullOrEmpty(externalObjectStartupOptions);

        return isExtensionValid && isModuleValid && isTagValid && isExternalObjectValid ? Status.OK_STATUS
            : Status.CANCEL_STATUS;
    }

    private static class ArchPriorityComparator
        implements Comparator<RuntimeInstallation>
    {
        private final AppArch infobaseArch;

        ArchPriorityComparator(final InfobaseReference infobase)
        {
            infobaseArch = ((InfobaseReference)Preconditions.checkNotNull((Object)infobase)).getAppArch();
        }

        @Override
        public int compare(final RuntimeInstallation installation, final RuntimeInstallation other)
        {
            if (infobaseArch == AppArch.AUTO)
            {
                return 0;
            }
            final Arch installationArch = installation.getArch();
            final Arch otherArch = other.getArch();
            if (infobaseArch == AppArch.X86 || infobaseArch == AppArch.X86_PRIORITY)
            {
                return Boolean.compare(installationArch == Arch.X86, otherArch == Arch.X86);
            }
            if (infobaseArch == AppArch.X86_64 || infobaseArch == AppArch.X86_64_PRIORITY)
            {
                return Boolean.compare(installationArch == Arch.X86_64, otherArch == Arch.X86_64);
            }
            return 0;
        }
    }
}
