package ru.capralow.dt.unit.internal.junit.model;

import org.eclipse.core.runtime.Assert;

import ru.capralow.dt.unit.junit.model.ITestCaseElement;

public class TestCaseElement
    extends TestElement
    implements ITestCaseElement
{

    private boolean fIgnored;
    private boolean fIsDynamicTest;

    public TestCaseElement(TestSuiteElement parent, String id, String testName, String displayName,
        boolean isDynamicTest, String[] parameterTypes, String uniqueId)
    {
        super(parent, id, testName, displayName, parameterTypes, uniqueId);
        Assert.isNotNull(parent);
        fIsDynamicTest = isDynamicTest;
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.jdt.junit.model.ITestCaseElement#getTestClassName()
     */
    @Override
    public String getTestClassName()
    {
        return getClassName();
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.jdt.junit.model.ITestCaseElement#getTestMethodName()
     * @see ru.capralow.dt.unit.internal.junit.runner.jdt.internal.junit.runner.MessageIds#TEST_IDENTIFIER_MESSAGE_FORMAT
     * @see ru.capralow.dt.unit.internal.junit.runner.jdt.internal.junit.runner.MessageIds#IGNORED_TEST_PREFIX
     */
    @Override
    public String getTestMethodName()
    {
        String testName = getTestName();
        int index = testName.lastIndexOf('(');
        if (index > 0)
            return testName.substring(0, index);
        index = testName.indexOf('@');
        if (index > 0)
            return testName.substring(0, index);
        return testName;
    }

    /*
     * @see org.eclipse.jdt.internal.junit.model.TestElement#getTestResult(boolean)
     */
    @Override
    public Result getTestResult(boolean includeChildren)
    {
        if (fIgnored)
            return Result.IGNORED;
        else
            return super.getTestResult(includeChildren);
    }

    public boolean isDynamicTest()
    {
        return fIsDynamicTest;
    }

    public boolean isIgnored()
    {
        return fIgnored;
    }

    public void setIgnored(boolean ignored)
    {
        fIgnored = ignored;
    }

    @Override
    public String toString()
    {
        return "TestCase: " + getTestClassName() + "." + getTestMethodName() + " : " + super.toString(); //$NON-NLS-2$ //$NON-NLS-3$
    }
}
