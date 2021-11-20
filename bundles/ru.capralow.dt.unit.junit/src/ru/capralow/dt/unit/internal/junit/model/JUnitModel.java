/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.ILaunchManager;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com._1c.g5.v8.dt.core.platform.IV8Project;

import ru.capralow.dt.unit.internal.junit.BasicElementLabels;
import ru.capralow.dt.unit.internal.junit.JUnitPlugin;
import ru.capralow.dt.unit.internal.junit.JUnitPreferencesConstants;
import ru.capralow.dt.unit.internal.junit.launcher.JUnitLaunchConfigurationConstants;
import ru.capralow.dt.unit.junit.TestRunListener;

/**
 * Central registry for JUnit test runs.
 */
public final class JUnitModel
{

    /**
     * Exports the given test run session.
     *
     * @param testRunSession the test run session
     * @param file the destination
     * @throws CoreException if an error occurred
     */
    public static void exportTestRunSession(TestRunSession testRunSession, File file) throws CoreException
    {
        FileOutputStream out = null;
        try
        {
            out = new FileOutputStream(file);
            exportTestRunSession(testRunSession, out);

        }
        catch (IOException | TransformerException e)
        {
            throwExportError(file, e);
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e2)
                {
                    JUnitPlugin.log(e2);
                }
            }
        }
    }

    public static void exportTestRunSession(TestRunSession testRunSession, OutputStream out)
        throws TransformerFactoryConfigurationError, TransformerException
    {

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        InputSource inputSource = new InputSource();
        SAXSource source = new SAXSource(new TestRunSessionSerializer(testRunSession), inputSource);
        StreamResult result = new StreamResult(out);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$
        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
        /*
         * Bug in Xalan: Only indents if proprietary property
         * org.apache.xalan.templates.OutputProperties.S_KEY_INDENT_AMOUNT is set.
         *
         * Bug in Xalan as shipped with J2SE 5.0:
         * Does not read the indent-amount property at all >:-(.
         */
        try
        {
            transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        catch (IllegalArgumentException e)
        {
            // no indentation today...
        }
        transformer.transform(source, result);
    }

    public static void importIntoTestRunSession(File swapFile, TestRunSession testRunSession) throws CoreException
    {
        try
        {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = parserFactory.newSAXParser();
            TestRunHandler handler = new TestRunHandler(testRunSession);
            parser.parse(swapFile, handler);
        }
        catch (ParserConfigurationException | SAXException | IOException | IllegalArgumentException e)
        {
            // Bug in parser: can throw IAE even if file is not null
            throwImportError(swapFile, e);
        }

    }

    /**
     * Imports a test run session from the given file.
     *
     * @param file a file containing a test run session transcript
     * @return the imported test run session
     * @throws CoreException if the import failed
     */
    public static TestRunSession importTestRunSession(File file) throws CoreException
    {
        try
        {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = parserFactory.newSAXParser();
            TestRunHandler handler = new TestRunHandler();
            parser.parse(file, handler);
            TestRunSession session = handler.getTestRunSession();
            JUnitPlugin.getModel().addTestRunSession(session);
            return session;
        }
        catch (ParserConfigurationException | SAXException | IOException | IllegalArgumentException e)
        {
            // Bug in parser: can throw IAE even if file is not null
            throwImportError(file, e);
        }
        return null; // does not happen
    }

    /**
     * Imports a test run session from the given URL.
     *
     * @param url an URL to a test run session transcript
     * @param monitor a progress monitor for cancellation
     * @return the imported test run session
     * @throws InvocationTargetException wrapping a CoreException if the import failed
     * @throws InterruptedException if the import was cancelled
     */
    public static TestRunSession importTestRunSession(String url, IProgressMonitor monitor)
        throws InvocationTargetException, InterruptedException
    {
        monitor.beginTask(Messages.JUnitModel_importing_from_url, IProgressMonitor.UNKNOWN);
        final String trimmedUrl = url.trim().replaceAll("\r\n?|\n", ""); //$NON-NLS-1$ //$NON-NLS-2$
        final TestRunHandler handler = new TestRunHandler(monitor);

        final CoreException[] exception = { null };
        final TestRunSession[] session = { null };

        Thread importThread = new Thread("JUnit URL importer") //$NON-NLS-1$
        {
            @Override
            public void run()
            {
                try
                {
                    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                    SAXParser parser = parserFactory.newSAXParser();
                    parser.parse(trimmedUrl, handler);
                    session[0] = handler.getTestRunSession();
                }
                catch (OperationCanceledException e)
                {
                    // canceled
                }
                catch (ParserConfigurationException | SAXException | IOException | IllegalArgumentException e)
                {
                    // Bug in parser: can throw IAE even if URL is not null
                    storeImportError(e);
                }
            }

            private void storeImportError(Exception e)
            {
                exception[0] = new CoreException(new org.eclipse.core.runtime.Status(IStatus.ERROR, JUnitPlugin.ID,
                    Messages.JUnitModel_could_not_import, e));
            }
        };
        importThread.start();

        while (session[0] == null && exception[0] == null && !monitor.isCanceled())
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                // that's OK
            }
        }
        if (session[0] == null)
        {
            if (exception[0] != null)
            {
                throw new InvocationTargetException(exception[0]);
            }
            else
            {
                // have to kill the thread since we don't control URLConnection and XML parsing
                importThread.interrupt();
                throw new InterruptedException();
            }
        }

        JUnitPlugin.getModel().addTestRunSession(session[0]);
        monitor.done();
        return session[0];
    }

    private static void throwExportError(File file, Exception e) throws CoreException
    {
        throw new CoreException(new org.eclipse.core.runtime.Status(IStatus.ERROR, JUnitPlugin.ID,
            MessageFormat.format(Messages.JUnitModel_could_not_write, BasicElementLabels.getPathLabel(file)), e));
    }

    private static void throwImportError(File file, Exception e) throws CoreException
    {
        throw new CoreException(new org.eclipse.core.runtime.Status(IStatus.ERROR, JUnitPlugin.ID,
            MessageFormat.format(Messages.JUnitModel_could_not_read, BasicElementLabels.getPathLabel(file)), e));
    }

    private final ListenerList<ITestRunSessionListener> fTestRunSessionListeners = new ListenerList<>();

    /**
     * Active test run sessions, youngest first.
     */
    private final LinkedList<TestRunSession> fTestRunSessions = new LinkedList<>();

    private final ILaunchListener fLaunchListener = new JUnitLaunchListener();

    /**
     * Adds the given {@link TestRunSession} and notifies all registered
     * {@link ITestRunSessionListener}s.
     *
     * @param testRunSession the session to add
     */
    public void addTestRunSession(TestRunSession testRunSession)
    {
        Assert.isNotNull(testRunSession);
        ArrayList<TestRunSession> toRemove = new ArrayList<>();

        synchronized (this)
        {
            Assert.isLegal(!fTestRunSessions.contains(testRunSession));
            fTestRunSessions.addFirst(testRunSession);

            int maxCount = Platform.getPreferencesService()
                .getInt(JUnitPlugin.ID, JUnitPreferencesConstants.MAX_TEST_RUNS, 10, null);
            int size = fTestRunSessions.size();
            if (size > maxCount)
            {
                List<TestRunSession> excess = fTestRunSessions.subList(maxCount, size);
                for (Iterator<TestRunSession> iter = excess.iterator(); iter.hasNext();)
                {
                    TestRunSession oldSession = iter.next();
                    if (!oldSession.isStarting() && !oldSession.isRunning())
                    {
                        toRemove.add(oldSession);
                        iter.remove();
                    }
                }
            }
        }

        for (TestRunSession oldSession : toRemove)
        {
            notifyTestRunSessionRemoved(oldSession);
        }
        notifyTestRunSessionAdded(testRunSession);
    }

    public void addTestRunSessionListener(ITestRunSessionListener listener)
    {
        fTestRunSessionListeners.add(listener);
    }

    /**
     * @return a list of active {@link TestRunSession}s. The list is a copy of
     *         the internal data structure and modifications do not affect the
     *         global list of active sessions. The list is sorted by age, youngest first.
     */
    public synchronized List<TestRunSession> getTestRunSessions()
    {
        return new ArrayList<>(fTestRunSessions);
    }

    /**
     * Removes the given {@link TestRunSession} and notifies all registered
     * {@link ITestRunSessionListener}s.
     *
     * @param testRunSession the session to remove
     */
    public void removeTestRunSession(TestRunSession testRunSession)
    {
        boolean existed;
        synchronized (this)
        {
            existed = fTestRunSessions.remove(testRunSession);
        }
        if (existed)
        {
            notifyTestRunSessionRemoved(testRunSession);
        }
        testRunSession.removeSwapFile();
    }

    public void removeTestRunSessionListener(ITestRunSessionListener listener)
    {
        fTestRunSessionListeners.remove(listener);
    }

    /**
     * Starts the model (called by the {@link JUnitPlugin} on startup).
     */
    public void start()
    {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        launchManager.addLaunchListener(fLaunchListener);
    }

    /**
     * Stops the model (called by the {@link JUnitCorePlugin} on shutdown).
     */
    public void stop()
    {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        launchManager.removeLaunchListener(fLaunchListener);

        File historyDirectory = JUnitPlugin.getHistoryDirectory();
        File[] swapFiles = historyDirectory.listFiles();
        if (swapFiles != null)
        {
            for (File swapFile : swapFiles)
            {
                swapFile.delete();
            }
        }

    }

    private void notifyTestRunSessionAdded(TestRunSession testRunSession)
    {
        for (ITestRunSessionListener listener : fTestRunSessionListeners)
        {
            listener.sessionAdded(testRunSession);
        }
    }

    private void notifyTestRunSessionRemoved(TestRunSession testRunSession)
    {
        testRunSession.stopTestRun();
        ILaunch launch = testRunSession.getLaunch();
        if (launch != null)
        {
            ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
            launchManager.removeLaunch(launch);
        }

        for (ITestRunSessionListener listener : fTestRunSessionListeners)
        {
            listener.sessionRemoved(testRunSession);
        }
    }

    private final class JUnitLaunchListener
        implements ILaunchListener
    {

        /**
         * Used to track new launches. We need to do this
         * so that we only attach a TestRunner once to a launch.
         * Once a test runner is connected, it is removed from the set.
         */
        private HashSet<ILaunch> fTrackedLaunches = new HashSet<>(20);

        /*
         * @see ILaunchListener#launchAdded(ILaunch)
         */
        @Override
        public void launchAdded(ILaunch launch)
        {
            fTrackedLaunches.add(launch);
        }

        /*
         * @see ILaunchListener#launchChanged(ILaunch)
         */
        @Override
        public void launchChanged(final ILaunch launch)
        {
            if (!fTrackedLaunches.contains(launch))
            {
                return;
            }

            ILaunchConfiguration config = launch.getLaunchConfiguration();
            if (config == null)
            {
                return;
            }

            final IV8Project v8Project = JUnitLaunchConfigurationConstants.getV8Project(config);
            if (v8Project == null)
            {
                return;
            }

            try
            {
                // test whether the launch defines the JUnit attributes
                String testProject =
                    config.getAttribute(JUnitLaunchConfigurationConstants.EXTENSION_PROJECT_TO_TEST, (String)null);
                if (testProject == null)
                {
                    return;
                }

                fTrackedLaunches.remove(launch);
                connectTestRunner(launch, v8Project, testProject);
            }
            catch (NumberFormatException | CoreException e)
            {
                // Нечего делать
            }
        }

        /*
         * @see ILaunchListener#launchRemoved(ILaunch)
         */
        @Override
        public void launchRemoved(final ILaunch launch)
        {
            fTrackedLaunches.remove(launch);
        }

        private void connectTestRunner(ILaunch launch, IV8Project configurationProject, String testExtensionName)
        {
            TestRunSession testRunSession = new TestRunSession(launch, configurationProject, testExtensionName);
            addTestRunSession(testRunSession);

            for (TestRunListener listener : JUnitPlugin.getInstance().getNewTestRunListeners())
            {
                listener.sessionLaunched(testRunSession);
            }
        }
    }

}
