package ru.capralow.dt.unit.launcher.plugin.internal.ui.metatypes;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com._1c.g5.v8.dt.bsl.types.extension.IExternalMetaTypesProvider;
import com._1c.g5.v8.dt.mcore.ContextDef;
import com._1c.g5.v8.dt.mcore.McoreFactory;
import com._1c.g5.v8.dt.mcore.McorePackage;
import com._1c.g5.v8.dt.mcore.Method;
import com._1c.g5.v8.dt.mcore.ParamSet;
import com._1c.g5.v8.dt.mcore.Parameter;
import com._1c.g5.v8.dt.mcore.Property;
import com._1c.g5.v8.dt.mcore.Type;
import com._1c.g5.v8.dt.mcore.TypeContainerRef;
import com._1c.g5.v8.dt.mcore.util.Environment;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com._1c.g5.v8.dt.platform.IEObjectProvider;
import com._1c.g5.v8.dt.platform.version.IRuntimeVersionSupport;
import com._1c.g5.v8.dt.platform.version.Version;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

import ru.capralow.dt.unit.launcher.plugin.core.frameworks.FrameworkUtils;
import ru.capralow.dt.unit.launcher.plugin.core.frameworks.gson.AssertMethod;
import ru.capralow.dt.unit.launcher.plugin.core.frameworks.gson.AssertMethodParameter;
import ru.capralow.dt.unit.launcher.plugin.core.frameworks.gson.Asserts;

public class FrameworkMetaTypesProvider implements IExternalMetaTypesProvider {

	@Inject
	private IRuntimeVersionSupport versionSupport;

	@Override
	public Collection<Type> getExternalTypes(Resource context) {
		Type type = McoreFactory.eINSTANCE.createType();

		type.setEnvironments(new Environments(Environment.SERVER, Environment.CLIENT, Environment.THIN_CLIENT));

		type.setName("TestFramework"); //$NON-NLS-1$
		type.setNameRu("ФреймворкТестирования"); //$NON-NLS-1$

		type.setContextDef(createContextDef(context));

		return Lists.newArrayList(type);
	}

	private ContextDef createContextDef(Resource context) {
		ContextDef contextDef = McoreFactory.eINSTANCE.createContextDef();

		Asserts asserts = FrameworkUtils.getAsserts();
		for (AssertMethod assertMethod : asserts.list) {
			ParamSet paramSet = McoreFactory.eINSTANCE.createParamSet();
			paramSet.setMinParams(assertMethod.params.length);
			paramSet.setMaxParams(assertMethod.params.length);

			for (AssertMethodParameter assertParam : assertMethod.params) {
				paramSet.getParams()
						.add(createParameter(assertParam.nameEn,
								assertParam.nameRu,
								assertParam.getTypes(),
								assertParam.defaultValue,
								context));
			}

			Method newMethod = createMethod(assertMethod.nameEn, assertMethod.nameRu, paramSet);
			contextDef.getMethods().add(newMethod);
		}

		return contextDef;
	}

	private Method createMethod(String name, String nameRu, ParamSet paramSet) {
		Method method = McoreFactory.eINSTANCE.createMethod();

		method.setName(name);
		method.setNameRu(nameRu);
		// Type type = getTypeByName("Number", context); //$NON-NLS-1$
		// if (type != null) {
		// method.setRetVal(true);
		// method.getRetValType().add(type);
		// }
		method.getParamSet().add(paramSet);

		return method;
	}

	private Property createProperty(String name, String nameRu, Resource context) {
		Property property = McoreFactory.eINSTANCE.createProperty();
		property.setName(name);
		property.setNameRu(nameRu);
		property.setWritable(true);
		property.setReadable(true);
		TypeContainerRef typeContainer = McoreFactory.eINSTANCE.createTypeContainerRef();
		Type type = getTypeByName("Boolean", context); //$NON-NLS-1$
		if (type != null) {
			typeContainer.getTypes().add(type);
			property.setTypeContainer(typeContainer);
		}
		return property;
	}

	private Parameter createParameter(String name, String nameRu, String[] typeNames, String defaultValue,
			Resource context) {
		Parameter parameter = McoreFactory.eINSTANCE.createParameter();

		parameter.setName(name);
		parameter.setNameRu(nameRu);

		for (String typeName : typeNames) {
			Type type = getTypeByName(typeName, context);
			if (type != null) {
				parameter.getType().add(type);
			}
		}

		return parameter;
	}

	private Type getTypeByName(String typeName, Resource context) {
		IEObjectProvider provider = IEObjectProvider.Registry.INSTANCE.get(McorePackage.Literals.TYPE_ITEM,
				versionSupport.getRuntimeVersionOrDefault(context, Version.LATEST));
		EObject typeProxy = provider.getProxy(typeName);
		if (typeProxy != null) {
			return (Type) EcoreUtil.resolve(typeProxy, context);
		}
		return null;
	}

}