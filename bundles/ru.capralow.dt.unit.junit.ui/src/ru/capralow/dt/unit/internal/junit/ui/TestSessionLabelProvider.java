/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

import java.text.MessageFormat;
import java.text.NumberFormat;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import ru.capralow.dt.unit.internal.junit.BasicElementLabels;
import ru.capralow.dt.unit.internal.junit.model.TestCaseElement;
import ru.capralow.dt.unit.internal.junit.model.TestElement;
import ru.capralow.dt.unit.internal.junit.model.TestElement.Status;
import ru.capralow.dt.unit.internal.junit.model.TestSuiteElement;
import ru.capralow.dt.unit.junit.model.ITestElement;

public class TestSessionLabelProvider
    extends LabelProvider
    implements IStyledLabelProvider
{

    private final TestRunnerViewPart fTestRunnerPart;
    private final int fLayoutMode;
    private final NumberFormat timeFormat;

    private boolean fShowTime;

    public TestSessionLabelProvider(TestRunnerViewPart testRunnerPart, int layoutMode)
    {
        fTestRunnerPart = testRunnerPart;
        fLayoutMode = layoutMode;
        fShowTime = true;

        timeFormat = NumberFormat.getNumberInstance();
        timeFormat.setGroupingUsed(true);
        timeFormat.setMinimumFractionDigits(3);
        timeFormat.setMaximumFractionDigits(3);
        timeFormat.setMinimumIntegerDigits(1);
    }

    @Override
    public Image getImage(Object element)
    {
        if (element instanceof TestElement && ((TestElement)element).isAssumptionFailure())
            return fTestRunnerPart.fTestAssumptionFailureIcon;

        if (element instanceof TestCaseElement)
        {
            TestCaseElement testCaseElement = ((TestCaseElement)element);
            if (testCaseElement.isIgnored())
                return fTestRunnerPart.fTestIgnoredIcon;

            Status status = testCaseElement.getStatus();
            if (status.isNotRun())
                return fTestRunnerPart.fTestIcon;
            else if (status.isRunning())
                return fTestRunnerPart.fTestRunningIcon;
            else if (status.isError())
                return fTestRunnerPart.fTestErrorIcon;
            else if (status.isFailure())
                return fTestRunnerPart.fTestFailIcon;
            else if (status.isOK())
                return fTestRunnerPart.fTestOkIcon;
            else
                throw new IllegalStateException(element.toString());

        }
        else if (element instanceof TestSuiteElement)
        {
            Status status = ((TestSuiteElement)element).getStatus();
            if (status.isNotRun())
                return fTestRunnerPart.fSuiteIcon;
            else if (status.isRunning())
                return fTestRunnerPart.fSuiteRunningIcon;
            else if (status.isError())
                return fTestRunnerPart.fSuiteErrorIcon;
            else if (status.isFailure())
                return fTestRunnerPart.fSuiteFailIcon;
            else if (status.isOK())
                return fTestRunnerPart.fSuiteOkIcon;
            else
                throw new IllegalStateException(element.toString());

        }
        else
        {
            throw new IllegalArgumentException(String.valueOf(element));
        }
    }

    @Override
    public StyledString getStyledText(Object element)
    {
        String label = getSimpleLabel(element);
        if (label == null)
        {
            return new StyledString(element.toString());
        }
        StyledString text = new StyledString(label);

        ITestElement testElement = (ITestElement)element;
        if (element instanceof TestCaseElement)
        {
            String decorated = getTextForFlatLayout((TestCaseElement)testElement, label);
            text = StyledCellLabelProvider.styleDecoratedString(decorated, StyledString.QUALIFIER_STYLER, text);
        }
        return addElapsedTime(text, testElement.getElapsedTimeInSeconds());
    }

    @Override
    public String getText(Object element)
    {
        String label = getSimpleLabel(element);
        if (label == null)
        {
            return element.toString();
        }
        ITestElement testElement = (ITestElement)element;
        if (element instanceof TestCaseElement)
        {
            label = getTextForFlatLayout((TestCaseElement)testElement, label);
        }
        return addElapsedTime(label, testElement.getElapsedTimeInSeconds());
    }

    public void setShowTime(boolean showTime)
    {
        fShowTime = showTime;
        fireLabelProviderChanged(new LabelProviderChangedEvent(this));
    }

    private String addElapsedTime(String string, double time)
    {
        if (!fShowTime || Double.isNaN(time))
        {
            return string;
        }
        String formattedTime = timeFormat.format(time);
        return MessageFormat.format(Messages.TestSessionLabelProvider_testName_elapsedTimeInSeconds,
            new String[] { string, formattedTime });
    }

    private StyledString addElapsedTime(StyledString styledString, double time)
    {
        String string = styledString.getString();
        String decorated = addElapsedTime(string, time);
        return StyledCellLabelProvider.styleDecoratedString(decorated, StyledString.COUNTER_STYLER, styledString);
    }

    private String getSimpleLabel(Object element)
    {
        if (element instanceof TestCaseElement)
        {
            TestCaseElement testCaseElement = (TestCaseElement)element;
            String displayName = testCaseElement.getDisplayName();
            return BasicElementLabels
                .getJavaElementName(displayName != null ? displayName : testCaseElement.getTestMethodName());
        }
        else if (element instanceof TestSuiteElement)
        {
            TestSuiteElement testSuiteElement = (TestSuiteElement)element;
            String displayName = testSuiteElement.getDisplayName();
            return BasicElementLabels
                .getJavaElementName(displayName != null ? displayName : testSuiteElement.getSuiteTypeName());
        }
        return null;
    }

    private String getTextForFlatLayout(TestCaseElement testCaseElement, String label)
    {
        String parentName;
        String parentDisplayName = testCaseElement.getParent().getDisplayName();
        if (parentDisplayName != null)
        {
            parentName = parentDisplayName;
        }
        else
        {
            if (testCaseElement.isDynamicTest())
            {
                parentName = testCaseElement.getTestMethodName();
            }
            else
            {
                parentName = testCaseElement.getTestClassName();
            }
        }
        return MessageFormat.format(Messages.TestSessionLabelProvider_testMethodName_className,
            new Object[] { label, BasicElementLabels.getJavaElementName(parentName) });
    }

}
