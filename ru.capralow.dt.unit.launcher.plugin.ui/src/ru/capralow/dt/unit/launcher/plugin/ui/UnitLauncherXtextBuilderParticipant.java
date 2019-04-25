package ru.capralow.dt.unit.launcher.plugin.ui;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription.Delta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._1c.g5.v8.dt.bsl.model.Module;

public class UnitLauncherXtextBuilderParticipant implements org.eclipse.xtext.builder.IXtextBuilderParticipant {
	private static final Logger LOGGER = LoggerFactory.getLogger(UnitLauncherXtextBuilderParticipant.class);

	@Override
	public void build(IBuildContext context, IProgressMonitor monitor) throws CoreException {
		List<Delta> deltas = context.getDeltas();
		for (Delta delta : deltas) {
			if (!delta.haveEObjectDescriptionsChanged())
				continue;

			EObject object = null;

			Iterator<IEObjectDescription> objectItr = delta.getNew().getExportedObjects().iterator();
			if (objectItr.hasNext())
				object = objectItr.next().getEObjectOrProxy();

			if (object == null) {
				String msg = String.format("Не найден объект конфигурации: \"%1$s\"", "");
				LOGGER.error(msg);
			}

			if (!(object instanceof Module))
				continue;

			Module module = (Module) object;

		}

	}

}
