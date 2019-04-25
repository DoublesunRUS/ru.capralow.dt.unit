package ru.capralow.dt.unit.launcher.plugin.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription.Delta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.core.platform.IConfigurationProject;
import com._1c.g5.v8.dt.core.platform.IExtensionProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.mcore.util.Environment;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com.google.inject.Inject;

public class UnitLauncherXtextBuilderParticipant implements org.eclipse.xtext.builder.IXtextBuilderParticipant {
	private static final Logger LOGGER = LoggerFactory.getLogger(UnitLauncherXtextBuilderParticipant.class);

	public static String getFeaturesLocation(IPath projectLocation) {
		return projectLocation + "/features/";
	}

	public static String getUnitTestKeyFromMethodText(String methodText) {
		String[] methodLines = methodText.split("\\r?\\n");
		for (String methodLine : methodLines) {
			if (methodLine.toLowerCase().contains("@unit-test")) {
				String keyName = methodLine.substring(methodLine.toLowerCase().indexOf("@unit-test") + 10);
				if (!keyName.isEmpty() && keyName.startsWith(":"))
					keyName = keyName.substring(1).split("[ ]")[0];
				else
					keyName = "";

				return keyName;
			}
		}

		return null;
	}

	public static void saveFeatures(String keyName, List<String> methodsNames, IPath projectLocation, String moduleName,
			Boolean forServer, Boolean forClient) {
		StringBuilder fileText = new StringBuilder();
		fileText.append(String.join(System.lineSeparator(),
				"# language: ru",
				"",
				"@tree",
				"",
				"Функционал: Модульное тестирование 1С",
				"	Как Разработчик",
				"	Я Хочу чтобы возвращаемое значение метода совпадало с эталонным",
				"	Чтобы я мог гарантировать работоспособность метода"));
		if (forServer) {
			fileText.append(System.lineSeparator());
			fileText.append(System.lineSeparator());
			fileText.append(String.join(System.lineSeparator(),
					String.format("Сценарий: %1$s (сервер)", moduleName),
					"	И я выполняю код встроенного языка на сервере"));
			for (String methodName : methodsNames) {
				fileText.append(System.lineSeparator());
				fileText.append(String.format("	| '%1$s(Объект);' |", methodName));
			}
		}
		if (forClient) {
			fileText.append(System.lineSeparator());
			fileText.append(System.lineSeparator());
			fileText.append(String.join(System.lineSeparator(),
					String.format("Сценарий: %1$s (клиент)", moduleName),
					"	И я выполняю код встроенного языка"));
			for (String methodName : methodsNames) {
				fileText.append(System.lineSeparator());
				fileText.append(String.format("	| '%1$s(Форма);' |", methodName));
			}
		}

		String featuresPathName = getFeaturesLocation(projectLocation);
		if (!keyName.isEmpty())
			featuresPathName += keyName.concat("/");

		File featuresPath = new File(featuresPathName);
		if (!featuresPath.exists())
			featuresPath.mkdirs();

		String fileName = String.format("%1$s/%2$s.feature", featuresPathName, moduleName);

		try (FileOutputStream outputStream = new FileOutputStream(fileName);
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
				BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);)

		{
			bufferedWriter.write(fileText.toString());

		} catch (IOException e) {
			String msg = String.format("Не удалось записать feature файл: \"%1$s\"", fileName);
			LOGGER.error(msg, e);

		}
	}

	private static Module getCommonModule(Delta delta, Configuration configuration) {
		EObject object = null;

		Iterator<IEObjectDescription> objectItr = delta.getNew().getExportedObjects().iterator();
		if (objectItr.hasNext())
			object = objectItr.next().getEObjectOrProxy();

		if (object == null) {
			String msg = String.format("Не найден объект конфигурации: \"%1$s\"", "");
			LOGGER.error(msg);
			return null;
		}

		if (!(object instanceof Module))
			return null;

		Module module = (Module) EcoreUtil.resolve(object, configuration);
		if (!(module.getModuleType().equals(ModuleType.COMMON_MODULE)))
			return null;

		return module;
	}

	@Inject
	private IV8ProjectManager projectManager;

	@Override
	public void build(IBuildContext context, IProgressMonitor monitor) throws CoreException {
		IProject project = context.getBuiltProject();
		IV8Project v8Project = projectManager.getProject(project);

		Configuration configuration = null;
		if (v8Project instanceof IConfigurationProject)
			configuration = ((IConfigurationProject) v8Project).getConfiguration();

		else if (v8Project instanceof IExtensionProject)
			configuration = ((IExtensionProject) v8Project).getConfiguration();

		List<Delta> deltas = context.getDeltas();
		for (Delta delta : deltas) {
			Module module = getCommonModule(delta, configuration);
			if (module == null)
				continue;

			Map<String, List<String>> units = new HashMap<>();
			for (Method method : module.allMethods()) {
				String keyName = getUnitTestKeyFromMethodText(NodeModelUtils.findActualNodeFor(method).getText());
				if (keyName == null)
					continue;

				List<String> methodsNames = units.get(keyName);
				if (methodsNames == null)
					methodsNames = new ArrayList<>();
				methodsNames.add(method.getName());
				units.put(keyName, methodsNames);
			}

			Boolean forServer = module.getEnvironments().contains(Environment.SERVER);
			Boolean forClient = module.getEnvironments().contains(Environment.THIN_CLIENT);

			String moduleName = ((CommonModule) module.getOwner()).getName();
			deleteModuleFeatures(project.getLocation(), moduleName);
			for (Entry<String, List<String>> entry : units.entrySet())
				saveFeatures(entry.getKey(), entry.getValue(), project.getLocation(), moduleName, forServer, forClient);
			deleteEmptyDirs(project.getLocation(), moduleName);
		}

	}

	private void deleteEmptyDirs(IPath projectLocation, String moduleName) {
		Path pathToBeDeleted = Paths.get(getFeaturesLocation(projectLocation));

		try (Stream<Path> files = Files.walk(pathToBeDeleted);) {
			files.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(file -> {
				if (file.isDirectory() && file.list().length == 0)
					try {
						Files.delete(file.toPath());
					} catch (IOException e) {
						String msg = String.format("Не удалось удалить пустые каталоги: \"%1$s\"", moduleName);
						LOGGER.error(msg);
					}
			});

		} catch (IOException e) {
			String msg = String.format("Не удалось удалить пустые каталоги: \"%1$s\"", moduleName);
			LOGGER.error(msg);

		}
	}

	private void deleteModuleFeatures(IPath projectLocation, String moduleName) {
		Path dirPath = Paths.get(getFeaturesLocation(projectLocation));

		try (Stream<Path> files = Files.walk(dirPath);) {
			files.map(Path::toFile).sorted(Comparator.comparing(File::isDirectory)).forEach(file -> {
				if (file.getName().endsWith(moduleName + ".feature"))
					try {
						Files.delete(file.toPath());
					} catch (IOException e) {
						String msg = String.format("Не удалось удалить файлы: \"%1$s\"", moduleName);
						LOGGER.error(msg);
					}
			});

		} catch (IOException e) {
			String msg = String.format("Не удалось удалить файлы: \"%1$s\"", moduleName);
			LOGGER.error(msg);

		}

	}

}
