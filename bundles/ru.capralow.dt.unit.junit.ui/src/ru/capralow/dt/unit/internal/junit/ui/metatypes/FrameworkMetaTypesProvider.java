/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui.metatypes;

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

import ru.capralow.dt.unit.junit.frameworks.FrameworkUtils;
import ru.capralow.dt.unit.junit.frameworks.gson.FrameworkMetaTypes;
import ru.capralow.dt.unit.junit.frameworks.gson.FrameworkMethod;
import ru.capralow.dt.unit.junit.frameworks.gson.FrameworkMethodParameter;
import ru.capralow.dt.unit.junit.frameworks.gson.FrameworkProperty;

public class FrameworkMetaTypesProvider
    implements IExternalMetaTypesProvider
{

    @Inject
    private IRuntimeVersionSupport versionSupport;

    @Override
    public Collection<Type> getExternalTypes(Resource context)
    {
        Type type = McoreFactory.eINSTANCE.createType();

        type.setEnvironments(new Environments(Environment.SERVER, Environment.CLIENT, Environment.THIN_CLIENT));

        type.setName("TestFramework"); //$NON-NLS-1$
        type.setNameRu("ФреймворкТестирования"); //$NON-NLS-1$

        type.setContextDef(createContextDef(context));

        return Lists.newArrayList(type);
    }

    private ContextDef createContextDef(Resource context)
    {
        ContextDef contextDef = McoreFactory.eINSTANCE.createContextDef();

        FrameworkMetaTypes metaTypes = FrameworkUtils.getFrameworkMetaTypes();

        for (FrameworkMethod metaMethod : metaTypes.methods)
        {
            ParamSet paramSet = McoreFactory.eINSTANCE.createParamSet();
            paramSet.setMinParams(metaMethod.getMinParams());
            paramSet.setMaxParams(metaMethod.params.length);

            for (FrameworkMethodParameter metaMethodParam : metaMethod.params)
            {
                paramSet.getParams().add(createParameter(metaMethodParam, context));
            }

            Method newMethod = createMethod(metaMethod, paramSet, context);
            contextDef.getMethods().add(newMethod);
        }

        for (FrameworkProperty metaProperty : metaTypes.properties)
        {
            createProperty(metaProperty, context);
        }

        return contextDef;
    }

    private Method createMethod(FrameworkMethod metaMethod, ParamSet paramSet, Resource context)
    {
        Method method = McoreFactory.eINSTANCE.createMethod();

        method.setName(metaMethod.nameEn);
        method.setNameRu(metaMethod.nameRu);

        for (String typeName : metaMethod.getReturnedValues())
        {
            Type type = getTypeByName(typeName, context);
            if (type != null)
            {
                method.setRetVal(true);
                method.getRetValType().add(type);
            }
        }

        method.getParamSet().add(paramSet);

        return method;
    }

    private Parameter createParameter(FrameworkMethodParameter metaMethodParam, Resource context)
    {
        Parameter parameter = McoreFactory.eINSTANCE.createParameter();

        parameter.setName(metaMethodParam.nameEn);
        parameter.setNameRu(metaMethodParam.nameRu);

        for (String typeName : metaMethodParam.getTypes())
        {
            Type type = getTypeByName(typeName, context);
            if (type != null)
            {
                parameter.getType().add(type);
            }
        }

        parameter.setOut(metaMethodParam.isOut);
        parameter.setDefaultValue(metaMethodParam.isDefaultValue);

        return parameter;
    }

    private Property createProperty(FrameworkProperty metaProperty, Resource context)
    {
        Property property = McoreFactory.eINSTANCE.createProperty();

        property.setName(metaProperty.nameEn);
        property.setNameRu(metaProperty.nameRu);

        property.setReadable(true);
        property.setWritable(false);

        TypeContainerRef typeContainer = McoreFactory.eINSTANCE.createTypeContainerRef();

        Type type = getTypeByName(metaProperty.type, context);
        if (type != null)
        {
            typeContainer.getTypes().add(type);
            property.setTypeContainer(typeContainer);
        }

        return property;
    }

    private Type getTypeByName(String typeName, Resource context)
    {
        IEObjectProvider provider = IEObjectProvider.Registry.INSTANCE.get(McorePackage.Literals.TYPE_ITEM,
            versionSupport.getRuntimeVersionOrDefault(context, Version.LATEST));

        EObject typeProxy = provider.getProxy(typeName);
        if (typeProxy != null)
        {
            return (Type)EcoreUtil.resolve(typeProxy, context);
        }

        return null;
    }

}
