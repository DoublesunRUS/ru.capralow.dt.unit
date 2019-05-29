package ru.capralow.dt.internal.launching.core;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.osgi.framework.Bundle;

import com._1c.g5.v8.dt.internal.launching.core.LaunchingPlugin;
import com._1c.g5.v8.dt.internal.launching.core.launchconfigurations.RuntimeClientLaunchDelegate;

import ru.capralow.dt.unit.launcher.plugin.core.frameworks.FrameworkUtils;
import ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.TestFramework;

public class RuntimeUnitLauncherLaunchDelegate extends RuntimeClientLaunchDelegate {

	@Override
	public void doLaunch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {

		TestFramework framework = FrameworkUtils.getFrameworkFromConfiguration(configuration,
				FrameworkUtils.getFrameworks());

		Bundle bundle = Platform.getBundle("ru.capralow.dt.unit.launcher.plugin.core");
		try {
			URL frameworkURL = FileLocator
					.toFileURL(bundle.getEntry(framework.getResourcePath() + framework.getEpfName()));

		} catch (IOException e) {
			String msg = MessageFormat.format(Messages.RuntimeUnitLauncherLaunchDelegate_Failed_to_save_framework_0,
					framework.getEpfName());
			LaunchingPlugin.log(LaunchingPlugin.createErrorStatus(msg, e));
		}

		super.doLaunch(configuration, mode, launch, monitor);
	}

	@Override
	protected IStatus isValid(ILaunchConfiguration configuration, String mode) throws CoreException {

		return super.isValid(configuration, mode);
	}

}
