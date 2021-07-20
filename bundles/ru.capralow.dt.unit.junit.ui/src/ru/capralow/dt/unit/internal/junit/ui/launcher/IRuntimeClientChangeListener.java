/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui.launcher;

import org.eclipse.core.resources.IProject;

import com._1c.g5.v8.dt.platform.services.model.InfobaseReference;

public interface IRuntimeClientChangeListener
{
    void infobaseChanged(InfobaseReference var1);

    void projectChanged(IProject var1);

    void runtimeClientAutoSelected();

    void runtimeClientChanged(String var1);
}
