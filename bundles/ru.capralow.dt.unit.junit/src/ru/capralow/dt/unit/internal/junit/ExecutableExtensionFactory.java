/**
 * Copyright (c) 2021, Alexander Kapralov
 */
package ru.capralow.dt.unit.internal.junit;

import org.osgi.framework.Bundle;

import com._1c.g5.wiring.AbstractGuiceAwareExecutableExtensionFactory;
import com.google.inject.Injector;

public class ExecutableExtensionFactory
    extends AbstractGuiceAwareExecutableExtensionFactory
{

    @Override
    protected Bundle getBundle()
    {
        return JUnitPlugin.getInstance().getBundle();
    }

    @Override
    protected Injector getInjector()
    {
        return JUnitPlugin.getInstance().getInjector();
    }

}
