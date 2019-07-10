package ru.capralow.dt.unit.launcher.plugin.internal.ui.bsl.typesystem;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.typesystem.IDynamicContextDefProvider;
import com._1c.g5.v8.dt.mcore.Type;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com.google.common.collect.ImmutableSet;

public class FrameworkContextDefProvider implements IDynamicContextDefProvider {

	private static final Set<String> SUPPORTED_TYPES;
	static {
		SUPPORTED_TYPES = (Set) ImmutableSet.builder().add("TestFramework").build();
	}

	@Override
	public Type computeDynamicType(EObject semanticObject, Type computeType, Environments envs) {
		return null;
	}

	@Override
	public boolean hasDynamicContext(EObject semanticObject, Type computeType, Environments envs) {
		return isFramework(semanticObject, computeType);
	}

	private boolean isFramework(EObject semanticObject, Type computeType) {
		if (!(semanticObject instanceof OperatorStyleCreator))
			return false;

		if (computeType == null)
			return false;

		String typeName = McoreUtil.getTypeName(computeType);
		return FrameworkContextDefProvider.SUPPORTED_TYPES.contains(typeName);
	}

}
