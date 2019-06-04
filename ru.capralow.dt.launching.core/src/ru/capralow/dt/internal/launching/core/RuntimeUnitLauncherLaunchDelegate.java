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
import java.text.MessageFormat;

import org.antlr.stringtemplate.StringTemplate;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.osgi.framework.Bundle;

import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.internal.launching.core.LaunchingPlugin;
import com._1c.g5.v8.dt.internal.launching.core.launchconfigurations.RuntimeClientLaunchDelegate;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com.google.common.io.CharSource;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;
import com.google.inject.Inject;

import ru.capralow.dt.unit.launcher.plugin.core.UnitTestLaunchConfigurationAttributes;
import ru.capralow.dt.unit.launcher.plugin.core.frameworks.FrameworkUtils;
import ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.TestFramework;

public class RuntimeUnitLauncherLaunchDelegate extends RuntimeClientLaunchDelegate {

	private static CharSource getFileInputSupplier(URL resourceURL) {
		return Resources.asCharSource(resourceURL, StandardCharsets.UTF_8);
	}

	private static void parseParamsTemplate(URL frameworkParamsURL, ILaunchConfiguration configuration,
			IV8ProjectManager projectManager) throws IOException {
		String paramsFilePathName = FrameworkUtils.getConfigurationFilesPath(configuration);

		String featuresPath = "";
		try {
			Boolean runModuleTests = configuration.getAttribute(UnitTestLaunchConfigurationAttributes.RUN_MODULE_TESTS,
					false);
			IProject project = FrameworkUtils.getConfigurationProject(configuration, projectManager);
			CommonModule commonModule = FrameworkUtils.getConfigurationModule(configuration, projectManager);

			featuresPath = project.getLocation() + "/features/";
			if (runModuleTests)
				featuresPath += commonModule.getName() + ".feature";

		} catch (CoreException e) {
			LaunchingPlugin.log(LaunchingPlugin
					.createErrorStatus(Messages.RuntimeUnitLauncherLaunchDelegate_Incorrect_launch_configuration, e));

		}

		String templateContent = readContents(getFileInputSupplier(frameworkParamsURL));
		StringTemplate template = new StringTemplate(templateContent);
		template.setAttribute("FeaturesPath", featuresPath);
		template.setAttribute("JUnitPath", paramsFilePathName);

		File paramsFilePath = new File(paramsFilePathName);
		if (!paramsFilePath.exists())
			paramsFilePath.mkdirs();

		try (FileOutputStream outputStream = new FileOutputStream(paramsFilePathName + "params.json");
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
				BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);)

		{
			bufferedWriter.write(template.toString());

		}

	}

	private static String readContents(CharSource source) {
		try (Reader reader = source.openBufferedStream()) {
			return CharStreams.toString(reader);

		} catch (IOException | NullPointerException e) {
			LaunchingPlugin.log(LaunchingPlugin.createErrorStatus(e.getMessage(), e));
			return "";

		}
	}

	private static void saveParamsToFile(ILaunchConfiguration configuration, IV8ProjectManager projectManager)
			throws CoreException {
		TestFramework framework = FrameworkUtils.getFrameworkFromConfiguration(configuration,
				FrameworkUtils.getFrameworks());

		Bundle bundle = FrameworkUtils.getFrameworkBundle();
		try {
			URL frameworkParamsURL = FileLocator
					.toFileURL(bundle.getEntry(framework.getResourcePath() + "params.json"));
			if (frameworkParamsURL == null) {
				String msg = MessageFormat.format(
						Messages.RuntimeUnitLauncherLaunchDelegate_Failed_to_get_framework_params_from_bundle_0_1,
						bundle.getSymbolicName(),
						framework.getResourcePath() + "params.json");
				LaunchingPlugin.log(LaunchingPlugin.createErrorStatus(msg, new IOException()));
				return;
			}
			File file = URIUtil.toFile(URIUtil.toURI(frameworkParamsURL));

			if (!file.exists()) {
				String msg = MessageFormat.format(
						Messages.RuntimeUnitLauncherLaunchDelegate_Failed_to_read_framework_params_0,
						file.toString());
				LaunchingPlugin.log(LaunchingPlugin.createErrorStatus(msg, new IOException()));
				return;
			}

			parseParamsTemplate(frameworkParamsURL, configuration, projectManager);

		} catch (IOException | URISyntaxException e) {
			String msg = MessageFormat.format(
					Messages.RuntimeUnitLauncherLaunchDelegate_Failed_to_save_framework_params_0,
					framework.getResourcePath() + "params.json");
			LaunchingPlugin.log(LaunchingPlugin.createErrorStatus(msg, e));

		}

	}

	@Inject
	private IV8ProjectManager projectManager;

	@Override
	public void doLaunch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {

		saveParamsToFile(configuration, projectManager);

		super.doLaunch(configuration, mode, launch, monitor);
	}

	@Override
	protected IStatus isValid(ILaunchConfiguration configuration, String mode) throws CoreException {

		return super.isValid(configuration, mode);
	}

}
