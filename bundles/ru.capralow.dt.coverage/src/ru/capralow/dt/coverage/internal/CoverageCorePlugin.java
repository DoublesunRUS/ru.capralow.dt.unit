/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

import com._1c.g5.wiring.InjectorAwareServiceRegistrator;
import com._1c.g5.wiring.ServiceInitialization;
import com.google.inject.Guice;
import com.google.inject.Injector;

import ru.capralow.dt.coverage.ICorePreferences;
import ru.capralow.dt.coverage.ISessionManager;

/**
 * Bundle activator for the 1Unit Coverage core.
 */
public class CoverageCorePlugin
    extends Plugin
{

    public static final String ID = "ru.capralow.dt.coverage"; //$NON-NLS-1$

    private static CoverageCorePlugin instance;

    public static IStatus createErrorStatus(String message, Throwable throwable)
    {
        return new Status(IStatus.ERROR, ID, 0, message, throwable);
    }

    public static CoverageCorePlugin getInstance()
    {
        return instance;
    }

    public static void log(IStatus status)
    {
        getInstance().getLog().log(status);
    }

    private ICorePreferences preferences = ICorePreferences.DEFAULT;

    private Injector injector;

    private ISessionManager sessionManager;

    private BslCoverageLoader coverageLoader;

    private InjectorAwareServiceRegistrator registrator;

    public BslCoverageLoader getBslCoverageLoader()
    {
        return coverageLoader;
    }

    public synchronized Injector getInjector()
    {
        if (injector == null)
            injector = createInjector();

        return injector;
    }

    public ICorePreferences getPreferences()
    {
        return this.preferences;
    }

    public ISessionManager getSessionManager()
    {
        return sessionManager;
    }

    public void setPreferences(ICorePreferences preferences)
    {
        this.preferences = preferences;
    }

    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        instance = this;

        sessionManager = new SessionManager();

        coverageLoader = new BslCoverageLoader(sessionManager);

        registrator = new InjectorAwareServiceRegistrator(context, this::getInjector);

        ServiceInitialization.schedule(() -> registrator.activateManagedService(CoverageManager.class));
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        instance = null;
        super.stop(context);

        registrator.deactivateManagedServices(this);

        coverageLoader.dispose();
        coverageLoader = null;

        sessionManager = null;
    }

    private Injector createInjector()
    {
        try
        {
            return Guice.createInjector(new ExternalDependenciesModule(getInstance()));

        }
        catch (Exception e)
        {
            String msg = MessageFormat.format(CoreMessages.Failed_to_create_injector_for_0,
                getInstance().getBundle().getSymbolicName());
            log(createErrorStatus(msg, e));
            return injector;

        }
    }
}
