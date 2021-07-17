/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.launching;

import ru.capralow.dt.coverage.internal.CoverageCorePlugin;

/**
 * Constants for coverage specific launch configuration entries.
 */
public final class ICoverageLaunchConfigurationConstants
{

    /**
     * List of Java element ids pointing to package fragment roots that form the
     * scope of a coverage launch. If unspecified a default scope is calculated
     * based on the launch type and preferences..
     */
    public static final String ATTR_SCOPE_IDS = CoverageCorePlugin.ID + ".SCOPE_IDS"; //$NON-NLS-1$

    private ICoverageLaunchConfigurationConstants()
    {
        // nothing to do
    }

}
