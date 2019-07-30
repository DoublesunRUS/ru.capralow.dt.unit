package ru.capralow.dt.eclemma.internal.core;

import org.osgi.framework.Bundle;

import com._1c.g5.wiring.AbstractGuiceAwareExecutableExtensionFactory;
import com.google.inject.Injector;

public class ExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return EclEmmaCoreFragment.getDefault().getBundle();
	}

	@Override
	protected Injector getInjector() {
		return EclEmmaCoreFragment.getInjector();
	}

}
