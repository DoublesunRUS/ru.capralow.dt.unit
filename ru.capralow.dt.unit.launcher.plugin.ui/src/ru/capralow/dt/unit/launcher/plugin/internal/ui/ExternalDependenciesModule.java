package ru.capralow.dt.unit.launcher.plugin.internal.ui;

import org.eclipse.core.runtime.Plugin;

import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.profiling.core.IProfilingService;
import com._1c.g5.wiring.AbstractServiceAwareModule;

public class ExternalDependenciesModule extends AbstractServiceAwareModule {

	public ExternalDependenciesModule(Plugin bundle) {
		super(bundle);
	}

	@Override
	protected void doConfigure() {
		bind(IProfilingService.class).toService();
		bind(IV8ProjectManager.class).toService();
	}

}
