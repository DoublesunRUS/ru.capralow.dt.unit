/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.launching;

import java.util.Set;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.emf.common.util.URI;

/**
 * Extension of the {@link ILaunch} interface to keep specific information for
 * coverage launches.
 */
public interface ICoverageLaunch
    extends ILaunch
{

    /**
     * Returns the collection of {@link URI} considered as the scope for this
     * launch.
     *
     * @return package fragment roots for this launch
     */
    Set<URI> getScope();
}
