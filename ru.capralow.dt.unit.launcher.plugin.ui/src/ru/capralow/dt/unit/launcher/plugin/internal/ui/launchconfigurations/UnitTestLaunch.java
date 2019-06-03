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

import ru.capralow.dt.unit.launcher.plugin.core.UnitTestLaunchConfigurationAttributes;
import ru.capralow.dt.unit.launcher.plugin.core.frameworks.FrameworkUtils;
import ru.capralow.dt.unit.launcher.plugin.internal.ui.UnitLauncherUiPlugin;

public class UnitTestLaunch {

	public static void showJUnitResult(IProcess process) {
		if (process.getLabel().contains("dbgs"))
			return;

		try {
			String frameworkName = process.getLaunch().getLaunchConfiguration()
					.getAttribute(UnitTestLaunchConfigurationAttributes.FRAMEWORK, "");
			if (frameworkName.isEmpty())
				return;

			ILaunchConfiguration configuration = process.getLaunch().getLaunchConfiguration();

			String paramsFilePathName = FrameworkUtils.getConfigurationFilesPath(configuration);

			File file = new File(paramsFilePathName + File.separator + "junit.xml");
			if (!file.exists()) {
				String msg = "Не удалось определить путь к фреймворку тестирования.";
				UnitLauncherUiPlugin.log(UnitLauncherUiPlugin.createErrorStatus(msg));
				return;
			}

			JUnitCore.importTestRunSession(file);

			Display.getDefault().asyncExec(() -> {
				String panelId = "org.eclipse.jdt.junit.ResultView";
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(panelId);

				} catch (PartInitException e) {
					String msg = MessageFormat.format("Не удалось отобразить панель {0}.", panelId);
					UnitLauncherUiPlugin.log(UnitLauncherUiPlugin.createErrorStatus(msg, e));

				}
			});

			Files.deleteIfExists(file.toPath());

		} catch (CoreException | IOException e) {
			String msg = "Не удалось прочитать файл с результатом модульных тестов.";
			UnitLauncherUiPlugin.log(UnitLauncherUiPlugin.createErrorStatus(msg, e));

		}
	}

	private UnitTestLaunch() {
		throw new IllegalStateException("Вспомогательный класс");
	}
}
