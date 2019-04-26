package ru.capralow.dt.unit.launcher.plugin.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.swt.program.Program;

public class UnitLauncherLaunchConfigurationDelegate extends LaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		String attribute = configuration.getAttribute(UnitLauncherLaunchConfigurationAttributes.CONSOLE_TEXT, "");
		Program.launch(attribute);
	}

}
