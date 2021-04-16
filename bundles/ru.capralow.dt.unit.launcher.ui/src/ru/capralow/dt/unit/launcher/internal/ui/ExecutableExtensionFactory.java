/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.unit.launcher.internal.ui;

import org.osgi.framework.Bundle;

import com._1c.g5.wiring.AbstractGuiceAwareExecutableExtensionFactory;
import com.google.inject.Injector;

public class ExecutableExtensionFactory
    extends AbstractGuiceAwareExecutableExtensionFactory
{

    @Override
    protected Bundle getBundle()
    {
        return UnitLauncherUiPlugin.getInstance().getBundle();
    }

    @Override
    protected Injector getInjector()
    {
        return UnitLauncherUiPlugin.getInstance().getInjector();
    }

}
