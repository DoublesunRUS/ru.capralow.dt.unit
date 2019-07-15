package ru.capralow.dt.unit.launcher.plugin.internal.ui;

import java.util.List;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IProcess;

import com._1c.g5.v8.dt.debug.model.base.data.BSLModuleType;
import com._1c.g5.v8.dt.profiling.core.ILineProfilingResult;
import com._1c.g5.v8.dt.profiling.core.IProfileTarget;
import com._1c.g5.v8.dt.profiling.core.IProfilingResult;
import com._1c.g5.v8.dt.profiling.core.IProfilingResultListener;
import com._1c.g5.v8.dt.profiling.core.IProfilingService;
import com._1c.g5.wiring.IManagedService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ru.capralow.dt.unit.launcher.plugin.internal.ui.launchconfigurations.UnitTestLaunch;

@Singleton
public class UnitLauncherManager implements IManagedService, IDebugEventSetListener, IProfilingResultListener {

	@Inject
	private IProfilingService profilingService;

	@Override
	public void activate() {
		DebugPlugin.getDefault().addDebugEventListener(this);
		profilingService.addProfilingResultsListener(this);
	}

	@Override
	public void deactivate() {
		DebugPlugin.getDefault().removeDebugEventListener(this);
		profilingService.removeProfilingResultsListener(this);
	}

	@Override
	public void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent event : events) {
			Object source = event.getSource();
			if (event.getKind() == DebugEvent.CREATE) {
				if (source instanceof IProfileTarget) {
					profilingService.toggleTargetWaitingState(true);
				}

			} else if (event.getKind() == DebugEvent.TERMINATE) {
				if (source instanceof IProcess) {
					UnitTestLaunch.showJUnitResult((IProcess) source);

				} else if (source instanceof IProfileTarget) {
					profilingService.toggleTargetWaitingState(false);
					List<IProfilingResult> profilingResults = profilingService.getResults();

					for (IProfilingResult profilingResult : profilingResults) {
						for (ILineProfilingResult result : profilingResult.getProfilingResults()) {
							if (result.getModuleID().getType() == BSLModuleType.EXT_MD_MODULE)
								continue;

							result.getLine();
						}
					}

				}

			}
		}
	}

	@Override
	public void resultRenamed(IProfilingResult result, String name) {
		return;
	}

	@Override
	public void resultsCleared() {
		return;

	}

	@Override
	public void resultsUpdated(IProfilingResult result) {
		return;
	}

}
