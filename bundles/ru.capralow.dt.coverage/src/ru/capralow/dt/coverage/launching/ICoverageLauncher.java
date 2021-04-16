/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.launching;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.emf.common.util.URI;

/**
 * The launch delegate for coverage configurations.
 */
public interface ICoverageLauncher
    extends ILaunchConfigurationDelegate2
{

    /**
     * Determines all {@link IPackageFragmentRoot}s that are part of the given
     * launch configuration.
     *
     * @param configuration launch configuration to determine overall scope
     *
     * @return overall scope as set of {@link IPackageFragmentRoot} elements
     *
     * @throws CoreException
     */
    Set<URI> getOverallScope(ILaunchConfiguration configuration) throws CoreException;

}
