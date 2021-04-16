/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;

import com._1c.g5.v8.dt.profiling.core.IProfilingResult;

/**
 * API for importing sessions. This interface is not intended to be implemented
 * by clients. Use {@link CoverageTools#getImporter()} to get an instance.
 */
public interface ISessionImporter
{

    /**
     * A call to this method triggers the actual import process.
     *
     * @param monitor progress monitor
     * @throws CoreException if something goes wrong during export
     */
    void importSession(IProgressMonitor monitor) throws CoreException;

    /**
     * Sets the description for the imported session.
     *
     * @param description textual description of the session
     */
    void setDescription(String description);

    /**
     * Sets the source for execution data.
     *
     * @param source profiling results
     */

    void setProfilingResult(IProfilingResult source);

    /**
     * Sets the set of package fragment roots that should be considered for coverage
     * analysis.
     *
     * @param scope scope for analysis
     */
    void setScope(Set<URI> scope);

}
