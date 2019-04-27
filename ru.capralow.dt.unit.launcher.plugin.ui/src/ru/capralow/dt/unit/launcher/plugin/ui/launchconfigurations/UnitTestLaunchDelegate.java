package ru.capralow.dt.unit.launcher.plugin.ui.launchconfigurations;

import java.io.File;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import com._1c.g5.v8.dt.common.Pair;
import com._1c.g5.v8.dt.core.platform.IConfigurationProject;
import com._1c.g5.v8.dt.core.platform.IExternalObjectProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.debug.core.model.IRuntimeDebugClientTarget;
import com._1c.g5.v8.dt.launching.core.DebugSessionAlreadyStartedResponse;
import com._1c.g5.v8.dt.launching.core.PublicationResult;
import com._1c.g5.v8.dt.launching.core.launchconfigurations.AbstractRuntimeDebugAwareLaunchDelegate;
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
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import ru.capralow.dt.unit.launcher.plugin.ui.UnitLauncherPlugin;

public class UnitTestLaunchDelegate extends AbstractRuntimeDebugAwareLaunchDelegate {
	@Inject
	private IExternalObjectDumpSupport externalObjectDumpSupport;
	@Inject
	private IInfobaseAccessManager infobaseAccessManager;
	@Inject
	private IPublicationManager publicationManager;
	@Inject
	private IResolvableRuntimeInstallationManager resolvableRuntimeInstallationManager;
	@Inject
	private IRuntimeComponentManager runtimeComponentManager;

	public void doLaunch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		IProject project = getProject(configuration).getProject();
		String launchUrl = getLaunchUrl(configuration);
		InfobaseReference infobase = getInfobase(configuration);
		IResolvableRuntimeInstallation resolvable = getInstallation(configuration);
		boolean deployBeforeLaunch = configuration
				.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_DEPLOY_BEFORE_LAUNCH", true);
		boolean deployFull = configuration
				.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_DEPLOY_FULL_CONFIGURATION", false);
		boolean associateAfterDeploy = configuration
				.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_ASSOCIATE_AFTER_DEPLOY", false);

		monitor.worked(1);
		IStatus infobaseUpdateStatus;
		if (launchUrl == null) {
			IStatus infobaseExistenceStatus;
			if ("debug".equals(mode)) {
				infobaseExistenceStatus = checkDebugSession(project, launch);
				if (!infobaseExistenceStatus.isOK()) {
					handleErrorStatus(infobaseExistenceStatus, monitor, launch);
					return;
				}

				monitor.worked(1);
			}

			infobaseExistenceStatus = checkInfobaseExistence(infobase, monitor);
			if (!infobaseExistenceStatus.isOK()) {
				handleErrorStatus(infobaseExistenceStatus, SubMonitor.convert(monitor), launch);
				return;
			}

			monitor.worked(1);
			if (deployBeforeLaunch) {
				infobaseUpdateStatus = updateInfobaseDatabase(project,
						infobase,
						deployFull,
						associateAfterDeploy,
						SubMonitor.convert(monitor));
				if (!infobaseUpdateStatus.isOK()) {
					handleErrorStatus(infobaseUpdateStatus, monitor, launch);
					return;
				}

				monitor.worked(1);
			}

			if (monitor.isCanceled()) {
				return;
			}

			Publication publication = null;
			boolean needPublication = configuration
					.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_NEED_INFOBASE_PUBLICATION", false);
			IStatus launchStatus;
			if (needPublication) {
				PublicationResult publishResult = publishInfobase(infobase, resolvable, monitor);
				launchStatus = publishResult.getStatus();
				publication = publishResult.getPublication();
				if (!launchStatus.isOK()) {
					handleErrorStatus(launchStatus, monitor, launch);
					return;
				}

				monitor.worked(1);
			}

			if (monitor.isCanceled()) {
				return;
			}

			RuntimeExecutionArguments arguments = buildExecutionArguments(configuration, infobase, monitor);
			if ("debug".equals(mode)) {
				String debugServerUrl = attachDebugTarget(configuration, launch, resolvable, monitor);
				monitor.worked(1);
				arguments.setDebugServerUrl(debugServerUrl);
			}

			launchStatus = launchInfobaseClient(configuration, launch, infobase, resolvable, arguments, publication);
			if (!launchStatus.isOK()) {
				handleErrorStatus(launchStatus, monitor, launch);
				return;
			}

			if (associateAfterDeploy) {
				associate(project, infobase, deployBeforeLaunch);
			}
		} else {
			RuntimeExecutionArguments arguments = buildExecutionArguments(configuration, infobase, monitor);
			if ("debug".equals(mode)) {
				infobaseUpdateStatus = checkDebugSession(project, launch);
				if (!infobaseUpdateStatus.isOK()) {
					return;
				}

				String debugServerUrl = attachDebugTarget(configuration, launch, resolvable, monitor);
				monitor.worked(1);
				arguments.setDebugServerUrl(debugServerUrl);
			}

			infobaseUpdateStatus = launchUrlConnectionClient(configuration,
					launch,
					launchUrl,
					resolvable,
					infobase,
					arguments);
			if (!infobaseUpdateStatus.isOK()) {
				handleErrorStatus(infobaseUpdateStatus, monitor, launch);
			}
		}

	}

	@Override
	protected IStatus isValid(ILaunchConfiguration configuration, String mode) throws CoreException {
		IV8Project project = getProject(configuration);
		if (!(project instanceof IConfigurationProject))
			return UnitLauncherPlugin.createErrorStatus(Messages.RuntimeClientLaunchDelegate_Incorrect_project, 1001);

		InfobaseReference infobase = getInfobase(configuration);
		String launchUrl = getLaunchUrl(configuration);
		if (infobase == null && launchUrl == null)
			return UnitLauncherPlugin.createErrorStatus(Messages.RuntimeClientLaunchDelegate_Incorrect_infobase, 1001);

		IResolvableRuntimeInstallation resolvable = getInstallation(configuration);
		if (resolvable == null)
			return UnitLauncherPlugin.createErrorStatus(Messages.RuntimeClientLaunchDelegate_Incorrect_runtime, 1001);

		String externalProjectName = configuration
				.getAttribute("com._1c.g5.v8.dt.debug.core.ATTR_EXTERNAL_OBJECT_PROJECT_NAME", (String) null);
		String externalObjectName = configuration.getAttribute("com._1c.g5.v8.dt.debug.core.ATTR_EXTERNAL_OBJECT_NAME",
				(String) null);
		String externalObjectType = configuration.getAttribute("com._1c.g5.v8.dt.debug.core.ATTR_EXTERNAL_OBJECT_TYPE",
				(String) null);
		IV8Project v8project = externalProjectName == null ? null : getProject(externalProjectName);
		if (v8project instanceof IExternalObjectProject && externalObjectType != null) {
			IExternalObjectProject externalObjectProject = (IExternalObjectProject) v8project;
			EObject externalObject = ExternalObjectHelper
					.getExternalObject(externalObjectProject, externalObjectName, externalObjectType);
			if (externalObjectName != null && externalObject == null)
				return UnitLauncherPlugin
						.createErrorStatus(Messages.RuntimeClientLaunchDelegate_Incorrect_external_object, 1001);

		}

		try {
			RuntimeInstallation installation = resolvable.get();
			String clientTypeId = configuration.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_CLIENT_TYPE",
					(String) null);
			if (clientTypeId != null && !runtimeComponentManager.supportsExecution(installation, clientTypeId)) {
				String message = MessageFormat.format(
						Messages.RuntimeClientLaunchDelegate_1C_Enterprise__0__has_no__1__installed,
						installation.getName(),
						runtimeComponentManager.getType(clientTypeId).getName());
				return UnitLauncherPlugin.createErrorStatus(message, 1000);
			}
			return Status.OK_STATUS;

		} catch (MatchingRuntimeNotFound e) {
			return createMatchingRuntimeNotFoundErrorStatus(1001, e);
		}
	}

	private IResolvableRuntimeInstallation getInstallation(ILaunchConfiguration configuration) throws CoreException {
		String resolvableAsString = configuration.getAttribute("com._1c.g5.v8.dt.debug.core.ATTR_RUNTIME_INSTALLATION",
				(String) null);
		return resolvableAsString == null ? null : resolvableRuntimeInstallationManager.deserialize(resolvableAsString);
	}

	private String getLaunchUrl(ILaunchConfiguration configuration) throws CoreException {
		return configuration.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_LAUNCH_URL", (String) null);
	}

	private String getLaunchUrl(InfobaseReference infobase, Publication publication) throws CoreException {
		if (infobase.getInfobaseType() == InfobaseType.WEB) {
			WebServerConnectionString connectionString = (WebServerConnectionString) infobase.getConnectionString();
			return connectionString.getUrl();
		}

		if (publication != null) {
			try {
				return publicationManager.getPublicationUrl(publication.getWebServer(), publication.getName());

			} catch (WebServerAccessException e) {
				throw new CoreException(UnitLauncherPlugin
						.createErrorStatus(Messages.RuntimeClientLaunchDelegate_Error_while_launching, e));
			}
		}
		throw new CoreException(UnitLauncherPlugin.createErrorStatus("Publication not provided for web client launch"));
	}

	private IStatus launchInfobaseClient(ILaunchConfiguration configuration, ILaunch launch, InfobaseReference infobase,
			IResolvableRuntimeInstallation installation, RuntimeExecutionArguments arguments, Publication publication)
			throws CoreException {
		String clientTypeId = ClientTypeSelectionSupport
				.getExecutionClientTypeId(configuration, installation, infobase, runtimeComponentManager);
		boolean canRunRuntimeClient = runtimeComponentManager.getTypes(IRuntimeClientLauncher.class).stream()
				.anyMatch(type -> clientTypeId.equals(type.getId()));

		try {
			if (!canRunRuntimeClient)
				return launchUrlConnectionClient(configuration,
						launch,
						getLaunchUrl(infobase, publication),
						installation,
						infobase,
						arguments);

			Pair<ILaunchableRuntimeComponent, IRuntimeClientLauncher> runtimeClient = getRuntimeClientComponent(
					installation,
					infobase,
					clientTypeId);
			Pair<String, Process> clientProcess = (runtimeClient.second)
					.runClient((ILaunchableRuntimeComponent) runtimeClient.first, infobase, arguments);
			DebugPlugin.newProcess(launch,
					(Process) clientProcess.second,
					formatProcessName((String) clientProcess.first),
					buildProcessArguments(configuration));
		} catch (CoreException e) {
			return e.getStatus();

		} catch (RuntimeExecutionException e2) {
			return UnitLauncherPlugin.createErrorStatus(Messages.RuntimeClientLaunchDelegate_Error_while_launching, e2);

		}

		return Status.OK_STATUS;
	}

	private <C extends IRuntimeComponent, E extends IRuntimeComponentExecutor> Pair<C, E> getRuntimeClientComponent(
			IResolvableRuntimeInstallation resolvable, InfobaseReference infobase, String clientTypeId)
			throws CoreException {
		IRuntimeComponentType componentType = runtimeComponentManager.getType(clientTypeId);
		if (componentType == null)
			throw new IllegalArgumentException(
					MessageFormat.format("1C:Enterprise runtime component with id {0} not found", clientTypeId));

		String message;
		try {
			RuntimeInstallation installation = resolvable
					.get(candidate -> runtimeComponentManager.hasComponent(candidate, clientTypeId)
							&& matchesByArch(candidate, infobase), new ArchPriorityComparator(infobase));

			if (!runtimeComponentManager.supportsExecution(installation, clientTypeId)) {
				message = MessageFormat.format(
						Messages.RuntimeClientLaunchDelegate_Platform__0__has_no__1__component_installed,
						installation.getName(),
						componentType.getName());
				throw new CoreException(UnitLauncherPlugin.createErrorStatus(message, 1000, (Throwable) null));
			}

			Pair<C, E> componentExecutor = runtimeComponentManager.getComponentAndExecutor(installation, clientTypeId);
			if (componentExecutor == null)
				throw new IllegalStateException(MessageFormat.format(
						"No 1C:Enterprise runtime component executor with component type \"{0}\" is registered",
						clientTypeId));

			return componentExecutor;

		} catch (MatchingRuntimeNotFound e) {
			message = MessageFormat.format(
					Messages.RuntimeClientLaunchDelegate_Platform__0__has_no__1__component_installed,
					resolvable,
					componentType.getName());
			throw new CoreException(UnitLauncherPlugin.createErrorStatus(message, 1000, (Throwable) null));

		}
	}

	private boolean matchesByArch(RuntimeInstallation installation, InfobaseReference infobase) {
		AppArch infobaseArch = infobase.getAppArch();
		Arch installationArch = installation.getArch();
		if (infobaseArch == AppArch.X86 && installationArch == Arch.X86_64)
			return false;

		return infobaseArch != AppArch.X86_64 || installationArch == Arch.X86_64;
	}

	private IStatus launchUrlConnectionClient(ILaunchConfiguration configuration, ILaunch launch, String launchUrl,
			IResolvableRuntimeInstallation installation, InfobaseReference infobase,
			RuntimeExecutionArguments arguments) throws CoreException {
		String clientTypeId = ClientTypeSelectionSupport.getExecutionClientTypeId(configuration,
				installation,
				(InfobaseReference) null,
				runtimeComponentManager);

		try {
			Pair<IRuntimeComponent, IUrlOpenClientLauncher> uriOpenClient = getRuntimeComponent(installation,
					clientTypeId);
			Pair<String, Process> clientProcess = (uriOpenClient.second)
					.runClient((IRuntimeComponent) uriOpenClient.first, launchUrl, infobase, arguments);
			if (clientProcess != null) {
				DebugPlugin.newProcess(launch,
						(Process) clientProcess.second,
						formatProcessName((String) clientProcess.first),
						buildProcessArguments(configuration));
			} else {
				removeLaunch(launch);
			}

		} catch (CoreException e) {
			return e.getStatus();

		} catch (RuntimeExecutionException e2) {
			return UnitLauncherPlugin.createErrorStatus(Messages.RuntimeClientLaunchDelegate_Error_while_launching, e2);

		}

		return Status.OK_STATUS;
	}

	private IStatus checkDebugSession(IProject project, ILaunch launch) throws CoreException {
		List<ILaunch> sameProject = new ArrayList<>();
		ILaunch[] launches = DebugPlugin.getDefault().getLaunchManager().getLaunches();

		for (int length = launches.length, i = 0; i < length; ++i) {
			ILaunch candidate = launches[length];
			if (candidate != launch && !candidate.isTerminated() && "debug".equals(candidate.getLaunchMode())) {
				IDebugTarget debugTarget = candidate.getDebugTarget();
				if (debugTarget instanceof IRuntimeDebugClientTarget
						&& Objects.equals(project, debugTarget.getAdapter(IProject.class))) {
					sameProject.add(candidate);
				}
			}
		}

		if (sameProject.isEmpty())
			return Status.OK_STATUS;

		IStatus debugSessionAlreadyStarted = UnitLauncherPlugin
				.createErrorStatus(Messages.RuntimeClientLaunchDelegate_Debug_session_already_started, 1003);
		DebugSessionAlreadyStartedResponse response = handleStatus(debugSessionAlreadyStarted, project);

		switch (response) {
		case RESTART_APPLICATION:
			for (ILaunch candidate2 : sameProject) {
				if (!candidate2.isTerminated()) {
					try {
						candidate2.terminate();
					} catch (DebugException e) {
						UnitLauncherPlugin.log(UnitLauncherPlugin.createErrorStatus(e.getMessage(), (Throwable) e));
					}
				}
			}

			return Status.OK_STATUS;

		case LAUNCH_ANYWAY:
			return Status.OK_STATUS;

		case CANCEL:
			return Status.CANCEL_STATUS;

		default:
			throw new IllegalStateException(MessageFormat.format("Unexpected result \"{0}\"", response));

		}
	}

	private String formatProcessName(String processName) {
		String timestamp = DateFormat.getDateTimeInstance(2, 2).format(new Date());
		return String.format("%s (%s)", processName, timestamp);
	}

	private RuntimeExecutionArguments buildExecutionArguments(ILaunchConfiguration configuration,
			InfobaseReference infobase, IProgressMonitor monitor) throws CoreException {
		RuntimeExecutionArguments arguments = new RuntimeExecutionArguments();
		boolean useInfobaseAccessUser = configuration
				.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_LAUNCH_USER_USE_INFOBASE_ACCESS", false);
		if (useInfobaseAccessUser && infobase != null) {
			IInfobaseAccessSettings settings = infobaseAccessManager.getSettings(infobase);
			arguments.setAccess(settings.access());
			arguments.setUsername(settings.userName());
			arguments.setPassword(settings.password());
		} else {
			InfobaseAccess access = configuration
					.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_LAUNCH_OS_INFOBASE_ACCESS", true)
							? InfobaseAccess.OS
							: InfobaseAccess.INFOBASE;
			arguments.setAccess(access);
			if (access == InfobaseAccess.INFOBASE) {
				arguments.setUsername(configuration
						.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_LAUNCH_USER_NAME", (String) null));
				arguments.setPassword(configuration
						.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_LAUNCH_USER_PASSWORD", (String) null));
			}
		}

		arguments.setDataSeparation(
				configuration.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_DATA_SEPARATION", (String) null));
		arguments.setStartupOption(
				configuration.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_STARTUP_OPTION", (String) null));
		arguments.setSlowConnection(
				configuration.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_SLOW_CONNECTION", false));
		arguments.setEmulateDelay(
				configuration.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_EMULATE_DELAY", false));
		arguments.setCallDelay(
				getDouble(configuration.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_CALL_DELAY", 0)));
		arguments.setDataSendingDelay(
				getDouble(configuration.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_DATA_SENDING_DELAY", 0)));
		arguments.setDataReceivingDelay(
				getDouble(configuration.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_DATA_RECEIVING_DELAY", 0)));
		arguments.setDisableStartupMessages(
				configuration.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_DO_NOT_DISPLAY_WARNINGS", false));
		arguments.setDisplayPerformance(
				configuration.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_SHOW_PERFORMANCE", false));
		arguments.setDisplayAllFunctions(
				configuration.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_SHOW_ALL_FUNCTIONS", false));
		String logFile = configuration.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_LOG_FILE", (String) null);
		if (logFile != null)
			arguments.setLogFile(new File(logFile));

		arguments.setClear(configuration.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_DO_NOT_CLEAR_LOG", false));
		String sessionLocale = configuration.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_SESSION_LOCALE",
				(String) null);
		if (sessionLocale != null)
			arguments.setSessionLocale(LocaleUtil.createLocale(sessionLocale));

		String interfaceLanguage = configuration.getAttribute("com._1c.g5.v8.dt.launching.core.ATTR_INTERFACE_LANGUAGE",
				(String) null);
		if (interfaceLanguage != null)
			arguments.setInterfaceLanguage(interfaceLanguage);

		String externalObjectProjectName = configuration
				.getAttribute("com._1c.g5.v8.dt.debug.core.ATTR_EXTERNAL_OBJECT_PROJECT_NAME", (String) null);
		IV8Project v8project = externalObjectProjectName == null ? null : getProject(externalObjectProjectName);
		if (v8project instanceof IExternalObjectProject) {
			IExternalObjectProject externalObjectProject = (IExternalObjectProject) v8project;
			if (externalObjectDumpSupport.isEnabled(externalObjectProject.getProject())) {
				String externalObjectName = configuration
						.getAttribute("com._1c.g5.v8.dt.debug.core.ATTR_EXTERNAL_OBJECT_NAME", (String) null);
				String externalObjectType = configuration
						.getAttribute("com._1c.g5.v8.dt.debug.core.ATTR_EXTERNAL_OBJECT_TYPE", (String) null);
				EObject externalObject = ExternalObjectHelper
						.getExternalObject(externalObjectProject, externalObjectName, externalObjectType);
				if (externalObject != null) {
					arguments.setExternalObjectDumpPath(externalObjectDumpSupport
							.getDump(externalObjectProject.getProject(), externalObject, true, monitor));
				} else {
					UnitLauncherPlugin.log(UnitLauncherPlugin.createErrorStatus(MessageFormat.format(
							Messages.RuntimeClientLaunchDelegate_External_object_with_name__0__not_found_in_project__1,
							externalObjectName,
							externalObjectProjectName)));
				}
			} else {
				UnitLauncherPlugin.log(UnitLauncherPlugin.createErrorStatus(MessageFormat.format(
						Messages.RuntimeClientLaunchDelegate_External_dump_generation_is_disabled_for_project__0,
						externalObjectProjectName)));
			}

		}

		return arguments;
	}

	private double getDouble(int intRepresentedDouble) {
		return (double) intRepresentedDouble / 100.0D;
	}

	private Map<String, String> buildProcessArguments(ILaunchConfiguration configuration) throws CoreException {
		return Maps.transformValues(configuration.getAttributes(), input -> input.toString());
	}

	private static class ArchPriorityComparator implements Comparator<RuntimeInstallation> {
		private AppArch infobaseArch;

		public ArchPriorityComparator(InfobaseReference infobase) {
			this.infobaseArch = ((InfobaseReference) Preconditions.checkNotNull((Object) infobase)).getAppArch();
		}

		@Override
		public int compare(final RuntimeInstallation installation, final RuntimeInstallation other) {
			if (this.infobaseArch == AppArch.AUTO) {
				return 0;
			}
			final Arch installationArch = installation.getArch();
			final Arch otherArch = other.getArch();
			if (this.infobaseArch == AppArch.X86 || this.infobaseArch == AppArch.X86_PRIORITY) {
				return Boolean.compare(installationArch == Arch.X86, otherArch == Arch.X86);
			}
			if (this.infobaseArch == AppArch.X86_64 || this.infobaseArch == AppArch.X86_64_PRIORITY) {
				return Boolean.compare(installationArch == Arch.X86_64, otherArch == Arch.X86_64);
			}
			return 0;
		}
	}

}