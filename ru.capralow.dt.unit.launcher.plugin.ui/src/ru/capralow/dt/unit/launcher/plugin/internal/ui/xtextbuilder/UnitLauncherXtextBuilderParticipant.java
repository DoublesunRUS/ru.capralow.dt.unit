package ru.capralow.dt.unit.launcher.plugin.internal.ui.xtextbuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
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
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescription.Delta;

import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.core.platform.IConfigurationProject;
import com._1c.g5.v8.dt.core.platform.IExtensionProject;
import com._1c.g5.v8.dt.core.platform.IExternalObjectProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.mcore.util.Environment;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com.google.inject.Inject;

import ru.capralow.dt.unit.launcher.plugin.internal.ui.UnitLauncherUiPlugin;

public class UnitLauncherXtextBuilderParticipant implements org.eclipse.xtext.builder.IXtextBuilderParticipant {
	public static String getFeaturesLocation(IPath projectLocation) {
		return projectLocation + "/features/"; //$NON-NLS-1$
	}

	public static String getUnitTestKeyFromMethodText(String methodText) {
		String[] methodLines = methodText.split("\\r?\\n"); //$NON-NLS-1$
		for (String methodLine : methodLines) {
			if (methodLine.toLowerCase().contains("@unit-test")) { //$NON-NLS-1$
				String keyName = methodLine.substring(methodLine.toLowerCase().indexOf("@unit-test") + 10); //$NON-NLS-1$
				if (!keyName.isEmpty() && keyName.startsWith(":")) //$NON-NLS-1$
					keyName = keyName.substring(1).split("[ ]")[0]; //$NON-NLS-1$
				else
					keyName = ""; //$NON-NLS-1$

				return keyName;
			}
		}

		return null;
	}

	public static void saveFeatures(String keyName, List<String> methodsNames, IPath projectLocation, String moduleName,
			String moduleSynonym, Boolean forServer, Boolean forClient) {
		StringBuilder fileText = new StringBuilder();
		fileText.append(String.join(System.lineSeparator(),
				"# language: ru", //$NON-NLS-1$
				"", //$NON-NLS-1$
				"@tree", //$NON-NLS-1$
				"@classname=ModuleExceptionPath", //$NON-NLS-1$
				"", //$NON-NLS-1$
				String.format("Функционал: %1$s", moduleSynonym), //$NON-NLS-1$
				"	Как Разработчик", //$NON-NLS-1$
				"	Я Хочу чтобы возвращаемое значение метода совпадало с эталонным", //$NON-NLS-1$
				"	Чтобы я мог гарантировать работоспособность метода")); //$NON-NLS-1$
		for (String methodName : methodsNames) {
			if (forServer) {
				fileText.append(System.lineSeparator());
				fileText.append(System.lineSeparator());
				fileText.append(String.join(System.lineSeparator(),
						"&OnServer",
						String.format("Сценарий: %1$s (сервер): %2$s", moduleName, methodName), //$NON-NLS-1$
						"	И я выполняю код встроенного языка на сервере")); //$NON-NLS-1$
				fileText.append(System.lineSeparator());
				fileText.append(String.format("	| '%1$s.%2$s(Объект());' |", moduleName, methodName)); //$NON-NLS-1$
			}
		}
		for (String methodName : methodsNames) {
			if (forClient) {
				fileText.append(System.lineSeparator());
				fileText.append(System.lineSeparator());
				fileText.append(String.join(System.lineSeparator(),
						String.format("Сценарий: %1$s (клиент): %2$s", moduleName, methodName), //$NON-NLS-1$
						"	И я выполняю код встроенного языка")); //$NON-NLS-1$
				fileText.append(System.lineSeparator());
				fileText.append(String.format("	| '%1$s.%2$s(Ванесса);' |", moduleName, methodName)); //$NON-NLS-1$
			}
		}

		String featuresPathName = getFeaturesLocation(projectLocation);
		if (!keyName.isEmpty())
			featuresPathName += keyName.concat("/"); //$NON-NLS-1$

		File featuresPath = new File(featuresPathName);
		if (!featuresPath.exists())
			featuresPath.mkdirs();

		String fileName = String.format("%1$s/%2$s.feature", featuresPathName, moduleName); //$NON-NLS-1$

		try (FileOutputStream outputStream = new FileOutputStream(fileName);
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
				BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);)

		{
			bufferedWriter.write(fileText.toString());

		} catch (IOException e) {
			String msg = MessageFormat
					.format(Messages.UnitLauncherXtextBuilderParticipant_Error_while_saving_feature_file_0, fileName);
			UnitLauncherUiPlugin.log(UnitLauncherUiPlugin.createErrorStatus(msg, e));

		}
	}

	private static Module getCommonModule(Delta delta, Configuration configuration) {
		EObject object = null;

		IResourceDescription deltaDescription = delta.getNew();
		if (deltaDescription == null)
			return null;

		Iterator<IEObjectDescription> objectItr = deltaDescription.getExportedObjects().iterator();
		if (objectItr.hasNext()) {
			IEObjectDescription objectDescription = objectItr.next();
			object = objectDescription.getEObjectOrProxy();

			if (object == null) {
				String msg = MessageFormat.format(
						Messages.UnitLauncherXtextBuilderParticipant_Unable_to_find_configuration_object_0,
						objectDescription.getName());
				UnitLauncherUiPlugin.log(UnitLauncherUiPlugin.createErrorStatus(msg));
				return null;
			}
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

		Configuration configuration = getConfigurationFromProject(v8Project);

		if (configuration == null) {
			String msg = MessageFormat.format(
					Messages.UnitLauncherXtextBuilderParticipant_Unable_to_get_configuration_from_base_project_0,
					v8Project);
			UnitLauncherUiPlugin.log(UnitLauncherUiPlugin.createErrorStatus(msg));
			return;
		}

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

			CommonModule commonModule = (CommonModule) module.getOwner();

			String moduleName = commonModule.getName();
			String moduleSynonym = commonModule.getSynonym().get("ru"); //$NON-NLS-1$
			deleteModuleFeatures(project.getLocation(), moduleName);
			for (Entry<String, List<String>> entry : units.entrySet())
				saveFeatures(entry.getKey(),
						entry.getValue(),
						project.getLocation(),
						moduleName,
						moduleSynonym,
						forServer,
						forClient);
		}
		deleteEmptyDirs(project.getLocation());

	}

	private void deleteEmptyDirs(IPath projectLocation) {
		Path pathToBeDeleted = Paths.get(getFeaturesLocation(projectLocation));
		if (!pathToBeDeleted.toFile().exists())
			return;

		try (Stream<Path> files = Files.walk(pathToBeDeleted);) {
			files.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(file -> {
				if (file.exists() && file.isDirectory() && file.list().length == 0)
					try {
						Files.delete(file.toPath());
					} catch (IOException e) {
						String msg = MessageFormat.format(
								Messages.UnitLauncherXtextBuilderParticipant_Unable_to_delete_empty_folder_0,
								file.toPath());
						UnitLauncherUiPlugin.log(UnitLauncherUiPlugin.createErrorStatus(msg, e));
					}
			});

		} catch (IOException e) {
			String msg = MessageFormat.format(
					Messages.UnitLauncherXtextBuilderParticipant_Unable_to_delete_empty_folders_for_project_0,
					pathToBeDeleted);
			UnitLauncherUiPlugin.log(UnitLauncherUiPlugin.createErrorStatus(msg, e));

		}
	}

	private void deleteModuleFeatures(IPath projectLocation, String moduleName) {
		Path dirPath = Paths.get(getFeaturesLocation(projectLocation));
		if (!dirPath.toFile().exists())
			return;

		try (Stream<Path> files = Files.walk(dirPath);) {
			files.map(Path::toFile).sorted(Comparator.comparing(File::isDirectory)).forEach(file -> {
				String fileName = moduleName + ".feature"; //$NON-NLS-1$
				if (file.exists() && file.getName().endsWith(fileName))
					try {
						Files.delete(file.toPath());
					} catch (IOException e) {
						String msg = MessageFormat.format(
								Messages.UnitLauncherXtextBuilderParticipant_Unable_to_delete_feature_file_0,
								fileName);
						UnitLauncherUiPlugin.log(UnitLauncherUiPlugin.createErrorStatus(msg, e));
					}
			});

		} catch (IOException e) {
			String msg = MessageFormat.format(
					Messages.UnitLauncherXtextBuilderParticipant_Unable_to_delete_feature_files_for_module_0,
					moduleName);
			UnitLauncherUiPlugin.log(UnitLauncherUiPlugin.createErrorStatus(msg, e));

		}

	}

	private Configuration getConfigurationFromProject(IV8Project v8Project) {
		Configuration configuration = null;
		if (v8Project instanceof IConfigurationProject) {
			configuration = ((IConfigurationProject) v8Project).getConfiguration();

		} else if (v8Project instanceof IExtensionProject) {
			configuration = ((IExtensionProject) v8Project).getConfiguration();

		} else if (v8Project instanceof IExternalObjectProject) {
			IConfigurationProject parent = ((IExternalObjectProject) v8Project).getParent();
			if (parent == null)
				return null;
			configuration = parent.getConfiguration();
		}

		return configuration;
	}

}
