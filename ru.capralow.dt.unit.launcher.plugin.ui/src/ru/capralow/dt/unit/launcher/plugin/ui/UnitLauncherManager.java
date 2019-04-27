package ru.capralow.dt.unit.launcher.plugin.ui;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.junit.JUnitCore;

import com._1c.g5.wiring.IManagedService;

public class UnitLauncherManager implements IManagedService, IDebugEventSetListener {

	public void activate() {
		DebugPlugin.getDefault().addDebugEventListener(this);
	}

	public void deactivate() {
		DebugPlugin.getDefault().removeDebugEventListener(this);
	}

	@Override
	public void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent event : events) {
			Object source = event.getSource();
			if (source instanceof IProcess && event.getKind() == DebugEvent.TERMINATE) {
				File file = new File("/Users/kapral/Downloads/junit (6).xml");

				try {
					JUnitCore.importTestRunSession(file);
				} catch (CoreException e) {
					UnitLauncherPlugin.createErrorStatus("Не удалось прочитать файл с результатом модульных тестов.",
							e);
				}
			}
		}
	}

}
