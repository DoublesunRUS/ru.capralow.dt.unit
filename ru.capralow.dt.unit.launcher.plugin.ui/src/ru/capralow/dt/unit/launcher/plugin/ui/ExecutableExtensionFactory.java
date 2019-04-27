package ru.capralow.dt.unit.launcher.plugin.ui;

import org.osgi.framework.Bundle;

import com._1c.g5.wiring.AbstractGuiceAwareExecutableExtensionFactory;
import com.google.inject.Injector;

public class ExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return UnitLauncherPlugin.getDefault().getBundle();
	}

	@Override
	protected Injector getInjector() {
		return UnitLauncherPlugin.getDefault().getInjector();
	}

}
