/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit;

import org.eclipse.core.runtime.Plugin;

import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.debug.core.model.IRuntimeDebugClientTargetManager;
import com._1c.g5.v8.dt.platform.services.core.dump.IExternalObjectDumpSupport;
import com._1c.g5.v8.dt.platform.services.core.infobases.IInfobaseAccessManager;
import com._1c.g5.v8.dt.platform.services.core.infobases.IInfobaseAssociationContextProvider;
import com._1c.g5.v8.dt.platform.services.core.infobases.IInfobaseAssociationManager;
import com._1c.g5.v8.dt.platform.services.core.infobases.IInfobaseManager;
import com._1c.g5.v8.dt.platform.services.core.publication.IPublicationManager;
import com._1c.g5.v8.dt.platform.services.core.runtimes.environments.IResolvableRuntimeInstallationManager;
import com._1c.g5.v8.dt.platform.services.core.runtimes.execution.IRuntimeComponentManager;
import com._1c.g5.wiring.AbstractServiceAwareModule;
import com.e1c.g5.dt.applications.IApplicationAttributeRepository;
import com.e1c.g5.dt.applications.IApplicationManager;

/**
 * @author Aleksandr Kapralov
 *
 */
public class ExternalDependenciesModule
    extends AbstractServiceAwareModule
{

    /**
     * @param bundle
     */
    public ExternalDependenciesModule(Plugin bundle)
    {
        super(bundle);
    }

    @Override
    protected void doConfigure()
    {
        bind(IApplicationAttributeRepository.class).toService();
        bind(IApplicationManager.class).toService();
        bind(IExternalObjectDumpSupport.class).toService();
        bind(IInfobaseAssociationManager.class).toService();
        bind(IInfobaseManager.class).toService();
        bind(IInfobaseAccessManager.class).toService();
        bind(IInfobaseAssociationContextProvider.class).toService();
        bind(IPublicationManager.class).toService();
        bind(IResolvableRuntimeInstallationManager.class).toService();
        bind(IRuntimeDebugClientTargetManager.class).toService();
        bind(IRuntimeComponentManager.class).toService();
        bind(IV8ProjectManager.class).toService();
    }

}
