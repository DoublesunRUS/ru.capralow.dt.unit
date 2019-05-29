package ru.capralow.dt.internal.launching.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import com._1c.g5.v8.dt.internal.launching.core.launchconfigurations.RuntimeClientLaunchDelegate;

public class RuntimeUnitLauncherLaunchDelegate extends RuntimeClientLaunchDelegate {

	@Override
	public void doLaunch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {

		super.doLaunch(configuration, mode, launch, monitor);
	}

	@Override
	protected IStatus isValid(ILaunchConfiguration configuration, String mode) throws CoreException {

		return super.isValid(configuration, mode);
	}

}
