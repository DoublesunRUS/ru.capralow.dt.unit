/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.junit;

import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import ru.capralow.dt.unit.internal.junit.JUnitPlugin;
import ru.capralow.dt.unit.internal.junit.model.JUnitModel;
import ru.capralow.dt.unit.internal.junit.model.ModelMessages;
import ru.capralow.dt.unit.internal.junit.model.TestRunSession;
import ru.capralow.dt.unit.junit.model.ITestRunSession;

/**
 * @author Aleksandr Kapralov
 *
 */
public final class JUnitCore
{
    public static final String JUNIT_CONTAINER_ID = "ru.capralow.dt.unit.junit.JUNIT_CONTAINER";
    public static final IPath JUNIT3_CONTAINER_PATH = new Path("ru.capralow.dt.unit.junit.JUNIT_CONTAINER").append("3");
    public static final IPath JUNIT4_CONTAINER_PATH = new Path("ru.capralow.dt.unit.junit.JUNIT_CONTAINER").append("4");
    public static final IPath JUNIT5_CONTAINER_PATH = new Path("ru.capralow.dt.unit.junit.JUNIT_CONTAINER").append("5");

    /**
     * @param listener
     */
    public static void addTestRunListener(TestRunListener listener)
    {
        JUnitPlugin.getInstance().getNewTestRunListeners().add(listener);
    }

    /**
     * @param testRunSession
     * @param file
     * @throws CoreException
     */
    public static void exportTestRunSession(TestRunSession testRunSession, File file) throws CoreException
    {
        JUnitModel.exportTestRunSession(testRunSession, file);
    }

    /**
     * @param testRunSession
     * @param output
     * @throws CoreException
     */
    public static void exportTestRunSession(TestRunSession testRunSession, OutputStream output) throws CoreException
    {
        try
        {
            JUnitModel.exportTestRunSession(testRunSession, output);
        }
        catch (TransformerException exception)
        {
            String pluginID = JUnitPlugin.ID;
            String message = ModelMessages.JUnitModel_could_not_export;
            throw new CoreException(new Status(4, pluginID, message, exception));
        }
    }

    /**
     * @param file
     * @return ITestRunSession
     * @throws CoreException
     */
    public static ITestRunSession importTestRunSession(File file) throws CoreException
    {
        return JUnitModel.importTestRunSession(file);
    }

    /**
     * @param url
     * @param monitor
     * @return ITestRunSession
     * @throws CoreException
     */
    public static ITestRunSession importTestRunSession(String url, IProgressMonitor monitor) throws CoreException
    {
        try
        {
            return JUnitModel.importTestRunSession(url, monitor);
        }
        catch (InvocationTargetException exception)
        {
            String pluginID = JUnitPlugin.ID;
            String message = ModelMessages.JUnitModel_could_not_import;
            Throwable throwable = exception.getCause() != null ? exception.getCause() : exception;
            throw new CoreException(new Status(4, pluginID, message, throwable));
        }
        catch (InterruptedException interruptedException)
        {
            return null;
        }
    }

    /**
     * @param listener
     */
    public static void removeTestRunListener(TestRunListener listener)
    {
        JUnitPlugin.getInstance().getNewTestRunListeners().remove(listener);
    }

    private JUnitCore()
    {
        // Utility class
    }
}
