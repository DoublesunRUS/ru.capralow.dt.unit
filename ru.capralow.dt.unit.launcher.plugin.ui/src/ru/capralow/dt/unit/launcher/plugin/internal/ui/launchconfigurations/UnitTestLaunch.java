package ru.capralow.dt.unit.launcher.plugin.internal.ui.launchconfigurations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
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

public class UnitTestLaunch {

	public static void showJUnitResult(IProcess process) {
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
				UnitLauncherUiPlugin.log(
						UnitLauncherUiPlugin.createErrorStatus(Messages.UnitTestLaunch_Unable_to_get_framework_path));
				return;
			}

			JUnitCore.importTestRunSession(file);

			Display.getDefault().asyncExec(() -> {
				String panelId = "org.eclipse.jdt.junit.ResultView"; //$NON-NLS-1$
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(panelId);

				} catch (PartInitException e) {
					String msg = MessageFormat.format(Messages.UnitTestLaunch_Unable_to_show_junit_panel_0, panelId);
					UnitLauncherUiPlugin.log(UnitLauncherUiPlugin.createErrorStatus(msg, e));

				}
			});

			Files.deleteIfExists(file.toPath());

		} catch (CoreException | IOException e) {
			UnitLauncherUiPlugin.log(
					UnitLauncherUiPlugin.createErrorStatus(Messages.UnitTestLaunch_Unable_to_read_junit_xml_file, e));

		}
	}

	private UnitTestLaunch() {
		throw new IllegalStateException(Messages.UnitTestLaunch_Internal_class);
	}
}
