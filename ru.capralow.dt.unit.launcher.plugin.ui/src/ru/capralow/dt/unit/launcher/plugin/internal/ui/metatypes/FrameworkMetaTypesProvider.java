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

		Method newMethod = createMethod("TrueCheck", "ПроверитьИстину", context); //$NON-NLS-1$ //$NON-NLS-2$
		contextDef.getMethods().add(newMethod);

		newMethod = createMethod("FalseCheck", "ПроверитьЛожь", context); //$NON-NLS-1$ //$NON-NLS-2$
		contextDef.getMethods().add(newMethod);

		// contextDef.getProperties().add(createProperty("InTypeProperty",
		// "СвойстваВнутриТипа", context)); //$NON-NLS-1$ //$NON-NLS-2$
		return contextDef;
	}

	private Method createMethod(String name, String nameRu, Resource context) {
		Method method = McoreFactory.eINSTANCE.createMethod();
		method.setName(name);
		method.setNameRu(nameRu);
		// Type type = getTypeByName("Number", context); //$NON-NLS-1$
		// if (type != null) {
		// method.setRetVal(true);
		// method.getRetValType().add(type);
		// }
		ParamSet set = McoreFactory.eINSTANCE.createParamSet();
		set.setMinParams(2);
		set.setMaxParams(2);
		method.getParamSet().add(set);
		set.getParams().add(createParameter("_True", "_Истина", "Boolean", context)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		set.getParams().add(createParameter("DopMessage", "ДопСообщениеОшибки", "String", context)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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

	private Parameter createParameter(String name, String nameRu, String typeName, Resource context) {
		Parameter parameter = McoreFactory.eINSTANCE.createParameter();
		parameter.setName(name);
		parameter.setNameRu(nameRu);
		Type type = getTypeByName(typeName, context);
		if (type != null) {
			parameter.getType().add(type);
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