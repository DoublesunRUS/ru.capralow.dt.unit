/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui.launcher;

import org.eclipse.core.resources.IProject;

import com._1c.g5.v8.dt.platform.services.model.InfobaseReference;

/**
 * @author Aleksandr Kapralov
 *
 */
public interface IRuntimeClientChangeListener
{
    /**
     * @param var1
     */
    void infobaseChanged(InfobaseReference var1);

    /**
     * @param var1
     */
    void projectChanged(IProject var1);

    /**
     *
     */
    void runtimeClientAutoSelected();

    /**
     * @param var1
     */
    void runtimeClientChanged(String var1);
}
