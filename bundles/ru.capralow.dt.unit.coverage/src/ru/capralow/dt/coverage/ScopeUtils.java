/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.emf.common.util.URI;

import com._1c.g5.v8.dt.bsl.model.Module;

import ru.capralow.dt.coverage.internal.CoverageCorePlugin;
import ru.capralow.dt.coverage.internal.DefaultScopeFilter;
import ru.capralow.dt.coverage.launching.ICoverageLaunchConfigurationConstants;
import ru.capralow.dt.coverage.launching.ICoverageLauncher;

/**
 * Collection of utility methods to deal with analysis scope.
 */
public final class ScopeUtils
{

    /**
     * Remove all JRE runtime entries from the given set
     *
     * @param scope set to filter
     * @return filtered set without JRE runtime entries
     */
    public static Set<URI> filterUnsupportedEntries(Collection<URI> scope)
    {
        Set<URI> filtered = new HashSet<>();
        for (URI root : scope)
        {
            // final IClasspathEntry entry = root.getRawClasspathEntry();
            // switch (entry.getEntryKind()) {
            // case IClasspathEntry.CPE_SOURCE:
            // case IClasspathEntry.CPE_LIBRARY:
            // case IClasspathEntry.CPE_VARIABLE:
            // filtered.add(root);
            // break;
            // case IClasspathEntry.CPE_CONTAINER:
            // IClasspathContainer container =
            // JavaCore.getClasspathContainer(entry.getPath(), root.getJavaProject());
            // if (container != null && container.getKind() ==
            // IClasspathContainer.K_APPLICATION) {
            // filtered.add(root);
            // }
            // break;
            // }
        }
        return filtered;
    }

    /**
     * Returns the scope configured with the given configuration. If no scope has
     * been explicitly defined, the default filter settings are applied to the
     * overall scope.
     *
     * @param configuration launch configuration to read scope from
     *
     * @return configured scope
     */
    public static Set<URI> getConfiguredScope(ILaunchConfiguration configuration) throws CoreException
    {
        Set<URI> all = getOverallScope(configuration);
        @SuppressWarnings("rawtypes")
        List<?> selection =
            configuration.getAttribute(ICoverageLaunchConfigurationConstants.ATTR_SCOPE_IDS, (List)null);
        if (selection == null)
        {
            DefaultScopeFilter filter = new DefaultScopeFilter(CoverageCorePlugin.getInstance().getPreferences());
            return filter.filter(all, configuration);

        }

        all.retainAll(readScope(selection));
        return all;
    }

    /**
     * Determines all {@link URI}s that potentially referenced by a given launch
     * configuration.
     *
     * @param configuration launch configuration to determine overall scope
     *
     * @return overall scope
     */
    public static Set<URI> getOverallScope(ILaunchConfiguration configuration) throws CoreException
    {
        ICoverageLauncher launcher =
            (ICoverageLauncher)configuration.getType().getDelegates(Collections.singleton(CoverageTools.LAUNCH_MODE))[0]
                .getDelegate();
        return launcher.getOverallScope(configuration);
    }

    /**
     * Determines all package fragment roots in the workspace.
     *
     * @return all package fragment roots
     */
    public static Set<URI> getWorkspaceScope()
    {
        Set<URI> scope = new HashSet<>();
        // IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
        // for (IJavaProject p : model.getJavaProjects()) {
        // // scope.addAll(Arrays.asList(p.getPackageFragmentRoots()));
        // }
        return filterUnsupportedEntries(scope);
    }

    /**
     * Reads a coverage scope from a collection of element ids.
     *
     * @param ids List of {@link String} ids
     * @return scope as {@link URI} collection
     */
    public static Set<URI> readScope(Collection<?> ids)
    {
        Set<URI> scope = new HashSet<>();
        for (Object handle : ids)
        {
            URI moduleUri = URI.createURI((String)handle);
            scope.add(moduleUri);
        }
        return scope;
    }

    /**
     * Writes a coverage scope as a list of ids.
     *
     * @param scope Scope as {@link Module} collection
     * @return List of ids
     */
    public static List<String> writeScope(Set<URI> scope)
    {
        List<String> ids = new ArrayList<>();
        for (URI root : scope)
        {
            ids.add(root.toString());
        }
        return ids;
    }

    private ScopeUtils()
    {
    }

}
