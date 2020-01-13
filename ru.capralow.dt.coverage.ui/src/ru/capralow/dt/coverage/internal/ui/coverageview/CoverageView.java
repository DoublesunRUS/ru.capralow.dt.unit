/*******************************************************************************
 * Copyright (c) 2006, 2019 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    Brock Janiczak - link with selection option (SF #1774547)
 *
 * Adapted by Alexander Kapralov
 *
 ******************************************************************************/
package ru.capralow.dt.coverage.internal.ui.coverageview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.IHandler;
import org.eclipse.jdt.ui.actions.IJavaEditorActionDefinitionIds;
import org.eclipse.jdt.ui.actions.JdtActionConstants;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.handlers.CollapseAllHandler;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode;

import ru.capralow.dt.coverage.core.CoverageTools;
import ru.capralow.dt.coverage.core.ICoverageSession;
import ru.capralow.dt.coverage.core.ISessionListener;
import ru.capralow.dt.coverage.core.analysis.IBslCoverageListener;
import ru.capralow.dt.coverage.internal.ui.ContextHelp;
import ru.capralow.dt.coverage.internal.ui.RedGreenBar;
import ru.capralow.dt.coverage.internal.ui.UIMessages;
import ru.capralow.dt.coverage.internal.ui.actions.OpenAction;

/**
 * Implementation of the coverage view.
 */
public class CoverageView extends ViewPart implements IShowInTarget {

	public static final String ID = "ru.capralow.dt.coverage.ui.CoverageView"; //$NON-NLS-1$

	/**
	 * Placeholder element for displaying "Loading..." in the coverage view.
	 */
	public static final Object LOADING_ELEMENT = new Object();

	protected static final int COLUMN_ELEMENT = 0;
	protected static final int COLUMN_RATIO = 1;
	protected static final int COLUMN_COVERED = 2;
	protected static final int COLUMN_MISSED = 3;
	protected static final int COLUMN_TOTAL = 4;

	private final ViewSettings settings = new ViewSettings();
	private final CellTextConverter cellTextConverter = new CellTextConverter(settings);
	private final MaxTotalCache maxTotalCache = new MaxTotalCache(settings);

	private TreeViewer viewer;

	// Actions
	private OpenAction openAction;

	private final List<IHandler> handlers = new ArrayList<>();

	private SelectionTracker selectionTracker;

	private CoverageViewSorter sorter = new CoverageViewSorter(settings, this);

	private final ISessionListener descriptionUpdater = new ISessionListener() {
		@Override
		public void sessionActivated(ICoverageSession session) {
			getViewSite().getShell().getDisplay().asyncExec(() -> {
				final ICoverageSession active = CoverageTools.getSessionManager().getActiveSession();
				setContentDescription(active == null ? "" : active.getDescription()); //$NON-NLS-1$
			});
		}

		@Override
		public void sessionAdded(ICoverageSession addedSession) {
			// Nothing to do
		}

		@Override
		public void sessionRemoved(ICoverageSession removedSession) {
			// Nothing to do
		}
	};

	private final IBslCoverageListener coverageListener = () -> getSite().getShell().getDisplay().asyncExec(() -> {
		maxTotalCache.reset();
		viewer.setInput(CoverageTools.getBslModelCoverage());
	});

	@Override
	public void createPartControl(Composite parent) {
		ContextHelp.setHelp(parent, ContextHelp.COVERAGE_VIEW);
		Tree tree = new Tree(parent, SWT.MULTI);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		viewer = new TreeViewer(tree);
		final TreeViewerColumn column0 = new TreeViewerColumn(viewer, SWT.LEFT);
		column0.setLabelProvider(new CellLabelProvider() {

			private final ILabelProvider delegate = new WorkbenchLabelProvider();

			@Override
			public void update(ViewerCell cell) {
				if (cell.getElement() == LOADING_ELEMENT) {
					cell.setText(UIMessages.CoverageView_loadingMessage);
					cell.setImage(null);
				} else {
					cell.setText(cellTextConverter.getElementName(cell.getElement()));
					cell.setImage(delegate.getImage(cell.getElement()));
				}
			}
		});
		sorter.addColumn(column0, COLUMN_ELEMENT);

		final TreeViewerColumn column1 = new TreeViewerColumn(viewer, SWT.RIGHT);
		column1.setLabelProvider(new OwnerDrawLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				if (cell.getElement() == LOADING_ELEMENT) {
					cell.setText(""); //$NON-NLS-1$
				} else {
					cell.setText(cellTextConverter.getRatio(cell.getElement()));
				}
			}

			@Override
			protected void erase(Event event, Object element) {
				// Нечего делать
			}

			@Override
			protected void measure(Event event, Object element) {
				// Нечего делать
			}

			@Override
			protected void paint(Event event, Object element) {
				final ICoverageNode coverage = CoverageTools.getCoverageInfo(element);
				if (coverage != null) {
					final ICounter counter = coverage.getCounter(settings.getCounters());
					RedGreenBar
							.draw(event, column1.getColumn().getWidth(), counter, maxTotalCache.getMaxTotal(element));
				}
			}
		});
		sorter.addColumn(column1, COLUMN_RATIO);

		final TreeViewerColumn column2 = new TreeViewerColumn(viewer, SWT.RIGHT);
		column2.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				if (cell.getElement() == LOADING_ELEMENT) {
					cell.setText(""); //$NON-NLS-1$
				} else {
					cell.setText(cellTextConverter.getCovered(cell.getElement()));
				}
			}
		});
		sorter.addColumn(column2, COLUMN_COVERED);

		final TreeViewerColumn column3 = new TreeViewerColumn(viewer, SWT.RIGHT);
		column3.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				if (cell.getElement() == LOADING_ELEMENT) {
					cell.setText(""); //$NON-NLS-1$
				} else {
					cell.setText(cellTextConverter.getMissed(cell.getElement()));
				}
			}
		});
		sorter.addColumn(column3, COLUMN_MISSED);

		final TreeViewerColumn column4 = new TreeViewerColumn(viewer, SWT.RIGHT);
		column4.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				if (cell.getElement() == LOADING_ELEMENT) {
					cell.setText(""); //$NON-NLS-1$
				} else {
					cell.setText(cellTextConverter.getTotal(cell.getElement()));
				}
			}
		});
		sorter.addColumn(column4, COLUMN_TOTAL);

		viewer.addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer2, Object parentElement, Object element) {
				if (element == LOADING_ELEMENT)
					return true;

				final ICoverageNode c = CoverageTools.getCoverageInfo(element);
				if (c == null) {
					return false;
				}
				final ICounter instructions = c.getInstructionCounter();
				if (instructions.getTotalCount() == 0) {
					return false;
				}
				if (settings.getHideUnusedElements() && instructions.getCoveredCount() == 0)
					return false;

				return true;
			}
		});
		settings.updateColumnHeaders(viewer);
		settings.restoreColumnWidth(viewer);
		viewer.setComparator(sorter);
		viewer.setContentProvider(new CoveredElementsContentProvider(settings));
		viewer.setInput(CoverageTools.getBslModelCoverage());
		getSite().setSelectionProvider(viewer);

		selectionTracker = new SelectionTracker(this, viewer);

		createHandlers();
		createActions();

		viewer.addOpenListener(event -> openAction.run((IStructuredSelection) event.getSelection()));

		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		tree.setMenu(menuMgr.createContextMenu(tree));
		getSite().registerContextMenu(menuMgr, viewer);

		CoverageTools.getSessionManager().addSessionListener(descriptionUpdater);
		CoverageTools.addBslCoverageListener(coverageListener);
	}

	@Override
	public void dispose() {
		for (IHandler h : handlers) {
			h.dispose();
		}
		handlers.clear();
		CoverageTools.removeBslCoverageListener(coverageListener);
		CoverageTools.getSessionManager().removeSessionListener(descriptionUpdater);
		selectionTracker.dispose();
		super.dispose();
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		settings.init(memento);
	}

	@Override
	public void saveState(IMemento memento) {
		settings.storeColumnWidth(viewer);
		settings.save(memento);
		super.saveState(memento);
	}

	@Override
	public void setFocus() {
		viewer.getTree().setFocus();
	}

	@Override
	public boolean show(ShowInContext context) {
		final ISelection selection = context.getSelection();
		if (selection instanceof IStructuredSelection) {
			viewer.setSelection(selection);
			return true;
		}
		return false;
	}

	private void activateHandler(String id, IHandler handler) {
		final IHandlerService hs = getSite().getService(IHandlerService.class);
		hs.activateHandler(id, handler);
		handlers.add(handler);
	}

	private void createActions() {
		// For the following commands we use actions, as they are already available

		final IActionBars ab = getViewSite().getActionBars();

		openAction = new OpenAction(getSite());
		openAction.setActionDefinitionId(IJavaEditorActionDefinitionIds.OPEN_EDITOR);
		ab.setGlobalActionHandler(JdtActionConstants.OPEN, openAction);
		openAction.setEnabled(false);
		viewer.addSelectionChangedListener(openAction);

		PropertyDialogAction propertiesAction = new PropertyDialogAction(getSite(), viewer);
		propertiesAction.setActionDefinitionId(IWorkbenchCommandConstants.FILE_PROPERTIES);
		ab.setGlobalActionHandler(ActionFactory.PROPERTIES.getId(), propertiesAction);
		propertiesAction.setEnabled(false);
		viewer.addSelectionChangedListener(propertiesAction);
	}

	/**
	 * Create local handlers.
	 */
	private void createHandlers() {
		activateHandler(SelectRootElementsHandler.ID, new SelectRootElementsHandler(settings, this));
		activateHandler(SelectCountersHandler.ID, new SelectCountersHandler(settings, this));
		activateHandler(HideUnusedElementsHandler.ID, new HideUnusedElementsHandler(settings, this));
		activateHandler(IWorkbenchCommandConstants.EDIT_COPY,
				new CopyHandler(settings, getSite().getShell().getDisplay(), viewer));
		activateHandler(IWorkbenchCommandConstants.FILE_REFRESH,
				new RefreshSessionHandler(CoverageTools.getSessionManager()));
		activateHandler(IWorkbenchCommandConstants.NAVIGATE_COLLAPSE_ALL, new CollapseAllHandler(viewer));
		activateHandler(LinkWithSelectionHandler.ID, new LinkWithSelectionHandler(settings, selectionTracker));
	}

	protected void refreshViewer() {
		maxTotalCache.reset();
		settings.updateColumnHeaders(viewer);
		viewer.refresh();
	}

}
