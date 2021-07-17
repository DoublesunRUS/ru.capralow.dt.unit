/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui.launcher;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com._1c.g5.v8.dt.launching.core.ILaunchConfigurationAttributes;
import com._1c.g5.v8.dt.platform.services.core.runtimes.execution.IRuntimeClientLauncher;
import com._1c.g5.v8.dt.platform.services.core.runtimes.execution.IRuntimeComponentManager;
import com._1c.g5.v8.dt.platform.services.core.runtimes.execution.IRuntimeComponentType;
import com._1c.g5.v8.dt.platform.services.core.runtimes.execution.IUrlOpenClientLauncher;
import com._1c.g5.v8.dt.platform.services.model.InfobaseReference;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public abstract class AbstractUnitTestLaunchTab
    extends AbstractLaunchConfigurationTab
    implements IRuntimeClientChangeListener
{
    @Inject
    protected IRuntimeComponentManager runtimeComponentManager;
    private boolean isAutoSelect = true;
    private String clientTypeId;

    @Override
    public void infobaseChanged(InfobaseReference infobase)
    {
        // Нечего делать
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration)
    {
        this.clientTypeId = this.getClientTypeId(configuration);
        this.isAutoSelect = this.isAutoSelect(configuration);
        this.doInitializeFrom(configuration);
        if (this.isAutoSelect)
        {
            this.doRuntimeClientAutoSelected();
        }
        else
        {
            this.doRuntimeClientChanged(this.clientTypeId);
        }
    }

    @Override
    public void projectChanged(IProject project)
    {
        // Нечего делать
    }

    @Override
    public void runtimeClientAutoSelected()
    {
        this.clientTypeId = null;
        this.isAutoSelect = true;
        this.doRuntimeClientAutoSelected();
    }

    @Override
    public final void runtimeClientChanged(String componentTypeId)
    {
        this.clientTypeId = componentTypeId;
        this.isAutoSelect = false;
        this.doRuntimeClientChanged(componentTypeId);
    }

    protected boolean canClientOpenUri()
    {
        return Collections2
            .transform(runtimeComponentManager.getTypes(IUrlOpenClientLauncher.class), IRuntimeComponentType::getId)
            .contains(getClientTypeId());
    }

    protected boolean canRunRuntimeClient()
    {
        return Collections2
            .transform(runtimeComponentManager.getTypes(IRuntimeClientLauncher.class), IRuntimeComponentType::getId)
            .contains(getClientTypeId());
    }

    protected abstract void doInitializeFrom(ILaunchConfiguration var1);

    protected void doRuntimeClientAutoSelected()
    {
        // Нечего делать
    }

    protected void doRuntimeClientChanged(String clientTypeId)
    {
        // Нечего делать
    }

    protected String getClientTypeId()
    {
        return this.clientTypeId;
    }

    protected String getClientTypeId(ILaunchConfiguration configuration)
    {
        try
        {
            return configuration.getAttribute(ILaunchConfigurationAttributes.CLIENT_TYPE, (String)null);
        }
        catch (CoreException e)
        {
            throw new IllegalStateException(e);
        }
    }

    protected boolean isAutoSelect()
    {
        return this.isAutoSelect;
    }

    protected boolean isAutoSelect(ILaunchConfiguration configuration)
    {
        try
        {
            return configuration.getAttribute(ILaunchConfigurationAttributes.CLIENT_AUTO_SELECT, false);
        }
        catch (CoreException e)
        {
            throw new IllegalStateException(e);
        }
    }

    protected void setVisible(boolean visible, Control... controls)
    {
        Preconditions.checkArgument(controls.length > 0, "Controls cannot be empty"); //$NON-NLS-1$
        Preconditions.checkArgument((!Arrays.asList(controls).contains(null)), "Controls cannot contain nulls"); //$NON-NLS-1$
        boolean visibilityChanged = Iterables.any(Arrays.asList(controls), input -> {
            GridData data = (GridData)input.getLayoutData();
            return !(data.exclude != visible && input.isVisible() == visible);
        });
        if (visibilityChanged)
        {
            Control[] arrcontrol = controls;
            int n = arrcontrol.length;
            var n2 = 0;
            while (n2 < n)
            {
                var control = arrcontrol[n2];
                control.setVisible(visible);
                GridData data = (GridData)control.getLayoutData();
                data.exclude = !visible;
                ++n2;
            }
            ((Composite)this.getControl()).layout(true);
        }
    }
}
