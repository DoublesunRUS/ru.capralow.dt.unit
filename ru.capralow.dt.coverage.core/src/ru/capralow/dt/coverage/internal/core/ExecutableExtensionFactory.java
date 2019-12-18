package ru.capralow.dt.coverage.internal.core;

import org.osgi.framework.Bundle;

import com._1c.g5.wiring.AbstractGuiceAwareExecutableExtensionFactory;
import com.google.inject.Injector;

public class ExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return CoverageCorePlugin.getInstance().getBundle();
	}

	@Override
	protected Injector getInjector() {
		return CoverageCorePlugin.getInjector();
	}

}
