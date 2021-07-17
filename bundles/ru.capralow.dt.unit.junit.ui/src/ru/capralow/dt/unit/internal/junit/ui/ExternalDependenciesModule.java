/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

import org.eclipse.core.runtime.Plugin;

import com._1c.g5.v8.dt.bm.index.emf.IBmEmfIndexManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.platform.services.core.ExternalObjectExtractor;
import com._1c.g5.v8.dt.platform.services.core.infobases.IInfobaseManager;
import com._1c.g5.v8.dt.platform.services.core.runtimes.IProcessEncodingProvider;
import com._1c.g5.v8.dt.platform.services.core.runtimes.environments.IResolvableRuntimeInstallationManager;
import com._1c.g5.v8.dt.platform.services.core.runtimes.execution.IRuntimeComponentManager;
import com._1c.g5.v8.dt.platform.version.IRuntimeVersionSupport;
import com._1c.g5.v8.dt.profiling.core.IProfilingService;
import com._1c.g5.wiring.AbstractServiceAwareModule;

public class ExternalDependenciesModule
    extends AbstractServiceAwareModule
{

    public ExternalDependenciesModule(Plugin bundle)
    {
        super(bundle);
    }

    @Override
    protected void doConfigure()
    {
        bind(ExternalObjectExtractor.class).toService();
        bind(IBmEmfIndexManager.class).toService();
        bind(IInfobaseManager.class).toService();
        bind(IProcessEncodingProvider.class).toService();
        bind(IProfilingService.class).toService();
        bind(IResourceLookup.class).toService();
        bind(IResolvableRuntimeInstallationManager.class).toService();
        bind(IRuntimeComponentManager.class).toService();
        bind(IRuntimeVersionSupport.class).toService();
        bind(IV8ProjectManager.class).toService();
    }

}
