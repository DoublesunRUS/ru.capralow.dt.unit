package ru.capralow.dt.unit.launcher.plugin.core.frameworks;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.emf.common.util.URI;
import org.osgi.framework.Bundle;

import com._1c.g5.v8.dt.core.platform.IExtensionProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com.google.common.io.CharSource;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;
import com.google.gson.Gson;

import ru.capralow.dt.unit.launcher.plugin.core.UnitTestLaunchConfigurationAttributes;
import ru.capralow.dt.unit.launcher.plugin.core.frameworks.gson.FeatureFormat;
import ru.capralow.dt.unit.launcher.plugin.core.frameworks.gson.FeatureSettings;
import ru.capralow.dt.unit.launcher.plugin.core.frameworks.gson.FrameworkMetaTypes;
import ru.capralow.dt.unit.launcher.plugin.core.frameworks.gson.FrameworkSettings;
import ru.capralow.dt.unit.launcher.plugin.core.frameworks.gson.FrameworksList;
import ru.capralow.dt.unit.launcher.plugin.core.frameworks.gson.TestFramework;
import ru.capralow.dt.unit.launcher.plugin.internal.core.UnitLauncherCorePlugin;

public class FrameworkUtils {
	private static final String FRAMEWORK_PLUGIN = "ru.capralow.dt.unit.launcher.plugin.core"; //$NON-NLS-1$

	public static final String PARAMS_FILE_NAME = "params.json"; //$NON-NLS-1$
	public static final String FRAMEWORK_FILE_NAME = "framework.epf"; //$NON-NLS-1$

	public static final String FEATURE_EXTENSION = ".feature"; //$NON-NLS-1$

	public static final String MODULE_NAME = "$ModuleName$"; //$NON-NLS-1$

	public static String getConfigurationFilesPath(ILaunchConfiguration configuration) {
		Bundle bundle = getFrameworkBundle();
		IPath resourcePath = Platform.getStateLocation(bundle);
		return resourcePath + "/" + configuration.getName() + "/"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static IProject getConfigurationProject(ILaunchConfiguration configuration, IV8ProjectManager projectManager)
			throws CoreException {
		String extensionProjectName = configuration
				.getAttribute(UnitTestLaunchConfigurationAttributes.EXTENSION_PROJECT_TO_TEST, (String) null);

		Collection<IProject> projects = getExtensionProjects(projectManager);

		Iterator<IProject> itrProjects = projects.iterator();
		while (itrProjects.hasNext()) {
			IProject candidate = itrProjects.next();
			if (candidate.getName().equals(extensionProjectName))
				return candidate;
		}

		String msg = MessageFormat.format(Messages.FrameworkUtils_Extension_project_not_found_0, extensionProjectName);
		throw new CoreException(UnitLauncherCorePlugin.createErrorStatus(msg));
	}

	public static IProject getConfigurationProject(String extensionProjectName, IV8ProjectManager projectManager) {
		Collection<IProject> projects = getExtensionProjects(projectManager);

		Iterator<IProject> itrProjects = projects.iterator();
		while (itrProjects.hasNext()) {
			IProject candidate = itrProjects.next();
			if (candidate.getName().equals(extensionProjectName))
				return candidate;
		}

		return null;
	}

	public static TestFramework getCurrentFramework() {
		FrameworksList frameworks = getFrameworks();

		return frameworks.getList()[0];
	}

	public static IPath getExtensionProjectLocation(IProject project, IV8ProjectManager projectManager) {
		if (project == null || projectManager == null)
			return null;

		IV8Project v8Project = projectManager.getProject(project);
		if (!(v8Project instanceof IExtensionProject)) {
			String msg = MessageFormat.format(Messages.FrameworkUtils_Wrong_project_class_0, v8Project.getClass());
			throw new NullPointerException(msg);
		}

		return project.getLocation();
	}

	public static Collection<IProject> getExtensionProjects(IV8ProjectManager projectManager) {
		return projectManager.getProjects(IExtensionProject.class).stream().map(IV8Project::getProject)
				.collect(Collectors.toList());
	}

	public static List<String> getFeatureClientScript(FeatureSettings featureSettings, String lang, String moduleName,
			String methodName) {
		FeatureFormat frameworkFeature = featureSettings.getFeature(lang);

		List<String> elements = new ArrayList<>();
		for (String element : frameworkFeature.getClientScript())
			elements.add(element.replace(MODULE_NAME, moduleName).replace("$MethodName$", methodName)); //$NON-NLS-1$

		return elements;
	}

	public static List<String> getFeatureDescription(FeatureSettings featureSettings, String lang, String projectName,
			String moduleName) {
		FeatureFormat frameworkFeature = featureSettings.getFeature(lang);

		List<String> elements = new ArrayList<>();
		for (String element : frameworkFeature.getDescription())
			elements.add(element.replace("$ProjectName$", projectName).replace(MODULE_NAME, moduleName)); //$NON-NLS-1$

		return elements;
	}

	public static List<String> getFeatureServerScript(FeatureSettings featureSettings, String lang, String moduleName,
			String methodName) {
		FeatureFormat frameworkFeature = featureSettings.getFeature(lang);

		List<String> elements = new ArrayList<>();
		for (String element : frameworkFeature.getServerScript())
			elements.add(element.replace(MODULE_NAME, moduleName).replace("$MethodName$", methodName)); //$NON-NLS-1$

		return elements;
	}

	public static FeatureSettings getFeatureSettings() {
		TestFramework framework = getCurrentFramework();
		String jsonContent = readContents(getFileInputSupplier(framework.getResourcePath() + "feature.json")); //$NON-NLS-1$

		FeatureSettings featureSettings = new Gson().fromJson(jsonContent, FeatureSettings.class);

		for (FeatureFormat feature : featureSettings.getFeatureFormat())
			featureSettings.setFeature(feature.getLang(), feature);

		return featureSettings;
	}

	public static Bundle getFrameworkBundle() {
		return Platform.getBundle(FRAMEWORK_PLUGIN);
	}

	public static FrameworkMetaTypes getFrameworkMetaTypes() {
		TestFramework framework = getCurrentFramework();
		String jsonContent = readContents(getFileInputSupplier(framework.getResourcePath() + "metaTypes.json")); //$NON-NLS-1$

		return new Gson().fromJson(jsonContent, FrameworkMetaTypes.class);
	}

	public static FrameworksList getFrameworks() {
		String jsonContent = readContents(getFileInputSupplier("/frameworks/frameworks.json")); //$NON-NLS-1$

		return new Gson().fromJson(jsonContent, FrameworksList.class);
	}

	public static FrameworkSettings getFrameworkSettings() {
		TestFramework framework = getCurrentFramework();
		String jsonContent = readContents(getFileInputSupplier(framework.getResourcePath() + "settings.json")); //$NON-NLS-1$

		return new Gson().fromJson(jsonContent, FrameworkSettings.class);
	}

	public static String getFrameworkStartupOptions(FrameworkSettings frameworkSettings, String paramsFilePathName) {
		String startupOptions = frameworkSettings.getStartupOptions();
		return startupOptions.replace("$ParamsFilePathName$", paramsFilePathName + PARAMS_FILE_NAME); //$NON-NLS-1$
	}

	public static String getModuleByName(String extensionModuleName, IProject project,
			IV8ProjectManager projectManager) {
		if (project == null)
			return null;

		IPath projectLocation = getExtensionProjectLocation(project, projectManager);

		List<String> modules = getTestModules(projectLocation);
		if (modules.isEmpty())
			return null;

		if (modules.contains(extensionModuleName))
			return modules.get(modules.indexOf(extensionModuleName));

		return null;
	}

	public static String getModuleFromConfiguration(ILaunchConfiguration configuration,
			IV8ProjectManager projectManager) throws CoreException {

		IProject project = getConfigurationProject(configuration, projectManager);

		String extensionModuleName = configuration
				.getAttribute(UnitTestLaunchConfigurationAttributes.EXTENSION_MODULE_TO_TEST, (String) null);

		return getModuleByName(extensionModuleName, project, projectManager);
	}

	public static URI getResourceURIforPlugin(String fileName) {
		URI uri = URI.createPlatformResourceURI(fileName, false);

		File file = getResourceFile(uri);

		return URI.createFileURI(file.getPath());
	}

	public static String getTagByName(String extensionTagName, IProject project, IV8ProjectManager projectManager) {
		if (project == null)
			return null;

		IPath projectLocation = getExtensionProjectLocation(project, projectManager);

		List<String> tags = getTestTags(projectLocation);
		if (tags.isEmpty())
			return null;

		if (tags.contains(extensionTagName))
			return tags.get(tags.indexOf(extensionTagName));

		return null;
	}

	public static String getTagFromConfiguration(ILaunchConfiguration configuration, IV8ProjectManager projectManager)
			throws CoreException {
		IProject project = getConfigurationProject(configuration, projectManager);

		String extensionTagName = configuration
				.getAttribute(UnitTestLaunchConfigurationAttributes.EXTENSION_TAG_TO_TEST, (String) null);

		return getTagByName(extensionTagName, project, projectManager);
	}

	public static List<String> getTestModules(IPath projectLocation) {
		if (projectLocation == null)
			return new ArrayList<>();

		String featuresPath = projectLocation + "/features/all/"; //$NON-NLS-1$
		File featuresDir = new File(featuresPath);
		File[] featureFiles = featuresDir.listFiles((dir1, name) -> name.endsWith(FEATURE_EXTENSION)); // $NON-NLS-1$
		if (featureFiles == null)
			return new ArrayList<>();

		List<String> modulesList = new ArrayList<>();

		for (File featureFile : featureFiles)
			modulesList.add(featureFile.getName().substring(0, featureFile.getName().lastIndexOf(FEATURE_EXTENSION)));

		return modulesList;
	}

	public static List<String> getTestTags(IPath projectLocation) {
		if (projectLocation == null)
			return new ArrayList<>();

		String featuresPath = projectLocation + "/features/"; //$NON-NLS-1$

		File featuresDir = new File(featuresPath);
		File[] featureFiles = featuresDir
				.listFiles((dir1, name) -> dir1.isDirectory() && !name.equalsIgnoreCase("all")); //$NON-NLS-1$
		if (featureFiles == null)
			return new ArrayList<>();

		List<String> tagsList = new ArrayList<>();

		for (File featureFile : featureFiles)
			tagsList.add(featureFile.getName());

		return tagsList;
	}

	private static CharSource getFileInputSupplier(String partName) {
		return Resources.asCharSource(UnitTestLaunchConfigurationAttributes.class.getResource(partName),
				StandardCharsets.UTF_8);
	}

	private static File getResourceFile(URI uri) {
		String[] segments = uri.segments();
		IPath resourcePath = UnitLauncherCorePlugin.getDefault().getStateLocation();
		for (Integer i = 1; i < segments.length - 1; ++i) {
			resourcePath = resourcePath.append(segments[i]);
			File file = resourcePath.toFile();
			if (file.exists() && !file.isDirectory()) {
				try {
					Files.delete(file.toPath());

				} catch (IOException e) {
					String msg = MessageFormat.format(Messages.FrameworkUtils_Unable_to_delete_framework_file_0,
							file.toPath());
					UnitLauncherCorePlugin.log(UnitLauncherCorePlugin.createErrorStatus(msg, e));

				}
			} else if (!file.exists()) {
				file.mkdir();
			}
		}
		resourcePath = resourcePath.append(segments[segments.length - 1]);
		return resourcePath.toFile();
	}

	private static String readContents(CharSource source) {
		try (Reader reader = source.openBufferedStream()) {
			return CharStreams.toString(reader);

		} catch (IOException | NullPointerException e) {
			return ""; //$NON-NLS-1$

		}
	}

	private FrameworkUtils() {
		throw new IllegalStateException(Messages.FrameworkUtils_Internal_class);
	}
}
