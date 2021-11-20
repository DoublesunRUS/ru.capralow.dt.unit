/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ru.capralow.dt.unit.internal.junit.model.TestElement;
import ru.capralow.dt.unit.internal.junit.model.TestRoot;
import ru.capralow.dt.unit.internal.junit.model.TestSuiteElement;

public class TestSessionTreeContentProvider
    implements ITreeContentProvider
{

    private static final Object[] NO_CHILDREN = new Object[0];

    @Override
    public void dispose()
    {
        // Нечего делать
    }

    @Override
    public Object[] getChildren(Object parentElement)
    {
        if (parentElement instanceof TestSuiteElement)
        {
            return ((TestSuiteElement)parentElement).getChildren();
        }
        else
        {
            return NO_CHILDREN;
        }
    }

    @Override
    public Object[] getElements(Object inputElement)
    {
        return ((TestRoot)inputElement).getChildren();
    }

    @Override
    public Object getParent(Object element)
    {
        return ((TestElement)element).getParent();
    }

    @Override
    public boolean hasChildren(Object element)
    {
        if (element instanceof TestSuiteElement)
        {
            return ((TestSuiteElement)element).getChildren().length != 0;
        }

        return false;
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        // Нечего делать
    }
}
