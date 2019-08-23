package ru.capralow.dt.eclemma.core.launching;

import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.eclemma.core.launching.CoverageLauncher;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;

import ru.capralow.dt.unit.launcher.plugin.core.frameworks.FrameworkUtils;

public class BslApplicationLauncher extends CoverageLauncher {

	@Inject
	IV8ProjectManager projectManager;

	@Override
	public Set<IPackageFragmentRoot> getOverallScope(ILaunchConfiguration configuration) throws CoreException {
		IProject project = FrameworkUtils.getConfigurationProject(configuration, projectManager);
		if (project == null)
			return Collections.emptySet();

		return Collections.emptySet();
	}

}
