/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.emf.common.util.URI;

import ru.capralow.dt.coverage.ICorePreferences;

/**
 * Utility to calculate the default scope for a given launch configuration.
 */
public class DefaultScopeFilter
{

    private ICorePreferences preferences;

    /**
     * Creates a new filter based on the given preferences.
     *
     * @param preferences call-back to retrieve current settings from.
     */
    public DefaultScopeFilter(ICorePreferences preferences)
    {
        this.preferences = preferences;
    }

    /**
     * Returns a filtered copy of the given {@link IClassFiles} set.
     *
     * @param classfiles {@link IClassFiles} to filter
     * @param configuration context information
     * @return filtered set
     * @throws CoreException may occur when accessing the Java model
     */
    public Set<URI> filter(Set<URI> all, ILaunchConfiguration configuration) throws CoreException
    {
        Set<URI> filtered = new HashSet<>(all);
        // if (preferences.getDefaultScopeSameProjectOnly()) {
        // sameProjectOnly(filtered, configuration);
        // }
        // String filter = preferences.getDefaultScopeFilter();
        // if (filter != null && filter.length() > 0) {
        // matchingPathsOnly(filtered, filter);
        // }
        return filtered;
    }

    // private void sameProjectOnly(Collection<Module> filtered, final
    // ILaunchConfiguration configuration)
    // throws CoreException {
    // final IJavaProject javaProject = JavaRuntime.getJavaProject(configuration);
    // if (javaProject != null) {
    // for (final Iterator<Module> i = filtered.iterator(); i.hasNext();) {
    // if (!javaProject.equals(i.next().getJavaProject())) {
    // i.remove();
    // }
    // }
    // }
    // }
    //
    // private void matchingPathsOnly(Collection<Module> filtered, final String
    // filter) {
    // final String[] matchStrings = filter.split(","); //$NON-NLS-1$
    // for (final Iterator<Module> i = filtered.iterator(); i.hasNext();) {
    // if (!isPathMatch(i.next(), matchStrings)) {
    // i.remove();
    // }
    // }
    // }
    //
    // private boolean isPathMatch(final Module root, final String[] matchStrings) {
    // final String path = root.getPath().toString();
    // for (final String match : matchStrings) {
    // if (path.indexOf(match) != -1) {
    // return true;
    // }
    // }
    // return false;
    // }

}
