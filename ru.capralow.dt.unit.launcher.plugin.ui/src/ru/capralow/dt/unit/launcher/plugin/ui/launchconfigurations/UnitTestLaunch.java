package ru.capralow.dt.unit.launcher.plugin.ui.launchconfigurations;

import java.io.File;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.junit.JUnitCore;

import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;

import ru.capralow.dt.unit.launcher.plugin.ui.UnitLauncherPlugin;

public class UnitTestLaunch {

	public static void showJUnitResult(IProcess process, IV8ProjectManager projectManager) {
		if (process.getLabel().contains("dbgs"))
			return;

		try {
			ILaunchConfiguration launchConfiguration = process.getLaunch().getLaunchConfiguration();
			Map<String, Object> launchAttributes = launchConfiguration.getAttributes();
			Object externalObjectName = launchAttributes
					.get(com._1c.g5.v8.dt.debug.core.IDebugConfigurationAttributes.EXTERNAL_OBJECT_PROJECT_NAME);
			if (externalObjectName == null || !((String) externalObjectName).equalsIgnoreCase("ФреймворкТестирования"))
				return;

			IPath projectLocation = projectManager.getProject((String) externalObjectName).getProject().getLocation();

			File file = new File(projectLocation.toString() + "/junit.xml");

			JUnitCore.importTestRunSession(file);

		} catch (CoreException e) {
			UnitLauncherPlugin.createErrorStatus("Не удалось прочитать файл с результатом модульных тестов.", e);

		}
	}

	private UnitTestLaunch() {
		throw new IllegalStateException("Вспомогательный класс");
	}
}
