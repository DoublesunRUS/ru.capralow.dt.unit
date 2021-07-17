/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.coverageview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

/**
 * This handler copies a textual representation of the current selection to the
 * clipboard.
 */
class CopyHandler
    extends AbstractHandler
    implements ISelectionChangedListener
{

    private final ViewSettings settings;
    private final CellTextConverter converter;
    private final Display display;
    private final ISelectionProvider selectionSource;

    CopyHandler(ViewSettings settings, Display display, ISelectionProvider selectionSource)
    {
        this.settings = settings;
        this.converter = new CellTextConverter(settings);
        this.display = display;
        this.selectionSource = selectionSource;
        selectionSource.addSelectionChangedListener(this);
    }

    @Override
    public void dispose()
    {
        selectionSource.removeSelectionChangedListener(this);
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        final StringBuilder sb = new StringBuilder();

        // Header
        final String[] headers = settings.getColumnHeaders();
        sb.append(headers[CoverageView.COLUMN_ELEMENT]).append(SWT.TAB);
        sb.append(headers[CoverageView.COLUMN_RATIO]).append(SWT.TAB);
        sb.append(headers[CoverageView.COLUMN_COVERED]).append(SWT.TAB);
        sb.append(headers[CoverageView.COLUMN_MISSED]).append(SWT.TAB);
        sb.append(headers[CoverageView.COLUMN_TOTAL]).append(Text.DELIMITER);

        // Rows:
        final IStructuredSelection selection = (IStructuredSelection)selectionSource.getSelection();
        for (final Object element : selection.toList())
        {
            sb.append(converter.getElementName(element)).append(SWT.TAB);
            sb.append(converter.getRatio(element)).append(SWT.TAB);
            sb.append(converter.getCovered(element)).append(SWT.TAB);
            sb.append(converter.getMissed(element)).append(SWT.TAB);
            sb.append(converter.getTotal(element)).append(Text.DELIMITER);
        }

        copy(sb.toString());
        return null;
    }

    @Override
    public boolean isEnabled()
    {
        return !selectionSource.getSelection().isEmpty();
    }

    @Override
    public void selectionChanged(SelectionChangedEvent event)
    {
        fireHandlerChanged(new HandlerEvent(this, true, false));
    }

    private void copy(String text)
    {
        final Clipboard cb = new Clipboard(display);
        final TextTransfer transfer = TextTransfer.getInstance();
        cb.setContents(new Object[] { text }, new Transfer[] { transfer });
        cb.dispose();
    }

}
