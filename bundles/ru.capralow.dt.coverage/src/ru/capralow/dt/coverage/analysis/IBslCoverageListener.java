/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.analysis;

/**
 * Callback interface implemented by clients that want to be informed, when the
 * current Bsl model coverage has changes.
 */
public interface IBslCoverageListener
{

    /**
     * Called when the current coverage data has changed.
     */
    void coverageChanged();

}
