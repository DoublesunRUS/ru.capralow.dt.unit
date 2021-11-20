/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ru.capralow.dt.unit.internal.junit.model.TestCaseElement;
import ru.capralow.dt.unit.internal.junit.model.TestRoot;
import ru.capralow.dt.unit.internal.junit.model.TestSuiteElement;
import ru.capralow.dt.unit.junit.model.ITestElement;

/**
 * @author Aleksandr Kapralov
 *
 */
public class TestSessionTableContentProvider
    implements IStructuredContentProvider
{

    @Override
    public void dispose()
    {
        // Нечего делать
    }

    @Override
    public Object[] getElements(Object inputElement)
    {
        ArrayList<ITestElement> all = new ArrayList<>();
        addAll(all, (TestRoot)inputElement);
        return all.toArray();
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        // Нечего делать
    }

    private void addAll(ArrayList<ITestElement> all, TestSuiteElement suite)
    {
        ITestElement[] children = suite.getChildren();
        for (ITestElement element : children)
        {
            if (element instanceof TestSuiteElement)
            {
                if (((TestSuiteElement)element).getSuiteStatus().isErrorOrFailure())
                {
                    all.add(element); // add failed suite to flat list too
                }
                addAll(all, (TestSuiteElement)element);
            }
            else if (element instanceof TestCaseElement)
            {
                all.add(element);
            }
        }
    }
}
