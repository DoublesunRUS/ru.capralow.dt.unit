/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

import org.eclipse.swt.graphics.Image;

/**
 * Manages a set of images that can show progress in the image itself.
 */
public class ProgressImages
{
    private static final int PROGRESS_STEPS = 9;

    private static final String BASE = "prgss/"; //$NON-NLS-1$
    private static final String FAILURE = "ff"; //$NON-NLS-1$
    private static final String OK = "ss"; //$NON-NLS-1$

    private Image[] fOkImages = new Image[PROGRESS_STEPS];
    private Image[] fFailureImages = new Image[PROGRESS_STEPS];

    /**
     *
     */
    public void dispose()
    {
        if (!isLoaded())
        {
            return;
        }

        for (int i = 0; i < PROGRESS_STEPS; i++)
        {
            fOkImages[i].dispose();
            fOkImages[i] = null;
            fFailureImages[i].dispose();
            fFailureImages[i] = null;
        }
    }

    /**
     * @param current
     * @param total
     * @param errors
     * @param failures
     * @return Image
     */
    public Image getImage(int current, int total, int errors, int failures)
    {
        if (!isLoaded())
        {
            load();
        }

        if (total == 0)
        {
            return fOkImages[0];
        }
        int index = ((current * PROGRESS_STEPS) / total) - 1;
        index = Math.min(Math.max(0, index), PROGRESS_STEPS - 1);

        if (errors + failures == 0)
        {
            return fOkImages[index];
        }
        return fFailureImages[index];
    }

    private Image createImage(String name)
    {
        return JUnitUiPlugin.getImageDescriptor(name).createImage();
    }

    private boolean isLoaded()
    {
        return fOkImages[0] != null;
    }

    private void load()
    {
        if (isLoaded())
        {
            return;
        }

        for (int i = 0; i < PROGRESS_STEPS; i++)
        {
            String okname = BASE + OK + Integer.toString(i + 1) + ".png"; //$NON-NLS-1$
            fOkImages[i] = createImage(okname);
            String failurename = BASE + FAILURE + Integer.toString(i + 1) + ".png"; //$NON-NLS-1$
            fFailureImages[i] = createImage(failurename);
        }
    }
}
