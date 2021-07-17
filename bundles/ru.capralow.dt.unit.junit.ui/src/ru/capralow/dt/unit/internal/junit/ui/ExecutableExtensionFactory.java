/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

import org.osgi.framework.Bundle;

import com._1c.g5.wiring.AbstractGuiceAwareExecutableExtensionFactory;
import com.google.inject.Injector;

public class ExecutableExtensionFactory
    extends AbstractGuiceAwareExecutableExtensionFactory
{

    @Override
    protected Bundle getBundle()
    {
        return JUnitUiPlugin.getInstance().getBundle();
    }

    @Override
    protected Injector getInjector()
    {
        return JUnitUiPlugin.getInstance().getInjector();
    }

}
