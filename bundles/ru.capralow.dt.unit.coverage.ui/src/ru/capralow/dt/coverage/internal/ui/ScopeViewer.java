/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;

/**
 * Viewer for selecting {@link Module} objects from a given list.
 */
public class ScopeViewer
    implements ISelectionProvider
{

    /**
     * Calculates a label for the class path of the given package fragment root. For
     * external entries this is the full path, otherwise it is the project relative
     * path.
     *
     * @param root package fragment root
     * @return label for the class path entry
     */
    private static String getPathLabel(URI root, IResourceLookup resourceLookup)
    {
        IFile moduleFile = resourceLookup.getPlatformResource(root);

        final IPath path = moduleFile.getFullPath();
        return path.removeFirstSegments(2).toString();
    }

    private final Table table;

    private final CheckboxTableViewer viewer;

    private final List<ISelectionChangedListener> listeners = new ArrayList<>();

    /**
     * Creates a new viewer within the given parent.
     *
     * @param parent composite to create the viewer's table in
     * @param style flags specifying the table's style
     */
    public ScopeViewer(Composite parent, int style, IV8ProjectManager projectManager, IResourceLookup resourceLookup)
    {
        this(new Table(parent, SWT.CHECK | style), projectManager, resourceLookup);
    }

    /**
     * Attaches the viewer to the given table.
     *
     * @param table view table
     */
    public ScopeViewer(Table table, IV8ProjectManager projectManager, IResourceLookup resourceLookup)
    {
        this.table = table;
        viewer = new CheckboxTableViewer(table);
        viewer.setContentProvider(ArrayContentProvider.getInstance());
        viewer.setLabelProvider(new UriLabelProvider(projectManager, resourceLookup));
        viewer.setSorter(new ModuleSorter(resourceLookup));
        viewer.addFilter(new ViewerFilter()
        {
            @Override
            public boolean select(Viewer viewer1, Object parentElement, Object element)
            {
                return element instanceof URI;
            }
        });
        viewer.addCheckStateListener(event -> fireSelectionEvent());
    }

    /**
     * Registers the given selection listener if not already registered.
     *
     * @param listener listener to add
     */
    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener)
    {
        if (!listeners.contains(listener))
        {
            listeners.add(listener);
        }
    }

    public void deselectAll()
    {
        viewer.setAllChecked(false);
        fireSelectionEvent();
    }

    /**
     * Returns the currently selected scope.
     *
     * @return list of package fragment roots that are currently checked
     */
    public Set<URI> getSelectedScope()
    {
        Set<URI> scope = new HashSet<>();
        for (final Object element : viewer.getCheckedElements())
        {
            scope.add((URI)element);
        }
        return scope;
    }

    @Override
    public ISelection getSelection()
    {
        return new StructuredSelection(getSelectedScope().toArray());
    }

    /**
     * Returns the table used by the viewer.
     *
     * @return table used by the viewer
     */
    public Table getTable()
    {
        return table;
    }

    /**
     * Removes the given selection listener.
     *
     * @param listener listener to remove
     */
    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener)
    {
        listeners.remove(listener);
    }

    public void selectAll()
    {
        viewer.setAllChecked(true);
        fireSelectionEvent();
    }

    /**
     * Sets the input for this viewer.
     *
     * @param set list of {@link IPackageFragmentRoot}s the user can select from
     */
    public void setInput(Set<URI> set)
    {
        viewer.setInput(set);
    }

    /**
     * Sets the selected scope.
     *
     * @param set list of package fragment roots that should be checked
     */
    public void setSelectedScope(final Set<URI> set)
    {
        viewer.setCheckedElements(set.toArray());
    }

    @Override
    public void setSelection(ISelection selection)
    {
        List<URI> scope = new ArrayList<>();
        for (final Object obj : ((IStructuredSelection)selection).toArray())
        {
            scope.add((URI)obj);
        }
        setSelectedScope(scope.stream().collect(Collectors.toSet()));
    }

    private void fireSelectionEvent()
    {
        SelectionChangedEvent evt = new SelectionChangedEvent(this, getSelection());
        for (final ISelectionChangedListener l : listeners)
        {
            l.selectionChanged(evt);
        }
    }

    // ISelectionProvider interface

    /**
     * The entries will be sorted by project name, type and path name.
     */
    private static class ModuleSorter
        extends ViewerSorter
    {

        private IResourceLookup resourceLookup;

        ModuleSorter(IResourceLookup resourceLookup)
        {
            this.resourceLookup = resourceLookup;
        }

        @Override
        public int compare(Viewer viewer, Object e1, Object e2)
        {
            URI root1 = (URI)e1;
            URI root2 = (URI)e2;
            @SuppressWarnings("rawtypes")
            final Comparator comparator = getComparator();
            return comparator.compare(getPathLabel(root1, resourceLookup), getPathLabel(root2, resourceLookup));
        }

    }

    private static class UriLabelProvider
        extends LabelProvider
    {

        private ILabelProvider delegate = new WorkbenchLabelProvider();

        private IResourceLookup resourceLookup;
        private IV8ProjectManager projectManager;

        UriLabelProvider(IV8ProjectManager projectManager, IResourceLookup resourceLookup)
        {
            this.projectManager = projectManager;
            this.resourceLookup = resourceLookup;
        }

        @Override
        public void dispose()
        {
            delegate.dispose();
        }

        @Override
        public Image getImage(Object element)
        {
            return delegate.getImage(element);
        }

        @Override
        public String getText(Object element)
        {
            URI root = (URI)element;
            String projectname = projectManager.getProject(root).getProject().getName();
            String path = getPathLabel(root, resourceLookup);
            if (path.length() > 0)
            {
                String fmt = UiMessages.ClassesViewerEntry_label;
                return NLS.bind(fmt, projectname, getPathLabel(root, resourceLookup));
            }
            else
            {
                return projectname;
            }
        }

    }

}
