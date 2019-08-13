package ru.capralow.dt.unit.launcher.plugin.internal.ui.junit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Strings;

import ru.capralow.dt.unit.launcher.plugin.core.UnitTestLaunchConfigurationAttributes;
import ru.capralow.dt.unit.launcher.plugin.core.frameworks.FrameworkUtils;
import ru.capralow.dt.unit.launcher.plugin.internal.ui.UnitLauncherUiPlugin;

public class ShowJUnitResult implements IDebugEventSetListener {

	public static final String JUNIT_PANEL_ID = "org.eclipse.jdt.junit.ResultView"; //$NON-NLS-1$

	private static void showJUnitResult(IProcess process) {
		if (process.getLabel().contains("dbgs")) //$NON-NLS-1$
			return;

		try {
			ILaunchConfiguration configuration = process.getLaunch().getLaunchConfiguration();

			String extensionProjectName = configuration
					.getAttribute(UnitTestLaunchConfigurationAttributes.EXTENSION_PROJECT_TO_TEST, (String) null);
			if (Strings.isNullOrEmpty(extensionProjectName))
				return;

			String paramsFilePathName = FrameworkUtils.getConfigurationFilesPath(configuration);

			File file = new File(paramsFilePathName + File.separator + "junit.xml"); //$NON-NLS-1$
			if (!file.exists()) {
				String msg = MessageFormat.format(Messages.UnitTestLaunch_Unable_to_find_junit_xml_file_0,
						file.getPath());
				UnitLauncherUiPlugin.log(UnitLauncherUiPlugin.createErrorStatus(msg));
				return;
			}

			JUnitCore.importTestRunSession(file);

			Display.getDefault().asyncExec(() -> {
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(JUNIT_PANEL_ID);

				} catch (PartInitException e) {
					String msg = MessageFormat.format(Messages.UnitTestLaunch_Unable_to_show_panel_0, JUNIT_PANEL_ID);
					UnitLauncherUiPlugin.log(UnitLauncherUiPlugin.createErrorStatus(msg, e));

				}
			});

			Files.deleteIfExists(file.toPath());

		} catch (CoreException | IOException e) {
			UnitLauncherUiPlugin.log(
					UnitLauncherUiPlugin.createErrorStatus(Messages.UnitTestLaunch_Unable_to_read_junit_xml_file, e));

		}
	}

	@Override
	public void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent event : events) {
			Object source = event.getSource();
			if (event.getKind() == DebugEvent.TERMINATE && source instanceof IProcess)
				showJUnitResult((IProcess) source);
		}
	}

}
