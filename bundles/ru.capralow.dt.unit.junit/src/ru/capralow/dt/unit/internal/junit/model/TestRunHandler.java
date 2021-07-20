/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.model;

import java.util.Arrays;
import java.util.Stack;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.osgi.util.NLS;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com._1c.g5.v8.dt.core.platform.IV8Project;

import ru.capralow.dt.unit.internal.junit.model.TestElement.Status;

public class TestRunHandler
    extends DefaultHandler
{

    private int fId;

    private TestRunSession fTestRunSession;
    private TestSuiteElement fTestSuite;
    private TestCaseElement fTestCase;
    private Stack<Boolean> fNotRun = new Stack<>();

    private StringBuffer fFailureBuffer;
    private boolean fInExpected;
    private boolean fInActual;
    private StringBuffer fExpectedBuffer;
    private StringBuffer fActualBuffer;

    private Locator fLocator;

    private Status fStatus;

    private IProgressMonitor fMonitor;
    private int fLastReportedLine;

    public TestRunHandler()
    {

    }

    public TestRunHandler(IProgressMonitor monitor)
    {
        fMonitor = monitor;
    }

    public TestRunHandler(TestRunSession testRunSession)
    {
        fTestRunSession = testRunSession;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        if (fInExpected)
        {
            fExpectedBuffer.append(ch, start, length);

        }
        else if (fInActual)
        {
            fActualBuffer.append(ch, start, length);

        }
        else if (fFailureBuffer != null)
        {
            fFailureBuffer.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        switch (qName)
        {
        // OK
        case IXmlTags.NODE_TESTRUN:
            break;
        // OK
        case IXmlTags.NODE_TESTSUITES:
            break;
        case IXmlTags.NODE_TESTSUITE:
            handleTestElementEnd(fTestSuite);
            fTestSuite = fTestSuite.getParent();
            break;
        // OK
        case IXmlTags.NODE_PROPERTIES:
        case IXmlTags.NODE_PROPERTY:
            break;
        case IXmlTags.NODE_TESTCASE:
            handleTestElementEnd(fTestCase);
            fTestCase = null;
            break;
        case IXmlTags.NODE_FAILURE:
        case IXmlTags.NODE_ERROR:
            {
                TestElement testElement = fTestCase;
                if (testElement == null)
                    testElement = fTestSuite;
                handleFailure(testElement);
                break;
            }
        case IXmlTags.NODE_EXPECTED:
            fInExpected = false;
            if (fFailureBuffer != null)
            {
                // skip whitespace from before <expected> and <actual> nodes
                fFailureBuffer.setLength(0);
            }
            break;
        case IXmlTags.NODE_ACTUAL:
            fInActual = false;
            if (fFailureBuffer != null)
            {
                // skip whitespace from before <expected> and <actual> nodes
                fFailureBuffer.setLength(0);
            }
            break;
        // OK
        case IXmlTags.NODE_SYSTEM_OUT:
        case IXmlTags.NODE_SYSTEM_ERR:
            break;
        case IXmlTags.NODE_SKIPPED:
            {
                TestElement testElement = fTestCase;
                if (testElement == null)
                    testElement = fTestSuite;
                if (fFailureBuffer != null && fFailureBuffer.length() > 0)
                {
                    handleFailure(testElement);
                    testElement.setAssumptionFailed(true);
                }
                else if (fTestCase != null)
                {
                    fTestCase.setIgnored(true);
                }
                else
                { // not expected
                    testElement.setAssumptionFailed(true);
                }
                break;
            }
        default:
            handleUnknownNode(qName);
            break;
        }
    }

    @Override
    public void error(SAXParseException e) throws SAXException
    {
        throw e;
    }

    /**
     * @return the parsed test run session, or <code>null</code>
     */
    public TestRunSession getTestRunSession()
    {
        return fTestRunSession;
    }

    @Override
    public void setDocumentLocator(Locator locator)
    {
        fLocator = locator;
    }

    @Override
    public void startDocument() throws SAXException
    {
        // Нечего делать
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        if (fLocator != null && fMonitor != null)
        {
            int line = fLocator.getLineNumber();
            if (line - 20 >= fLastReportedLine)
            {
                line -= line % 20;
                fLastReportedLine = line;
                fMonitor.subTask(NLS.bind(Messages.TestRunHandler_lines_read, Integer.valueOf(line)));
            }
        }
        if (Thread.interrupted())
            throw new OperationCanceledException();

        switch (qName)
        {
        case IXmlTags.NODE_TESTRUN:
            if (fTestRunSession == null)
            {
                String name = attributes.getValue(IXmlTags.ATTR_NAME);
                String project = attributes.getValue(IXmlTags.ATTR_PROJECT);
                IV8Project configurationProject = null;
                if (project != null)
                {
                    // TODO: Переделать на получение проекта
                }
                fTestRunSession = new TestRunSession(name, configurationProject);

            }
            else
            {
                fTestRunSession.reset();
            }
            fTestSuite = fTestRunSession.getTestRoot();
            break;
        // support Ant's 'junitreport' task; create suite from NODE_TESTSUITE
        case IXmlTags.NODE_TESTSUITES:
            break;
        case IXmlTags.NODE_TESTSUITE:
            {
                String name = attributes.getValue(IXmlTags.ATTR_NAME);
                if (fTestRunSession == null)
                {
                    // support standalone suites and Ant's 'junitreport' task:
                    fTestRunSession = new TestRunSession(name, null);
                    fTestSuite = fTestRunSession.getTestRoot();
                }
                String pack = attributes.getValue(IXmlTags.ATTR_PACKAGE);
                String suiteName = pack == null ? name : pack + "." + name; //$NON-NLS-1$
                String displayName = attributes.getValue(IXmlTags.ATTR_DISPLAY_NAME);
                String paramTypesStr = attributes.getValue(IXmlTags.ATTR_PARAMETER_TYPES);
                String[] paramTypes;
                if (paramTypesStr != null && !paramTypesStr.trim().isEmpty())
                {
                    paramTypes = paramTypesStr.split(","); //$NON-NLS-1$
                    Arrays.parallelSetAll(paramTypes, i -> paramTypes[i].trim());
                }
                else
                {
                    paramTypes = null;
                }
                String uniqueId = attributes.getValue(IXmlTags.ATTR_UNIQUE_ID);
                if (uniqueId != null && uniqueId.trim().isEmpty())
                {
                    uniqueId = null;
                }
                fTestSuite = (TestSuiteElement)fTestRunSession.createTestElement(fTestSuite, getNextId(), suiteName,
                    true, 0, false, displayName, paramTypes, uniqueId);
                readTime(fTestSuite, attributes);
                fNotRun.push(Boolean.valueOf(attributes.getValue(IXmlTags.ATTR_INCOMPLETE)));
                break;
            }
        // not interested
        case IXmlTags.NODE_PROPERTIES:
        case IXmlTags.NODE_PROPERTY:
            break;
        case IXmlTags.NODE_TESTCASE:
            {
                String name = attributes.getValue(IXmlTags.ATTR_NAME);
                String classname = attributes.getValue(IXmlTags.ATTR_CLASSNAME);
                String testName = name + '(' + classname + ')';
                boolean isDynamicTest = Boolean.parseBoolean(attributes.getValue(IXmlTags.ATTR_DYNAMIC_TEST));
                String displayName = attributes.getValue(IXmlTags.ATTR_DISPLAY_NAME);
                String paramTypesStr = attributes.getValue(IXmlTags.ATTR_PARAMETER_TYPES);
                String[] paramTypes;
                if (paramTypesStr != null && !paramTypesStr.trim().isEmpty())
                {
                    paramTypes = paramTypesStr.split(","); //$NON-NLS-1$
                    Arrays.parallelSetAll(paramTypes, i -> paramTypes[i].trim());
                }
                else
                {
                    paramTypes = null;
                }
                String uniqueId = attributes.getValue(IXmlTags.ATTR_UNIQUE_ID);
                if (uniqueId != null && uniqueId.trim().isEmpty())
                {
                    uniqueId = null;
                }
                fTestCase = (TestCaseElement)fTestRunSession.createTestElement(fTestSuite, getNextId(), testName, false,
                    0, isDynamicTest, displayName, paramTypes, uniqueId);
                fNotRun.push(Boolean.parseBoolean(attributes.getValue(IXmlTags.ATTR_INCOMPLETE)));
                fTestCase.setIgnored(Boolean.parseBoolean(attributes.getValue(IXmlTags.ATTR_IGNORED)));
                readTime(fTestCase, attributes);
                break;
            }
        case IXmlTags.NODE_ERROR:
            //TODO: multiple failures: https://bugs.eclipse.org/bugs/show_bug.cgi?id=125296
            fStatus = Status.ERROR;
            fFailureBuffer = new StringBuffer();
            break;
        case IXmlTags.NODE_FAILURE:
            //TODO: multiple failures: https://bugs.eclipse.org/bugs/show_bug.cgi?id=125296
            fStatus = Status.FAILURE;
            fFailureBuffer = new StringBuffer();
            break;
        case IXmlTags.NODE_EXPECTED:
            fInExpected = true;
            fExpectedBuffer = new StringBuffer();
            break;
        case IXmlTags.NODE_ACTUAL:
            fInActual = true;
            fActualBuffer = new StringBuffer();
            break;
        // not interested
        case IXmlTags.NODE_SYSTEM_OUT:
        case IXmlTags.NODE_SYSTEM_ERR:
            break;
        case IXmlTags.NODE_SKIPPED:
            // before Ant 1.9.0: not an Ant JUnit tag, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=276068
            // later: child of <suite> or <test>, see https://issues.apache.org/bugzilla/show_bug.cgi?id=43969
            fStatus = Status.OK;
            fFailureBuffer = new StringBuffer();
            String message = attributes.getValue(IXmlTags.ATTR_MESSAGE);
            if (message != null)
            {
                fFailureBuffer.append(message).append('\n');
            }
            break;
        default:
            throw new SAXParseException("unknown node '" + qName + "'", fLocator); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    @Override
    public void warning(SAXParseException e) throws SAXException
    {
        throw e;
    }

    private String getNextId()
    {
        return Integer.toString(fId++);
    }

    private void handleFailure(TestElement testElement)
    {
        if (fFailureBuffer != null)
        {
            fTestRunSession.registerTestFailureStatus(testElement, fStatus, fFailureBuffer.toString(),
                toString(fExpectedBuffer), toString(fActualBuffer));
            fFailureBuffer = null;
            fExpectedBuffer = null;
            fActualBuffer = null;
            fStatus = null;
        }
    }

    private void handleTestElementEnd(TestElement testElement)
    {
        boolean completed = fNotRun.pop() != Boolean.TRUE;
        fTestRunSession.registerTestEnded(testElement, completed);
    }

    private void handleUnknownNode(String qName) throws SAXException
    {
        //TODO: just log if debug option is enabled?
        StringBuilder msg = new StringBuilder("unknown node '").append(qName).append("'"); //$NON-NLS-1$//$NON-NLS-2$
        if (fLocator != null)
        {
            msg.append(" at line ") //$NON-NLS-1$
                .append(fLocator.getLineNumber())
                .append(", column ") //$NON-NLS-1$
                .append(fLocator.getColumnNumber());
        }
        throw new SAXException(msg.toString());
    }

    private void readTime(TestElement testElement, Attributes attributes)
    {
        var timeString = attributes.getValue(IXmlTags.ATTR_TIME);
        if (timeString != null)
        {
            try
            {
                testElement.setElapsedTimeInSeconds(Double.parseDouble(timeString));
            }
            catch (NumberFormatException e)
            {
                // Нечего делать
            }
        }
    }

    private String toString(StringBuffer buffer)
    {
        return buffer != null ? buffer.toString() : null;
    }
}
