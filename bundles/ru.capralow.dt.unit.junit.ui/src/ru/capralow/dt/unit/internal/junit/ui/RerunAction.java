/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

/**
 * Requests to rerun a test.
 */
public class RerunAction
    extends Action
{
    private String fTestId;
    private String fClassName;
    private String fTestName;
    private String fTestDisplayName;
    private TestRunnerViewPart fTestRunner;
    private String fUniqueId;
    private String fLaunchMode;

    /**
     * Constructor for RerunAction.
     * @param actionName the name of the action
     * @param runner the JUnit view
     * @param testId the test id
     * @param className the class name containing the test
     * @param testName the method to run or <code>null</code>
     * @param testDisplayName the display name of the test to re-run or <code>null</code>
     * @param uniqueId the unique ID of the test to re-run or <code>null</code>
     * @param launchMode the launch mode
     */
    public RerunAction(String actionName, TestRunnerViewPart runner, String testId, String className, String testName,
        String testDisplayName, String uniqueId, String launchMode)
    {
        super(actionName);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJunitHelpContextIds.RERUN_ACTION);
        fTestRunner = runner;
        fTestId = testId;
        fClassName = className;
        fTestName = testName;
        fTestDisplayName = testDisplayName;
        fUniqueId = uniqueId;
        fLaunchMode = launchMode;
    }

    @Override
    public void run()
    {
        fTestRunner.rerunTest(fTestId, fClassName, fTestName, fTestDisplayName, fUniqueId, fLaunchMode);
    }
}
