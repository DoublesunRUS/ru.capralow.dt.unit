/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.PageBook;

import ru.capralow.dt.unit.internal.junit.model.TestCaseElement;
import ru.capralow.dt.unit.internal.junit.model.TestElement;
import ru.capralow.dt.unit.internal.junit.model.TestElement.Status;
import ru.capralow.dt.unit.internal.junit.model.TestRoot;
import ru.capralow.dt.unit.internal.junit.model.TestRunSession;
import ru.capralow.dt.unit.internal.junit.model.TestSuiteElement;
import ru.capralow.dt.unit.internal.junit.ui.TestRunnerViewPart.SortingCriterion;
import ru.capralow.dt.unit.internal.junit.ui.viewsupport.ColoringLabelProvider;
import ru.capralow.dt.unit.internal.junit.ui.viewsupport.SelectionProviderMediator;
import ru.capralow.dt.unit.junit.model.ITestElement;
import ru.capralow.dt.unit.junit.model.ITestElement.Result;

public class TestViewer
{
    private final FailuresOnlyFilter fFailuresOnlyFilter = new FailuresOnlyFilter();

    private final IgnoredOnlyFilter fIgnoredOnlyFilter = new IgnoredOnlyFilter();

    private final TestRunnerViewPart fTestRunnerPart;

    private final Clipboard fClipboard;

    private PageBook fViewerbook;

    private TreeViewer fTreeViewer;

    private TestSessionTreeContentProvider fTreeContentProvider;

    private TestSessionLabelProvider fTreeLabelProvider;
    private TableViewer fTableViewer;

    private TestSessionLabelProvider fTableLabelProvider;

    private SelectionProviderMediator fSelectionProvider;
    private int fLayoutMode;
    private boolean fTreeHasFilter;
    private boolean fTableHasFilter;
    private TestRunSession fTestRunSession;
    private boolean fTreeNeedsRefresh;
    private boolean fTableNeedsRefresh;
    private HashSet<TestElement> fNeedUpdate;

    private TestCaseElement fAutoScrollTarget;
    private LinkedList<TestSuiteElement> fAutoClose;
    private HashSet<TestSuiteElement> fAutoExpand;

    public TestViewer(Composite parent, Clipboard clipboard, TestRunnerViewPart runner)
    {
        fTestRunnerPart = runner;
        fClipboard = clipboard;

        fLayoutMode = TestRunnerViewPart.LAYOUT_HIERARCHICAL;

        createTestViewers(parent);

        registerViewersRefresh();

        initContextMenu();
    }

    public void expandFirstLevel()
    {
        fTreeViewer.expandToLevel(2);
    }

    public StructuredViewer getActiveViewer()
    {
        if (fLayoutMode == TestRunnerViewPart.LAYOUT_HIERARCHICAL)
            return fTreeViewer;
        else
            return fTableViewer;
    }

    public Control getTestViewerControl()
    {
        return fViewerbook;
    }

    /**
     * To be called periodically by the TestRunnerViewPart (in the UI thread).
     */
    public void processChangesInUI()
    {
        TestRoot testRoot;
        if (fTestRunSession == null)
        {
            registerViewersRefresh();
            fTreeNeedsRefresh = false;
            fTableNeedsRefresh = false;
            fTreeViewer.setInput(null);
            fTableViewer.setInput(null);
            return;
        }

        testRoot = fTestRunSession.getTestRoot();

        StructuredViewer viewer = getActiveViewer();
        if (getActiveViewerNeedsRefresh())
        {
            clearUpdateAndExpansion();
            setActiveViewerNeedsRefresh(false);
            viewer.setInput(testRoot);

        }
        else
        {
            Object[] toUpdate;
            synchronized (this)
            {
                toUpdate = fNeedUpdate.toArray();
                fNeedUpdate.clear();
            }
            if (!fTreeNeedsRefresh && toUpdate.length > 0)
            {
                if (fTreeHasFilter)
                    for (Object element : toUpdate)
                        updateElementInTree((TestElement)element);
                else
                {
                    HashSet<Object> toUpdateWithParents = new HashSet<>(Arrays.asList(toUpdate));
                    for (Object element : toUpdate)
                    {
                        TestElement parent = ((TestElement)element).getParent();
                        while (parent != null)
                        {
                            toUpdateWithParents.add(parent);
                            parent = parent.getParent();
                        }
                    }
                    fTreeViewer.update(toUpdateWithParents.toArray(), null);
                }
            }
            if (!fTableNeedsRefresh && toUpdate.length > 0)
            {
                if (fTableHasFilter)
                    for (Object element : toUpdate)
                        updateElementInTable((TestElement)element);
                else
                    fTableViewer.update(toUpdate, null);
            }
        }
        autoScrollInUI();
    }

    public synchronized void registerActiveSession(TestRunSession testRunSession)
    {
        fTestRunSession = testRunSession;
        registerAutoScrollTarget(null);
        registerViewersRefresh();
    }

    public void registerAutoScrollTarget(TestCaseElement testCaseElement)
    {
        fAutoScrollTarget = testCaseElement;
    }

    public synchronized void registerFailedForAutoScroll(TestElement testElement)
    {
        TestSuiteElement parent = (TestSuiteElement)fTreeContentProvider.getParent(testElement);
        if (parent != null)
            fAutoExpand.add(parent);
    }

    /**
     * @param testElement the added test
     */
    public synchronized void registerTestAdded(TestElement testElement)
    {
        //TODO: performance: would only need to refresh parent of added element
        fTreeNeedsRefresh = true;
        fTableNeedsRefresh = true;
    }

    public synchronized void registerViewersRefresh()
    {
        fTreeNeedsRefresh = true;
        fTableNeedsRefresh = true;
        clearUpdateAndExpansion();
    }

    public synchronized void registerViewerUpdate(final TestElement testElement)
    {
        fNeedUpdate.add(testElement);
    }

    public void selectFailure(boolean showNext)
    {
        IStructuredSelection selection = (IStructuredSelection)getActiveViewer().getSelection();
        TestElement selected = (TestElement)selection.getFirstElement();
        TestElement next;

        if (selected == null)
        {
            next = getNextChildFailure(fTestRunSession.getTestRoot(), showNext);
        }
        else
        {
            next = getNextFailure(selected, showNext);
        }

        if (next != null)
            getActiveViewer().setSelection(new StructuredSelection(next), true);
    }

    public void selectFirstFailure()
    {
        TestElement firstFailure = getNextChildFailure(fTestRunSession.getTestRoot(), true);
        if (firstFailure != null)
            getActiveViewer().setSelection(new StructuredSelection(firstFailure), true);
    }

    /**
     * It makes sense to display either failed or ignored tests, not both together.
     *
     * @param failuresOnly whether to show only failed tests
     * @param ignoredOnly whether to show only skipped tests
     * @param layoutMode the layout mode
     */
    public synchronized void setShowFailuresOrIgnoredOnly(boolean failuresOnly, boolean ignoredOnly, int layoutMode)
    {
        /*
         * Management of fTreeViewer and fTableViewer
         * ******************************************
         * - invisible viewer is updated on registerViewerUpdate unless its f*NeedsRefresh is true
         * - invisible viewer is not refreshed upfront
         * - on layout change, new viewer is refreshed if necessary
         * - filter only applies to "current" layout mode / viewer
         */
        try
        {
            fViewerbook.setRedraw(false);

            IStructuredSelection selection = null;
            boolean switchLayout = layoutMode != fLayoutMode;
            if (switchLayout)
            {
                selection = (IStructuredSelection)fSelectionProvider.getSelection();
                if (layoutMode == TestRunnerViewPart.LAYOUT_HIERARCHICAL)
                {
                    if (fTreeNeedsRefresh)
                    {
                        clearUpdateAndExpansion();
                    }
                }
                else
                {
                    if (fTableNeedsRefresh)
                    {
                        clearUpdateAndExpansion();
                    }
                }
                fLayoutMode = layoutMode;
                fViewerbook.showPage(getActiveViewer().getControl());
            }
            //avoid realizing all TableItems, especially in flat mode!
            StructuredViewer viewer = getActiveViewer();
            if (failuresOnly || ignoredOnly)
            {
                if (getActiveViewerHasFilter())
                {
                    //For simplicity clear both filters (only one of them is used)
                    viewer.removeFilter(fFailuresOnlyFilter);
                    viewer.removeFilter(fIgnoredOnlyFilter);
                }
                setActiveViewerHasFilter(true);
                viewer.setInput(null);
                //Set either the failures or the skipped tests filter
                ViewerFilter filter = fFailuresOnlyFilter;
                if (ignoredOnly)
                {
                    filter = fIgnoredOnlyFilter;
                }
                viewer.addFilter(filter);
                setActiveViewerNeedsRefresh(true);

            }
            else
            {
                if (getActiveViewerHasFilter())
                {
                    setActiveViewerNeedsRefresh(true);
                    setActiveViewerHasFilter(false);
                    viewer.setInput(null);
                    viewer.removeFilter(fIgnoredOnlyFilter);
                    viewer.removeFilter(fFailuresOnlyFilter);
                }
            }
            processChangesInUI();

            if (selection != null)
            {
                // workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=125708
                // (ITreeSelection not adapted if TreePaths changed):
                StructuredSelection flatSelection = new StructuredSelection(selection.toList());
                fSelectionProvider.setSelection(flatSelection, true);
            }

        }
        finally
        {
            fViewerbook.setRedraw(true);
        }
    }

    public synchronized void setShowTime(boolean showTime)
    {
        try
        {
            fViewerbook.setRedraw(false);
            fTreeLabelProvider.setShowTime(showTime);
            fTableLabelProvider.setShowTime(showTime);
        }
        finally
        {
            fViewerbook.setRedraw(true);
        }
    }

    public synchronized void setSortingCriterion(SortingCriterion sortingCriterion)
    {
        ViewerComparator viewerComparator;
        switch (sortingCriterion)
        {
        case SORT_BY_EXECUTION_ORDER:
            viewerComparator = null;
            break;
        case SORT_BY_EXECUTION_TIME:
            viewerComparator = new TestExecutionTimeComparator();
            break;
        case SORT_BY_NAME:
            viewerComparator = new TestNameComparator();
            break;
        default:
            viewerComparator = null;
            break;
        }
        fTableViewer.setComparator(viewerComparator);
        fTreeViewer.setComparator(viewerComparator);
    }

    private void addRerunActions(IMenuManager manager, TestCaseElement testCaseElement)
    {
        String className = testCaseElement.getClassName();
        String testMethodName = testCaseElement.getTestMethodName();
        String[] parameterTypes = testCaseElement.getParameterTypes();
        if (parameterTypes != null)
        {
            String paramTypesStr = Arrays.stream(parameterTypes).collect(Collectors.joining(",")); //$NON-NLS-1$
            testMethodName = testMethodName + "(" + paramTypesStr + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        manager.add(new RerunAction(Messages.RerunAction_label_run, fTestRunnerPart, testCaseElement.getId(), className,
            testMethodName, testCaseElement.getDisplayName(), testCaseElement.getUniqueId(), ILaunchManager.RUN_MODE));
        manager.add(new RerunAction(Messages.RerunAction_label_debug, fTestRunnerPart, testCaseElement.getId(),
            className, testMethodName, testCaseElement.getDisplayName(), testCaseElement.getUniqueId(),
            ILaunchManager.DEBUG_MODE));
    }

    private void addRerunActions(IMenuManager manager, TestSuiteElement testSuiteElement)
    {
        String qualifiedName = null;
        String testMethodName = null; // test method name is null when re-running a regular test class

        String testName = testSuiteElement.getTestName();

        // TODO: Реализовать получение имени метода

        if (qualifiedName != null)
        {
            manager.add(new RerunAction(Messages.RerunAction_label_run, fTestRunnerPart, testSuiteElement.getId(),
                qualifiedName, testMethodName, testSuiteElement.getDisplayName(), testSuiteElement.getUniqueId(),
                ILaunchManager.RUN_MODE));
            manager.add(new RerunAction(Messages.RerunAction_label_debug, fTestRunnerPart, testSuiteElement.getId(),
                qualifiedName, testMethodName, testSuiteElement.getDisplayName(), testSuiteElement.getUniqueId(),
                ILaunchManager.DEBUG_MODE));
        }
    }

    private void autoScrollInUI()
    {
        if (!fTestRunnerPart.isAutoScroll())
        {
            clearAutoExpand();
            fAutoClose.clear();
            return;
        }

        if (fLayoutMode == TestRunnerViewPart.LAYOUT_FLAT)
        {
            if (fAutoScrollTarget != null)
                fTableViewer.reveal(fAutoScrollTarget);
            return;
        }

        synchronized (this)
        {
            for (TestSuiteElement suite : fAutoExpand)
            {
                fTreeViewer.setExpandedState(suite, true);
            }
            clearAutoExpand();
        }

        TestCaseElement current = fAutoScrollTarget;
        fAutoScrollTarget = null;

        TestSuiteElement parent = current == null ? null : (TestSuiteElement)fTreeContentProvider.getParent(current);
        if (fAutoClose.isEmpty() || !fAutoClose.getLast().equals(parent))
        {
            // we're in a new branch, so let's close old OK branches:
            for (ListIterator<TestSuiteElement> iter = fAutoClose.listIterator(fAutoClose.size()); iter.hasPrevious();)
            {
                TestSuiteElement previousAutoOpened = iter.previous();
                if (previousAutoOpened.equals(parent))
                    break;

                if (previousAutoOpened.getStatus() == TestElement.Status.OK)
                {
                    // auto-opened the element, and all children are OK -> auto close
                    iter.remove();
                    fTreeViewer.collapseToLevel(previousAutoOpened, AbstractTreeViewer.ALL_LEVELS);
                }
            }

            while (parent != null && !fTestRunSession.getTestRoot().equals(parent)
                && !fTreeViewer.getExpandedState(parent))
            {
                fAutoClose.add(parent); // add to auto-opened elements -> close later if STATUS_OK
                parent = (TestSuiteElement)fTreeContentProvider.getParent(parent);
            }
        }
        if (current != null)
            fTreeViewer.reveal(current);
    }

    private synchronized void clearAutoExpand()
    {
        fAutoExpand.clear();
    }

    private void clearUpdateAndExpansion()
    {
        fNeedUpdate = new LinkedHashSet<>();
        fAutoClose = new LinkedList<>();
        fAutoExpand = new HashSet<>();
    }

    private int compareElapsedTime(Object o1, Object o2)
    {
        double elapsedTime1 = ((TestElement)o1).getElapsedTimeInSeconds();
        double elapsedTime2 = ((TestElement)o2).getElapsedTimeInSeconds();
        return Double.compare(elapsedTime2, elapsedTime1);
    }

    private int compareName(Object o1, Object o2)
    {
        String testName1 = ((TestElement)o1).getTestName();
        String testName2 = ((TestElement)o2).getTestName();
        return testName1.toLowerCase().compareTo(testName2.toLowerCase());
    }

    private void createTestViewers(Composite parent)
    {
        fViewerbook = new PageBook(parent, SWT.NULL);

        fTreeViewer = new TreeViewer(fViewerbook, SWT.V_SCROLL | SWT.SINGLE);
        fTreeViewer.setUseHashlookup(true);
        fTreeContentProvider = new TestSessionTreeContentProvider();
        fTreeViewer.setContentProvider(fTreeContentProvider);
        fTreeLabelProvider = new TestSessionLabelProvider(fTestRunnerPart, TestRunnerViewPart.LAYOUT_HIERARCHICAL);
        fTreeViewer.setLabelProvider(new ColoringLabelProvider(fTreeLabelProvider));

        fTableViewer = new TableViewer(fViewerbook, SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE);
        fTableViewer.setUseHashlookup(true);
        TestSessionTableContentProvider fTableContentProvider = new TestSessionTableContentProvider();
        fTableViewer.setContentProvider(fTableContentProvider);
        fTableLabelProvider = new TestSessionLabelProvider(fTestRunnerPart, TestRunnerViewPart.LAYOUT_FLAT);
        fTableViewer.setLabelProvider(new ColoringLabelProvider(fTableLabelProvider));

        fSelectionProvider =
            new SelectionProviderMediator(new StructuredViewer[] { fTreeViewer, fTableViewer }, fTreeViewer);
        fSelectionProvider.addSelectionChangedListener(new TestSelectionListener());
        TestOpenListener testOpenListener = new TestOpenListener();
        fTreeViewer.getTree().addSelectionListener(testOpenListener);
        fTableViewer.getTable().addSelectionListener(testOpenListener);

        fTestRunnerPart.getSite().setSelectionProvider(fSelectionProvider);

        fViewerbook.showPage(fTreeViewer.getTree());
    }

    private boolean getActiveViewerHasFilter()
    {
        if (fLayoutMode == TestRunnerViewPart.LAYOUT_HIERARCHICAL)
            return fTreeHasFilter;
        else
            return fTableHasFilter;
    }

    private boolean getActiveViewerNeedsRefresh()
    {
        if (fLayoutMode == TestRunnerViewPart.LAYOUT_HIERARCHICAL)
            return fTreeNeedsRefresh;
        else
            return fTableNeedsRefresh;
    }

    private Comparator<ITestElement> getComparator()
    {
        SortingCriterion sortingCriterion = fTestRunnerPart.getSortingCriterion();
        Comparator<ITestElement> comparator;
        switch (sortingCriterion)
        {
        case SORT_BY_EXECUTION_ORDER:
            comparator = null;
            break;
        case SORT_BY_EXECUTION_TIME:
            comparator = new Comparator<>()
            {
                @Override
                public int compare(ITestElement o1, ITestElement o2)
                {
                    return compareElapsedTime(o1, o2);
                }
            };
            break;
        case SORT_BY_NAME:
            comparator = new Comparator<>()
            {
                @Override
                public int compare(ITestElement o1, ITestElement o2)
                {
                    return compareName(o1, o2);
                }
            };
            break;
        default:
            comparator = null;
            break;
        }
        return comparator;
    }

    private TestElement getNextChildFailure(TestSuiteElement root, boolean showNext)
    {
        ITestElement[] elements = root.getChildren();
        Comparator<ITestElement> comparator = getComparator();
        if (comparator != null)
        {
            Arrays.sort(elements, comparator);
        }
        List<ITestElement> children = Arrays.asList(elements);
        if (!showNext)
            children = new ReverseList<>(children);
        for (ITestElement element : children)
        {
            TestElement child = (TestElement)element;
            if (child.getStatus().isErrorOrFailure())
            {
                if (child instanceof TestCaseElement)
                {
                    return child;
                }
                else
                {
                    TestSuiteElement testSuiteElement = (TestSuiteElement)child;
                    if (testSuiteElement.getChildren().length == 0)
                    {
                        return testSuiteElement;
                    }
                    return getNextChildFailure(testSuiteElement, showNext);
                }
            }
        }
        return null;
    }

    private TestElement getNextFailure(TestElement selected, boolean showNext)
    {
        if (selected instanceof TestSuiteElement)
        {
            TestElement nextChild = getNextChildFailure((TestSuiteElement)selected, showNext);
            if (nextChild != null)
                return nextChild;
        }
        return getNextFailureSibling(selected, showNext);
    }

    private TestElement getNextFailureSibling(TestElement current, boolean showNext)
    {
        TestSuiteElement parent = current.getParent();
        if (parent == null)
            return null;

        ITestElement[] elements = parent.getChildren();
        Comparator<ITestElement> comparator = getComparator();
        if (comparator != null)
        {
            Arrays.sort(elements, comparator);
        }
        List<ITestElement> siblings = Arrays.asList(elements);

        if (!showNext)
            siblings = new ReverseList<>(siblings);

        int nextIndex = siblings.indexOf(current) + 1;
        for (int i = nextIndex; i < siblings.size(); i++)
        {
            TestElement sibling = (TestElement)siblings.get(i);
            if (sibling.getStatus().isErrorOrFailure())
            {
                if (sibling instanceof TestCaseElement)
                {
                    return sibling;
                }
                else
                {
                    TestSuiteElement testSuiteElement = (TestSuiteElement)sibling;
                    if (testSuiteElement.getChildren().length == 0)
                    {
                        return testSuiteElement;
                    }
                    return getNextChildFailure(testSuiteElement, showNext);
                }
            }
        }
        return getNextFailureSibling(parent, showNext);
    }

    private void initContextMenu()
    {
        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(this::handleMenuAboutToShow);
        fTestRunnerPart.getSite().registerContextMenu(menuMgr, fSelectionProvider);
        Menu menu = menuMgr.createContextMenu(fViewerbook);
        fTreeViewer.getTree().setMenu(menu);
        fTableViewer.getTable().setMenu(menu);
    }

    private boolean isShown(TestElement current)
    {
        return fFailuresOnlyFilter.select(current);
    }

    private void setActiveViewerHasFilter(boolean filter)
    {
        if (fLayoutMode == TestRunnerViewPart.LAYOUT_HIERARCHICAL)
            fTreeHasFilter = filter;
        else
            fTableHasFilter = filter;
    }

    private void setActiveViewerNeedsRefresh(boolean needsRefresh)
    {
        if (fLayoutMode == TestRunnerViewPart.LAYOUT_HIERARCHICAL)
            fTreeNeedsRefresh = needsRefresh;
        else
            fTableNeedsRefresh = needsRefresh;
    }

    private void updateElementInTable(TestElement element)
    {
        if (isShown(element))
        {
            if (fTableViewer.testFindItem(element) == null)
            {
                TestElement previous = getNextFailure(element, false);
                int insertionIndex = -1;
                if (previous != null)
                {
                    TableItem item = (TableItem)fTableViewer.testFindItem(previous);
                    if (item != null)
                        insertionIndex = fTableViewer.getTable().indexOf(item);
                }
                fTableViewer.insert(element, insertionIndex);
            }
            else
            {
                fTableViewer.update(element, null);
            }
        }
        else
        {
            fTableViewer.remove(element);
        }
    }

    private void updateElementInTree(final TestElement testElement)
    {
        if (isShown(testElement))
        {
            updateShownElementInTree(testElement);
        }
        else
        {
            TestElement current = testElement;
            do
            {
                if (fTreeViewer.testFindItem(current) != null)
                    fTreeViewer.remove(current);
                current = current.getParent();
            }
            while (!(current instanceof TestRoot) && !isShown(current));

            while (current != null && !(current instanceof TestRoot))
            {
                fTreeViewer.update(current, null);
                current = current.getParent();
            }
        }
    }

    private void updateShownElementInTree(TestElement testElement)
    {
        if (testElement == null || testElement instanceof TestRoot) // paranoia null check
            return;

        TestSuiteElement parent = testElement.getParent();
        updateShownElementInTree(parent); // make sure parent is shown and up-to-date

        if (fTreeViewer.testFindItem(testElement) == null)
        {
            fTreeViewer.add(parent, testElement); // if not yet in tree: add
        }
        else
        {
            fTreeViewer.update(testElement, null); // if in tree: update
        }
    }

    void handleDefaultSelected()
    {
        IStructuredSelection selection = (IStructuredSelection)fSelectionProvider.getSelection();
        if (selection.size() != 1)
            return;

        TestElement testElement = (TestElement)selection.getFirstElement();

        // TODO: Реализовать открытие теста
    }

    void handleMenuAboutToShow(IMenuManager manager)
    {
        IStructuredSelection selection = (IStructuredSelection)fSelectionProvider.getSelection();
        if (!selection.isEmpty())
        {
            TestElement testElement = (TestElement)selection.getFirstElement();

            if (testElement instanceof TestSuiteElement)
            {
                TestSuiteElement testSuiteElement = (TestSuiteElement)testElement;
//                manager.add(getOpenTestAction(testSuiteElement));
                manager.add(new Separator());
                addRerunActions(manager, testSuiteElement);
            }
            else
            {
                TestCaseElement testCaseElement = (TestCaseElement)testElement;
//                manager.add(getOpenTestAction(testCaseElement));
                manager.add(new Separator());
                addRerunActions(manager, testCaseElement);
            }
            if (fLayoutMode == TestRunnerViewPart.LAYOUT_HIERARCHICAL)
            {
                manager.add(new Separator());
                manager.add(new ExpandAllAction());
                manager.add(new CollapseAllAction());
            }

        }
        if (fTestRunSession != null && fTestRunSession.getFailureCount() + fTestRunSession.getErrorCount() > 0)
        {
            if (fLayoutMode != TestRunnerViewPart.LAYOUT_HIERARCHICAL)
                manager.add(new Separator());
            manager.add(new CopyFailureListAction(fTestRunnerPart, fClipboard));
        }
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "-end")); //$NON-NLS-1$
    }

    private class CollapseAllAction
        extends Action
    {
        CollapseAllAction()
        {
            setText(Messages.CollapseAllAction_text);
            setToolTipText(Messages.CollapseAllAction_tooltip);
        }

        @Override
        public void run()
        {
            fTreeViewer.collapseAll();
        }
    }

    private class ExpandAllAction
        extends Action
    {
        ExpandAllAction()
        {
            setText(Messages.ExpandAllAction_text);
            setToolTipText(Messages.ExpandAllAction_tooltip);
        }

        @Override
        public void run()
        {
            fTreeViewer.expandAll();
        }
    }

    private final class FailuresOnlyFilter
        extends ViewerFilter
    {
        public boolean select(TestElement testElement)
        {
            var status = testElement.getStatus();
            if (status.isErrorOrFailure())
                return true;
            else
                return !fTestRunSession.isRunning() && status == Status.RUNNING; // rerunning
        }

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element)
        {
            return select((TestElement)element);
        }
    }

    private final class IgnoredOnlyFilter
        extends ViewerFilter
    {
        public boolean select(TestElement testElement)
        {
            if (hasIgnoredInTestResult(testElement))
                return true;
            else
                return !fTestRunSession.isRunning() && testElement.getStatus() == Status.RUNNING; // rerunning
        }

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element)
        {
            return select((TestElement)element);
        }

        /**
         * Checks whether a test was skipped i.e. it was ignored (<code>@Ignored</code>) or had any
         * assumption failure.
         *
         * @param testElement the test element (a test suite or a single test case)
         *
         * @return <code>true</code> if the test element or any of its children has
         *         {@link Result#IGNORED} test result
         */
        private boolean hasIgnoredInTestResult(TestElement testElement)
        {
            if (testElement instanceof TestSuiteElement)
            {
                ITestElement[] children = ((TestSuiteElement)testElement).getChildren();
                for (ITestElement child : children)
                {
                    boolean hasIgnoredTestResult = hasIgnoredInTestResult((TestElement)child);
                    if (hasIgnoredTestResult)
                    {
                        return true;
                    }
                }
                return false;
            }

            return testElement.getTestResult(false) == Result.IGNORED;
        }
    }

    private static class ReverseList<E>
        extends AbstractList<E>
    {
        private final List<E> fList;

        ReverseList(List<E> list)
        {
            fList = list;
        }

        @Override
        public E get(int index)
        {
            return fList.get(fList.size() - index - 1);
        }

        @Override
        public int size()
        {
            return fList.size();
        }
    }

    private final class TestExecutionTimeComparator
        extends ViewerComparator
    {
        @Override
        public int compare(Viewer viewer, Object o1, Object o2)
        {
            return compareElapsedTime(o1, o2);
        }
    }

    private final class TestNameComparator
        extends ViewerComparator
    {
        @Override
        public int compare(Viewer viewer, Object o1, Object o2)
        {
            return compareName(o1, o2);
        }
    }

    private final class TestOpenListener
        extends SelectionAdapter
    {
        @Override
        public void widgetDefaultSelected(SelectionEvent e)
        {
            handleDefaultSelected();
        }
    }

    private final class TestSelectionListener
        implements ISelectionChangedListener
    {
        @Override
        public void selectionChanged(SelectionChangedEvent event)
        {
            handleSelected();
        }

        private void handleSelected()
        {
            IStructuredSelection selection = (IStructuredSelection)fSelectionProvider.getSelection();
            TestElement testElement = null;
            if (selection.size() == 1)
            {
                testElement = (TestElement)selection.getFirstElement();
            }
            fTestRunnerPart.handleTestSelected(testElement);
        }

    }

}
