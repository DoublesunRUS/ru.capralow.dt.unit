/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * A progress bar with a red/green indication for success or failure.
 */
public class JUnitProgressBar
    extends Canvas
{
    private static final int DEFAULT_WIDTH = 160;
    private static final int DEFAULT_HEIGHT = 18;

    private int fCurrentTickCount = 0;
    private int fMaxTickCount = 0;
    private int fColorBarWidth = 0;
    private Color fOkColor;
    private Color fFailureColor;
    private Color fStoppedColor;
    private boolean fError;
    private boolean fStopped = false;

    /**
     * @param parent
     */
    public JUnitProgressBar(Composite parent)
    {
        super(parent, SWT.NONE);

        addControlListener(new ControlAdapter()
        {
            @Override
            public void controlResized(ControlEvent e)
            {
                fColorBarWidth = scale(fCurrentTickCount);
                redraw();
            }
        });
        addPaintListener(this::paint);
        Display display = parent.getDisplay();
        fFailureColor = new Color(display, 159, 63, 63);
        fOkColor = new Color(display, 95, 191, 95);
        fStoppedColor = new Color(display, 120, 120, 120);
    }

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed)
    {
        checkWidget();
        Point size = new Point(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        if (wHint != SWT.DEFAULT)
        {
            size.x = wHint;
        }
        if (hHint != SWT.DEFAULT)
        {
            size.y = hHint;
        }
        return size;
    }

    /**
     * @param hasErrors
     */
    public void refresh(boolean hasErrors)
    {
        fError = hasErrors;
        redraw();
    }

    /**
     *
     */
    public void reset()
    {
        fError = false;
        fStopped = false;
        fCurrentTickCount = 0;
        fMaxTickCount = 0;
        fColorBarWidth = 0;
        redraw();
    }

    /**
     * @param hasErrors
     * @param stopped
     * @param ticksDone
     * @param maximum
     */
    public void reset(boolean hasErrors, boolean stopped, int ticksDone, int maximum)
    {
        boolean noChange =
            fError == hasErrors && fStopped == stopped && fCurrentTickCount == ticksDone && fMaxTickCount == maximum;
        fError = hasErrors;
        fStopped = stopped;
        fCurrentTickCount = ticksDone;
        fMaxTickCount = maximum;
        fColorBarWidth = scale(ticksDone);
        if (!noChange)
        {
            redraw();
        }
    }

    /**
     * @param max
     */
    public void setMaximum(int max)
    {
        fMaxTickCount = max;
    }

    /**
     * @param failures
     */
    public void step(int failures)
    {
        fCurrentTickCount++;
        int x = fColorBarWidth;

        fColorBarWidth = scale(fCurrentTickCount);

        if (!fError && failures > 0)
        {
            fError = true;
            x = 1;
        }
        if (fCurrentTickCount == fMaxTickCount)
        {
            fColorBarWidth = getClientArea().width - 1;
        }
        paintStep(x, fColorBarWidth);
    }

    /**
     *
     */
    public void stopped()
    {
        fStopped = true;
        redraw();
    }

    private void drawBevelRect(GC gc, int x, int y, int w, int h, Color topleft, Color bottomright)
    {
        gc.setForeground(topleft);
        gc.drawLine(x, y, x + w - 1, y);
        gc.drawLine(x, y, x, y + h - 1);

        gc.setForeground(bottomright);
        gc.drawLine(x + w, y, x + w, y + h);
        gc.drawLine(x, y + h, x + w, y + h);
    }

    private void paint(PaintEvent event)
    {
        GC gc = event.gc;
        Display disp = getDisplay();

        Rectangle rect = getClientArea();
        gc.fillRectangle(rect);
        drawBevelRect(gc, rect.x, rect.y, rect.width - 1, rect.height - 1,
            disp.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW),
            disp.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));

        setStatusColor(gc);
        fColorBarWidth = Math.min(rect.width - 2, fColorBarWidth);
        gc.fillRectangle(1, 1, fColorBarWidth, rect.height - 2);
    }

    private void paintStep(int startX, int endX)
    {
        GC gc = new GC(this);
        setStatusColor(gc);
        Rectangle rect = getClientArea();
        int startXmax = Math.max(1, startX);
        gc.fillRectangle(startXmax, 1, endX - startXmax, rect.height - 2);
        gc.dispose();
    }

    private int scale(int value)
    {
        if (fMaxTickCount > 0)
        {
            Rectangle r = getClientArea();
            if (r.width != 0)
            {
                return Math.max(0, value * (r.width - 2) / fMaxTickCount);
            }
        }
        return value;
    }

    private void setStatusColor(GC gc)
    {
        if (fStopped)
        {
            gc.setBackground(fStoppedColor);
        }
        else if (fError)
        {
            gc.setBackground(fFailureColor);
        }
        else
        {
            gc.setBackground(fOkColor);
        }
    }

}
