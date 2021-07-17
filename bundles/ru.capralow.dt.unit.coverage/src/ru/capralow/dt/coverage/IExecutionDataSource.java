/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage;

import org.eclipse.core.runtime.CoreException;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;

/**
 * Common interface for all sources of execution data.
 */
public interface IExecutionDataSource
{

    /**
     * Emits all stored execution data in the given visitors.
     *
     * @param executionDataVisitor visitor for execution data
     * @param visitor for session information
     */
    void accept(IExecutionDataVisitor executionDataVisitor, ISessionInfoVisitor sessionInfoVisitor)
        throws CoreException;

}
