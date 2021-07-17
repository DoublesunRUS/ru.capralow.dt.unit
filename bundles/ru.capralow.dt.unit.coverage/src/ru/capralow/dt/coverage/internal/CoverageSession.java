/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.emf.common.util.URI;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;

import com._1c.g5.v8.dt.profiling.core.IProfilingResult;

import ru.capralow.dt.coverage.ICoverageSession;

/**
 * A {@link ru.capralow.dt.coverage.ICoverageSession} implementation.
 */
public class CoverageSession
    extends PlatformObject
    implements ICoverageSession
{

    private String description;
    private Set<URI> scope;
    private ILaunchConfiguration launchConfiguration;
    private String profileName;

    public CoverageSession(IProfilingResult profilingResult, Set<URI> set, ILaunchConfiguration launchConfiguration)
    {
        Object[] args = new Object[] { profilingResult.getName(), new Date() };

        this.profileName = profilingResult.getName();
        this.description = MessageFormat.format(CoreMessages.LaunchSessionDescription_value, args);
        this.scope = Collections.unmodifiableSet(new HashSet<>(set));
        this.launchConfiguration = launchConfiguration;
    }

    // ICoverageSession implementation

    @Override
    public void accept(IExecutionDataVisitor executionDataVisitor, ISessionInfoVisitor sessionInfoVisitor)
        throws CoreException
    {
        // Для BSL используется IProfilingResult
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public ILaunchConfiguration getLaunchConfiguration()
    {
        return launchConfiguration;
    }

    @Override
    public String getProfileName()
    {
        return profileName;
    }

    @Override
    public Set<URI> getScope()
    {
        return scope;
    }

}
