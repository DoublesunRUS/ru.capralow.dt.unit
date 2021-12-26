/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.PushbackReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;

import ru.capralow.dt.unit.internal.junit.JUnitPlugin;

/**
 * @author Aleksandr Kapralov
 *
 */
public class RemoteTestRunnerClient
{
    private static String nullifyEmpty(StringBuffer buf)
    {
        int length = buf.length();
        if (length == 0)
        {
            return null;
        }
        char last = buf.charAt(length - 1);
        if (last == '\n')
        {
            if (length > 1 && buf.charAt(length - 2) == '\r')
            {
                return buf.substring(0, length - 2);
            }
            return buf.substring(0, length - 1);
        }
        if (last == '\r')
        {
            return buf.substring(0, length - 1);
        }
        return buf.toString();
    }

    private final StringBuffer fFailedTrace = new StringBuffer();
    private final StringBuffer fExpectedResult = new StringBuffer();
    private final StringBuffer fActualResult = new StringBuffer();
    private final StringBuffer fFailedRerunTrace = new StringBuffer();
    ProcessingState fDefaultState = new DefaultProcessingState();
    ProcessingState fTraceState = new TraceProcessingState();
    ProcessingState fExpectedState = new AppendingProcessingState(fExpectedResult, "%EXPECTE");
    ProcessingState fActualState = new AppendingProcessingState(fActualResult, "%ACTUALE");
    ProcessingState fRerunState = new AppendingProcessingState(fFailedRerunTrace, "%RTRACEE");
    ProcessingState fCurrentState = fDefaultState;
    private ITestRunListener2[] fListeners;
    private ServerSocket fServerSocket;
    private Socket fSocket;
    private int fPort = -1;
    private PrintWriter fWriter;
    private PushbackReader fPushbackReader;

    private String fLastLineDelimiter;

    private String fVersion;

    private String fFailedTest;

    private String fFailedTestId;

    private int fFailureKind;

    private boolean fDebug = false;

    /**
     * @return boolean
     */
    public boolean isRunning()
    {
        return fSocket != null;
    }

    /**
     * @param testId
     * @param className
     * @param testName
     */
    public void rerunTest(String testId, String className, String testName)
    {
        if (isRunning())
        {
            fActualResult.setLength(0);
            fExpectedResult.setLength(0);
            fWriter.println(">RERUN  " + testId + " " + className + " " + testName);
            fWriter.flush();
        }
    }

    /**
     * @param listeners
     * @param port
     */
    public synchronized void startListening(ITestRunListener2[] listeners, int port)
    {
        fListeners = listeners;
        fPort = port;
        ServerConnection connection = new ServerConnection(port);
        connection.start();
    }

    /**
     *
     */
    public synchronized void stopTest()
    {
        if (isRunning())
        {
            fWriter.println(">STOP   ");
            fWriter.flush();
        }
    }

    /**
     *
     */
    public synchronized void stopWaiting()
    {
        if (fServerSocket != null && !fServerSocket.isClosed() && fSocket == null)
        {
            shutDown();
        }
    }

    private boolean hasTestId()
    {
        if (fVersion == null)
        {
            return true;
        }
        return fVersion.equals("v2");
    }

    private synchronized void shutDown()
    {
        if (fDebug)
        {
            System.out.println("shutdown " + fPort);
        }
        if (fWriter != null)
        {
            fWriter.close();
            fWriter = null;
        }
        try
        {
            if (fPushbackReader != null)
            {
                fPushbackReader.close();
                fPushbackReader = null;
            }
        }
        catch (IOException iOException)
        {
            // nothing to do
        }
        try
        {
            if (fSocket != null)
            {
                fSocket.close();
                fSocket = null;
            }
        }
        catch (IOException iOException)
        {
            // nothing to do
        }
        try
        {
            if (fServerSocket != null)
            {
                fServerSocket.close();
                fServerSocket = null;
            }
        }
        catch (IOException iOException)
        {
            // nothing to do
        }
    }

    String[] extractTestId(String arg)
    {
        String[] result = new String[2];
        if (!hasTestId())
        {
            result[0] = arg;
            result[1] = arg;
            return result;
        }
        int i = arg.indexOf(44);
        result[0] = arg.substring(0, i);
        result[1] = arg.substring(i + 1, arg.length());
        return result;
    }

    /**
     * @author Aleksandr Kapralov
     *
     */
    public abstract class ListenerSafeRunnable
        implements ISafeRunnable
    {
        @Override
        public void handleException(Throwable exception)
        {
            JUnitPlugin.log(exception);
        }
    }

    private class ServerConnection
        extends Thread
    {
        int fServerPort;

        ServerConnection(int port)
        {
            super("ServerConnection");
            fServerPort = port;
        }

        @Override
        public void run()
        {
            try
            {
                String message;
                if (fDebug)
                {
                    System.out.println("Creating server socket " + fServerPort);
                }
                fServerSocket = new ServerSocket(fServerPort);
                fSocket = fServerSocket.accept();
                fPushbackReader = new PushbackReader(
                    new BufferedReader(new InputStreamReader(fSocket.getInputStream(), StandardCharsets.UTF_8)));
                fWriter =
                    new PrintWriter(new OutputStreamWriter(fSocket.getOutputStream(), StandardCharsets.UTF_8), true);
                while (fPushbackReader != null && (message = readMessage(fPushbackReader)) != null)
                {
                    receiveMessage(message);
                }
            }
            catch (SocketException socketException)
            {
                notifyTestRunTerminated();
            }
            catch (IOException e)
            {
                JUnitPlugin.log(e);
            }
            shutDown();
        }

        private void receiveMessage(String message)
        {
            fCurrentState = fCurrentState.readMessage(message);
        }

        private void notifyTestRunTerminated()
        {
            if (JUnitPlugin.isStopped())
            {
                return;
            }
            ITestRunListener2[] arriTestRunListener2 = fListeners;
            int n = arriTestRunListener2.length;
            int n2 = 0;
            while (n2 < n)
            {
                final ITestRunListener2 listener = arriTestRunListener2[n2];
                SafeRunner.run(new ListenerSafeRunnable()
                {

                    @Override
                    public void run()
                    {
                        listener.testRunTerminated();
                    }
                });
                ++n2;
            }
        }

        private String readMessage(PushbackReader in) throws IOException
        {
            int ch;
            StringBuilder buf = new StringBuilder(128);
            while ((ch = in.read()) != -1)
            {
                switch (ch)
                {
                case '\n':
                {
                    fLastLineDelimiter = "\n";
                    return buf.toString();
                }
                case '\r':
                {
                    ch = in.read();
                    if (ch == '\n')
                    {
                        fLastLineDelimiter = "\r\n";
                    }
                    else
                    {
                        in.unread(ch);
                        fLastLineDelimiter = "\r";
                    }
                    return buf.toString();
                }
                default:
                    buf.append((char)ch);
                    break;
                }
            }
            fLastLineDelimiter = null;
            if (buf.length() == 0)
            {
                return null;
            }
            return buf.toString();
        }
    }

    class AppendingProcessingState
        extends ProcessingState
    {
        private final StringBuffer fBuffer;
        private String fEndString;

        AppendingProcessingState(StringBuffer buffer, String endString)
        {
            super();
            fBuffer = buffer;
            fEndString = endString;
        }

        void entireStringRead()
        {
            /**
             * subclasses can override to do special things when end message is read
             */
        }

        @Override
        ProcessingState readMessage(String message)
        {
            if (message.startsWith(fEndString))
            {
                entireStringRead();
                return fDefaultState;
            }
            fBuffer.append(message);
            if (fLastLineDelimiter != null)
            {
                fBuffer.append(fLastLineDelimiter);
            }
            return this;
        }
    }

    class DefaultProcessingState
        extends ProcessingState
    {
        DefaultProcessingState()
        {
            super();
        }

        @Override
        ProcessingState readMessage(String message)
        {
            if (message.startsWith("%TRACES "))
            {
                fFailedTrace.setLength(0);
                return fTraceState;
            }
            if (message.startsWith("%EXPECTS"))
            {
                fExpectedResult.setLength(0);
                return fExpectedState;
            }
            if (message.startsWith("%ACTUALS"))
            {
                fActualResult.setLength(0);
                return fActualState;
            }
            if (message.startsWith("%RTRACES"))
            {
                fFailedRerunTrace.setLength(0);
                return fRerunState;
            }
            String arg = message.substring(8);
            if (message.startsWith("%TESTC  "))
            {
                int count = 0;
                int v = arg.indexOf(32);
                if (v == -1)
                {
                    fVersion = "v1";
                    count = Integer.parseInt(arg);
                }
                else
                {
                    fVersion = arg.substring(v + 1);
                    String sc = arg.substring(0, v);
                    count = Integer.parseInt(sc);
                }
                notifyTestRunStarted(count);
                return this;
            }
            if (message.startsWith("%TESTS  "))
            {
                notifyTestStarted(arg);
                return this;
            }
            if (message.startsWith("%TESTE  "))
            {
                notifyTestEnded(arg);
                return this;
            }
            if (message.startsWith("%ERROR  "))
            {
                extractFailure(arg, 1);
                return this;
            }
            if (message.startsWith("%FAILED "))
            {
                extractFailure(arg, 2);
                return this;
            }
            if (message.startsWith("%RUNTIME"))
            {
                long elapsedTime = Long.parseLong(arg);
                testRunEnded(elapsedTime);
                return this;
            }
            if (message.startsWith("%TSTSTP "))
            {
                long elapsedTime = Long.parseLong(arg);
                notifyTestRunStopped(elapsedTime);
                shutDown();
                return this;
            }
            if (message.startsWith("%TSTTREE"))
            {
                notifyTestTreeEntry(arg);
                return this;
            }
            if (message.startsWith("%TSTRERN"))
            {
                if (hasTestId())
                {
                    scanReranMessage(arg);
                }
                else
                {
                    scanOldReranMessage(arg);
                }
                return this;
            }
            return this;
        }

        private void notifyTestRunStarted(final int count)
        {
            if (JUnitPlugin.isStopped())
            {
                return;
            }
            ITestRunListener2[] arriTestRunListener2 = fListeners;
            int n = arriTestRunListener2.length;
            int n2 = 0;
            while (n2 < n)
            {
                final ITestRunListener2 listener = arriTestRunListener2[n2];
                SafeRunner.run(new ListenerSafeRunnable()
                {

                    @Override
                    public void run()
                    {
                        listener.testRunStarted(count);
                    }
                });
                ++n2;
            }
        }

        private void notifyTestRunStopped(final long elapsedTime)
        {
            if (JUnitPlugin.isStopped())
            {
                return;
            }
            ITestRunListener2[] arriTestRunListener2 = fListeners;
            int n = arriTestRunListener2.length;
            int n2 = 0;
            while (n2 < n)
            {
                final ITestRunListener2 listener = arriTestRunListener2[n2];
                SafeRunner.run(new ListenerSafeRunnable()
                {

                    @Override
                    public void run()
                    {
                        listener.testRunStopped(elapsedTime);
                    }
                });
                ++n2;
            }
        }

        private void extractFailure(String arg, int status)
        {
            String[] s = extractTestId(arg);
            fFailedTestId = s[0];
            fFailedTest = s[1];
            fFailureKind = status;
        }

        private void notifyTestEnded(final String test)
        {
            if (JUnitPlugin.isStopped())
            {
                return;
            }
            ITestRunListener2[] arriTestRunListener2 = fListeners;
            int n = arriTestRunListener2.length;
            int n2 = 0;
            while (n2 < n)
            {
                final ITestRunListener2 listener = arriTestRunListener2[n2];
                SafeRunner.run(new ListenerSafeRunnable()
                {

                    @Override
                    public void run()
                    {
                        String[] s = extractTestId(test);
                        listener.testEnded(s[0], s[1]);
                    }
                });
                ++n2;
            }
        }

        private void notifyTestStarted(final String test)
        {
            if (JUnitPlugin.isStopped())
            {
                return;
            }
            ITestRunListener2[] arriTestRunListener2 = fListeners;
            int n = arriTestRunListener2.length;
            int n2 = 0;
            while (n2 < n)
            {
                final ITestRunListener2 listener = arriTestRunListener2[n2];
                SafeRunner.run(new ListenerSafeRunnable()
                {

                    @Override
                    public void run()
                    {
                        String[] s = extractTestId(test);
                        listener.testStarted(s[0], s[1]);
                    }
                });
                ++n2;
            }
        }

        private void notifyTestTreeEntry(String treeEntry)
        {
            ITestRunListener2[] arriTestRunListener2 = fListeners;
            int n = arriTestRunListener2.length;
            int n2 = 0;
            while (n2 < n)
            {
                ITestRunListener2 listener = arriTestRunListener2[n2];
                if (!hasTestId())
                {
                    listener.testTreeEntry(fakeTestId(treeEntry));
                }
                else
                {
                    listener.testTreeEntry(treeEntry);
                }
                ++n2;
            }
        }

        private void scanOldReranMessage(String arg)
        {
            int c = arg.indexOf(" ");
            int t = arg.indexOf(" ", c + 1);
            String className = arg.substring(0, c);
            String testName = arg.substring(c + 1, t);
            String status = arg.substring(t + 1);
            String testId = String.valueOf(className) + testName;
            notifyTestReran(testId, className, testName, status);
        }

        private void scanReranMessage(String arg)
        {
            int i = arg.indexOf(32);
            int c = arg.indexOf(32, i + 1);
            int t = arg.endsWith("ERROR") ? arg.length() - "ERROR".length() - 1
                : (arg.endsWith("FAILURE") ? arg.length() - "FAILURE".length() - 1
                    : (arg.endsWith("OK") ? arg.length() - "OK".length() - 1 : arg.indexOf(32, c + 1)));
            String testId = arg.substring(0, i);
            String className = arg.substring(i + 1, c);
            String testName = arg.substring(c + 1, t);
            String status = arg.substring(t + 1);
            notifyTestReran(testId, className, testName, status);
        }

        private void testRunEnded(final long elapsedTime)
        {
            if (JUnitPlugin.isStopped())
            {
                return;
            }
            ITestRunListener2[] arriTestRunListener2 = fListeners;
            int n = arriTestRunListener2.length;
            int n2 = 0;
            while (n2 < n)
            {
                final ITestRunListener2 listener = arriTestRunListener2[n2];
                SafeRunner.run(new ListenerSafeRunnable()
                {

                    @Override
                    public void run()
                    {
                        listener.testRunEnded(elapsedTime);
                    }
                });
                ++n2;
            }
        }

        private void notifyTestReran(String testId, String className, String testName, String status)
        {
            int statusCode = ITestRunListener2.STATUS_OK;
            if ("FAILURE".equals(status))
            {
                statusCode = ITestRunListener2.STATUS_FAILURE;
            }
            else if ("ERROR".equals(status))
            {
                statusCode = ITestRunListener2.STATUS_ERROR;
            }
            String trace = "";
            if (statusCode != ITestRunListener2.STATUS_OK)
            {
                trace = fFailedRerunTrace.toString();
            }
            notifyTestReran(testId, className, testName, statusCode, trace);
        }

        private void notifyTestReran(final String testId, final String className, final String testName,
            final int statusCode, final String trace)
        {
            ITestRunListener2[] arriTestRunListener2 = fListeners;
            int n = arriTestRunListener2.length;
            int n2 = 0;
            while (n2 < n)
            {
                final ITestRunListener2 listener = arriTestRunListener2[n2];
                SafeRunner.run(new ListenerSafeRunnable()
                {

                    @Override
                    public void run()
                    {
                        listener.testReran(testId, className, testName, statusCode, trace,
                            RemoteTestRunnerClient.nullifyEmpty(fExpectedResult),
                            RemoteTestRunnerClient.nullifyEmpty(fActualResult));
                    }
                });
                ++n2;
            }
        }

        private String fakeTestId(String treeEntry)
        {
            int index0 = treeEntry.indexOf(44);
            String testName = treeEntry.substring(0, index0).trim();
            return String.valueOf(testName) + "," + treeEntry;
        }
    }

    abstract static class ProcessingState
    {
        abstract ProcessingState readMessage(String var1);
    }

    class TraceProcessingState
        extends AppendingProcessingState
    {
        TraceProcessingState()
        {
            super(fFailedTrace, "%TRACEE ");
        }

        @Override
        void entireStringRead()
        {
            notifyTestFailed();
            fExpectedResult.setLength(0);
            fActualResult.setLength(0);
        }

        @Override
        ProcessingState readMessage(String message)
        {
            if (message.startsWith("%TRACEE "))
            {
                notifyTestFailed();
                fFailedTrace.setLength(0);
                fActualResult.setLength(0);
                fExpectedResult.setLength(0);
                return fDefaultState;
            }
            fFailedTrace.append(message);
            if (fLastLineDelimiter != null)
            {
                fFailedTrace.append(fLastLineDelimiter);
            }
            return this;
        }

        private void notifyTestFailed()
        {
            if (JUnitPlugin.isStopped())
            {
                return;
            }
            ITestRunListener2[] arriTestRunListener2 = fListeners;
            int n = arriTestRunListener2.length;
            int n2 = 0;
            while (n2 < n)
            {
                final ITestRunListener2 listener = arriTestRunListener2[n2];
                SafeRunner.run(new ListenerSafeRunnable()
                {

                    @Override
                    public void run()
                    {
                        listener.testFailed(fFailureKind, fFailedTestId, fFailedTest, fFailedTrace.toString(),
                            RemoteTestRunnerClient.nullifyEmpty(fExpectedResult),
                            RemoteTestRunnerClient.nullifyEmpty(fActualResult));
                    }
                });
                ++n2;
            }
        }
    }

}
