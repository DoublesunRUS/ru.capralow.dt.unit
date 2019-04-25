package ru.capralow.dt.unit.launcher.plugin.ui;

import org.eclipse.core.runtime.Plugin;

import com._1c.g5.v8.dt.bm.index.emf.IBmEmfIndexManager;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IDerivedDataManagerProvider;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.wiring.AbstractServiceAwareModule;

public class UnitLauncherExternalDependenciesModule extends AbstractServiceAwareModule {

	public UnitLauncherExternalDependenciesModule(Plugin bundle) {
		super(bundle);
	}

	@Override
	protected void doConfigure() {
		bind(IV8ProjectManager.class).toService();
		bind(IBmEmfIndexManager.class).toService();
		bind(IResourceLookup.class).toService();
		bind(IDerivedDataManagerProvider.class).toService();
		bind(IBmModelManager.class).toService();
	}

}
