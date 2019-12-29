package ru.capralow.dt.coverage.internal.core;

import org.eclipse.core.runtime.Plugin;

import com._1c.g5.v8.dt.bm.index.emf.IBmEmfIndexManager;
import com._1c.g5.v8.dt.bm.xtext.BmAwareResourceSetProvider;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.debug.core.model.IBslModuleLocator;
import com._1c.g5.v8.dt.profiling.core.IProfilingService;
import com._1c.g5.wiring.AbstractServiceAwareModule;

public class ExternalDependenciesModule extends AbstractServiceAwareModule {

	public ExternalDependenciesModule(Plugin bundle) {
		super(bundle);
	}

	@Override
	protected void doConfigure() {
		bind(BmAwareResourceSetProvider.class).toService();
		bind(IBmEmfIndexManager.class).toService();
		bind(IBslModuleLocator.class).toService();
		bind(IProfilingService.class).toService();
		bind(IResourceLookup.class).toService();
		bind(IV8ProjectManager.class).toService();
	}

}
