/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.unit.internal.junit;

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Injector;

import ru.capralow.dt.unit.internal.junit.model.JUnitModel;
import ru.capralow.dt.unit.junit.TestRunListener;

public class JUnitPlugin
    extends Plugin
{
    public static final String ID = "ru.capralow.dt.unit.junit"; //$NON-NLS-1$

    private static JUnitPlugin instance;

    private static boolean fIsStopped = false;

    private static final String HISTORY_DIR_NAME = "history"; //$NON-NLS-1$

    public static final String ID_EXTENSION_POINT_TESTRUN_LISTENERS = ID + "." + "testRunListeners"; //$NON-NLS-1$ //$NON-NLS-2$

    public static IStatus createErrorStatus(String message)
    {
        return new Status(IStatus.ERROR, ID, 0, message, (Throwable)null);
    }

    public static IStatus createErrorStatus(String message, int code)
    {
        return new Status(IStatus.ERROR, ID, code, message, (Throwable)null);
    }

    public static IStatus createErrorStatus(String message, int code, Throwable throwable)
    {
        return new Status(IStatus.ERROR, ID, code, message, throwable);
    }

    public static IStatus createErrorStatus(String message, Throwable throwable)
    {
        return new Status(IStatus.ERROR, ID, 0, message, throwable);
    }

    public static File getHistoryDirectory() throws IllegalStateException
    {
        var historyDir = getInstance().getStateLocation().append(HISTORY_DIR_NAME).toFile();
        if (!historyDir.isDirectory())
        {
            historyDir.mkdir();
        }
        return historyDir;
    }

    public static JUnitPlugin getInstance()
    {
        return instance;
    }

    public static JUnitModel getModel()
    {
        return getInstance().fJUnitModel;
    }

    public static boolean isStopped()
    {
        return fIsStopped;
    }

    public static void log(IStatus status)
    {
        getInstance().getLog().log(status);
    }

    public static void log(Throwable e)
    {
        log(new Status(IStatus.ERROR, ID, IStatus.ERROR, "Error", e)); //$NON-NLS-1$
    }

    private Injector injector;

    private final JUnitModel fJUnitModel = new JUnitModel();

    /**
     * List storing the registered test run listeners
     */
    private ListenerList<TestRunListener> fNewTestRunListeners;

    public synchronized Injector getInjector()
    {
        if (injector == null)
            injector = createInjector();

        return injector;
    }

    /**
     * @return a <code>ListenerList</code> of all <code>TestRunListener</code>s
     */
    public ListenerList<TestRunListener> getNewTestRunListeners()
    {
        IExtensionPoint extensionPoint =
            Platform.getExtensionRegistry().getExtensionPoint(ID_EXTENSION_POINT_TESTRUN_LISTENERS);
        if (extensionPoint == null)
        {
            return null;
        }
        IConfigurationElement[] configs = extensionPoint.getConfigurationElements();
        var status = new MultiStatus(ID, IStatus.OK, "Could not load some testRunner extension points", null);
        for (IConfigurationElement config : configs)
        {
            try
            {
                Object testRunListener = config.createExecutableExtension("class"); //$NON-NLS-1$
                fNewTestRunListeners.add((TestRunListener)testRunListener);
            }
            catch (CoreException e)
            {
                status.add(e.getStatus());
            }
        }
        if (!status.isOK())
        {
            log(status);
        }

        return fNewTestRunListeners;
    }

    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);

        instance = this;
        fNewTestRunListeners = new ListenerList<>();

        fJUnitModel.start();
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        fIsStopped = true;
        try
        {
            InstanceScope.INSTANCE.getNode(ID).flush();
            fJUnitModel.stop();
        }
        finally
        {
            super.stop(context);
        }

        instance = null;
    }

    private Injector createInjector()
    {
        try
        {
            return Guice.createInjector(new ExternalDependenciesModule(this));

        }
        catch (Exception e)
        {
            String msg = MessageFormat.format(Messages.JUnitPlugin_Failed_to_create_injector_for_0,
                getBundle().getSymbolicName());
            log(createErrorStatus(msg, e));
            return null;

        }
    }

}
