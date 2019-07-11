package ru.capralow.dt.unit.launcher.plugin.internal.ui.typesystem;

import java.util.List;

import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.typesystem.IInvocationTypesComputerExtension;
import com._1c.g5.v8.dt.mcore.TypeDescription;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.md.resource.MdTypeUtil;
import com.google.common.collect.Lists;

public class FrameworkInvocationTypesComputer implements IInvocationTypesComputerExtension {

	@Override
	public List<TypeItem> getTypes(Invocation inv) {
		return computeTypes(inv);
	}

	private List<TypeItem> computeTypes(Invocation inv) {
		TypeDescription typeDescription = MdTypeUtil.newTypeDescription();
		TypeItem newType = MdTypeUtil.getSingleType(typeDescription);
		return Lists.newArrayList(new TypeItem[] { newType });
	}
}
