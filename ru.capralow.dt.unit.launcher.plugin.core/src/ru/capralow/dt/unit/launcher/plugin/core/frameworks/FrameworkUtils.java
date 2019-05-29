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
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.emf.common.util.URI;

import com.google.common.io.CharSource;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;

import ru.capralow.dt.unit.launcher.plugin.core.UnitTestLaunchConfigurationAttributes;
import ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.TestFramework;
import ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.ulFactory;
import ru.capralow.dt.unit.launcher.plugin.internal.core.UnitLauncherCorePlugin;

public class FrameworkUtils {

	public static TestFramework getFrameworkFromConfiguration(ILaunchConfiguration configuration,
			Collection<TestFramework> frameworks) throws CoreException {
		if (frameworks == null)
			return null;

		TestFramework framework = null;
		Iterator<TestFramework> itrFrameworks = frameworks.iterator();
		while (itrFrameworks.hasNext()) {
			TestFramework candidate = itrFrameworks.next();
			if (candidate.getName()
					.equals(configuration.getAttribute(UnitTestLaunchConfigurationAttributes.FRAMEWORK, ""))) {

				framework = candidate;
			}
		}

		return framework;
	}

	public static Collection<TestFramework> getFrameworks() {
		ArrayList<TestFramework> newFrameworks = new ArrayList<>();

		String frameworksContents = readContents(getFileInputSupplier("frameworks.txt"));

		for (String frameworkString : frameworksContents.split(System.lineSeparator())) {
			String[] frameworkList = frameworkString.split("[,]");

			TestFramework framework = ulFactory.eINSTANCE.createTestFramework();
			framework.setName(frameworkList[0]);
			framework.setVersion(frameworkList[1]);
			framework.setResourcePath("frameworks" + File.separator + frameworkList[2] + File.separator);
			framework.setEpfName(frameworkList[3]);

			newFrameworks.add(framework);
		}

		return newFrameworks.stream().collect(Collectors.toList());
	}

	private static CharSource getFileInputSupplier(String partName) {
		return Resources.asCharSource(
				UnitTestLaunchConfigurationAttributes.class.getResource("/frameworks/" + partName),
				StandardCharsets.UTF_8);
	}

	public static URI getResourceURIforPlugin(String fileName) {
		URI uri = URI.createPlatformResourceURI(fileName, false);

		File file = getResourceFile(uri);

		return URI.createFileURI(file.getPath());
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
			return "";

		}
	}

	private FrameworkUtils() {
		throw new IllegalStateException(Messages.FrameworkUtils_Internal_class);
	}
}
