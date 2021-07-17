package ru.capralow.dt.unit.internal.junit.ui.launcher;

import org.eclipse.core.resources.IProject;

import com._1c.g5.v8.dt.platform.services.model.InfobaseReference;

public interface IRuntimeClientChangeListener
{
    public void infobaseChanged(InfobaseReference var1);

    public void projectChanged(IProject var1);

    public void runtimeClientAutoSelected();

    public void runtimeClientChanged(String var1);
}
