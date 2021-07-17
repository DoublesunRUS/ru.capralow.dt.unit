/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;

import com._1c.g5.v8.dt.profiling.core.IProfilingResult;

import ru.capralow.dt.coverage.ICoverageSession;
import ru.capralow.dt.coverage.ISessionImporter;
import ru.capralow.dt.coverage.ISessionManager;

/**
 * Implementation of ISessionImporter.
 */
public class SessionImporter
    implements ISessionImporter
{

    private final ISessionManager sessionManager;

    private String description;
    private IProfilingResult profilingResult;
    private Set<URI> scope;

    public SessionImporter(ISessionManager sessionManager)
    {
        this.sessionManager = sessionManager;
    }

    @Override
    public void importSession(IProgressMonitor monitor) throws CoreException
    {
        monitor.beginTask(CoreMessages.ImportingSession_task, 2);
        monitor.worked(1);
        ICoverageSession session = new CoverageSession(profilingResult, scope, null);
        sessionManager.addSession(session, true, null);
        monitor.done();
    }

    @Override
    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public void setProfilingResult(final IProfilingResult source)
    {
        this.profilingResult = source;
    }

    @Override
    public void setScope(Set<URI> scope)
    {
        this.scope = scope;
    }

}
