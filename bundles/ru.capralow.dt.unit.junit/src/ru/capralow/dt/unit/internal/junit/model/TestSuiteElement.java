/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.model;

import java.util.ArrayList;
import java.util.List;

import ru.capralow.dt.unit.junit.model.ITestElement;
import ru.capralow.dt.unit.junit.model.ITestSuiteElement;

public class TestSuiteElement
    extends TestElement
    implements ITestSuiteElement
{

    private List<TestElement> fChildren;
    private Status fChildrenStatus;

    public TestSuiteElement(TestSuiteElement parent, String id, String testName, int childrenCount, String displayName,
        String[] parameterTypes, String uniqueId)
    {
        super(parent, id, testName, displayName, parameterTypes, uniqueId);
        fChildren = new ArrayList<>(childrenCount);
    }

    public void addChild(TestElement child)
    {
        fChildren.add(child);
    }

    public void childChangedStatus(TestElement child, Status childStatus)
    {
        int childCount = fChildren.size();
        if (child == fChildren.get(0) && childStatus.isRunning())
        {
            // is first child, and is running -> copy status
            internalSetChildrenStatus(childStatus);
            return;
        }
        TestElement lastChild = fChildren.get(childCount - 1);
        if (child == lastChild)
        {
            if (childStatus.isDone())
            {
                // all children done, collect cumulative status
                internalSetChildrenStatus(getCumulatedStatus());
                return;
            }
            // go on (child could e.g. be a TestSuiteElement with RUNNING_FAILURE)

        }
        else if (!lastChild.getStatus().isNotRun())
        {
            // child is not last, but last child has been run -> child has been rerun or is rerunning
            internalSetChildrenStatus(getCumulatedStatus());
            return;
        }

        // finally, set RUNNING_FAILURE/ERROR if child has failed but suite has not failed:
        if (childStatus.isFailure())
        {
            if (fChildrenStatus == null || !fChildrenStatus.isErrorOrFailure())
            {
                internalSetChildrenStatus(Status.RUNNING_FAILURE);
                return;
            }
        }
        else if (childStatus.isError())
        {
            if (fChildrenStatus == null || !fChildrenStatus.isError())
            {
                internalSetChildrenStatus(Status.RUNNING_ERROR);
                return;
            }
        }
    }

    @Override
    public ITestElement[] getChildren()
    {
        return fChildren.toArray(new ITestElement[fChildren.size()]);
    }

    @Override
    public Status getStatus()
    {
        Status suiteStatus = getSuiteStatus();
        if (fChildrenStatus != null)
        {
            // must combine children and suite status here, since failures can occur e.g. in @AfterClass
            return Status.combineStatus(fChildrenStatus, suiteStatus);
        }
        else
        {
            return suiteStatus;
        }
    }

    public Status getSuiteStatus()
    {
        return super.getStatus();
    }

    @Override
    public String getSuiteTypeName()
    {
        return getClassName();
    }

    @Override
    public Result getTestResult(boolean includeChildren)
    {
        if (includeChildren)
        {
            return getStatus().convertToResult();
        }
        else
        {
            return super.getStatus().convertToResult();
        }
    }

    public void removeChild(TestElement child)
    {
        fChildren.remove(child);
    }

    @Override
    public String toString()
    {
        return "TestSuite: " + getTestName() + " : " + super.toString() + " (" + fChildren.size() + ")"; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

    private Status getCumulatedStatus()
    {
        // copy list to avoid concurreny problems
        TestElement[] children = fChildren.toArray(new TestElement[fChildren.size()]);
        if (children.length == 0)
            return getSuiteStatus();

        Status cumulated = children[0].getStatus();

        for (int i = 1; i < children.length; i++)
        {
            Status childStatus = children[i].getStatus();
            cumulated = Status.combineStatus(cumulated, childStatus);
        }
        return cumulated;
    }

    private void internalSetChildrenStatus(Status status)
    {
        if (fChildrenStatus == status)
            return;

        if (status == Status.RUNNING)
        {
            if (fTime >= 0.0d)
            {
                // re-running child: ignore change
            }
            else
            {
                fTime = -System.currentTimeMillis() / 1000d;
            }
        }
        else if (status.convertToProgressState() == ProgressState.COMPLETED)
        {
            if (fTime < 0)
            { // assert ! Double.isNaN(fTime)
                double endTime = System.currentTimeMillis() / 1000d;
                fTime = endTime + fTime;
            }
        }

        fChildrenStatus = status;
        TestSuiteElement parent = getParent();
        if (parent != null)
            parent.childChangedStatus(this, getStatus());
    }

}
