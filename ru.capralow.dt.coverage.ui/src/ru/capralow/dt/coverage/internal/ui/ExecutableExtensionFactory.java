package ru.capralow.dt.coverage.internal.ui;

import org.osgi.framework.Bundle;

import com._1c.g5.wiring.AbstractGuiceAwareExecutableExtensionFactory;
import com.google.inject.Injector;

public class ExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return CoverageUIPlugin.getInstance().getBundle();
	}

	@Override
	protected Injector getInjector() {
		return CoverageUIPlugin.getInstance().getInjector();
	}

}
