/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.junit;

import java.util.Iterator;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;

import com._1c.g5.v8.dt.bm.index.emf.IBmEmfIndexProvider;
import com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;

/**
 * @author Aleksandr Kapralov
 *
 */
public final class MdUtils
{

    /**
     * @param objectFullName
     * @param bmEmfIndexProvider
     * @return MdObject
     */
    public static MdObject getConfigurationObject(String objectFullName, IBmEmfIndexProvider bmEmfIndexProvider)
    {
        EClass mdLiteral = getMdLiteral(objectFullName);
        QualifiedName qnObjectName = getConfigurationObjectQualifiedName(objectFullName, mdLiteral);

        MdObject object = null;

        Iterable<IEObjectDescription> objectIndex =
            bmEmfIndexProvider.getEObjectIndexByType(mdLiteral, qnObjectName, true);
        Iterator<IEObjectDescription> objectItr = objectIndex.iterator();
        if (objectItr.hasNext())
        {
            object = (MdObject)objectItr.next().getEObjectOrProxy();
        }

        if (object == null)
        {
            return null;
        }

        return object;
    }

    private static QualifiedName getConfigurationObjectQualifiedName(String objectFullName, EClass mdLiteral)
    {
        String[] objectArray = objectFullName.substring(objectFullName.indexOf('.') + 1).split("[.]"); //$NON-NLS-1$

        QualifiedName qnObjectName = null;
        for (String objectValue : objectArray)
        {
            if (qnObjectName == null)
            {
                qnObjectName = QualifiedName.create(mdLiteral.getName(), objectValue);
            }

            else
            {
                if (mdLiteral.equals(MdClassPackage.Literals.SUBSYSTEM))
                {
                    qnObjectName = qnObjectName.append(QualifiedName.create(mdLiteral.getName(), objectValue));
                }

                else
                {
                    qnObjectName = qnObjectName.append(QualifiedName.create(objectValue));
                }

            }

        }

        return qnObjectName;
    }

    private static EClass getMdLiteral(String objectFullName)
    {
        EClass mdLiteral = MdClassPackage.Literals.CONFIGURATION;

        String objectType = objectFullName.substring(0, objectFullName.indexOf('.'));

        if (objectType.equals("Подсистема")) //$NON-NLS-1$
        {
            mdLiteral = MdClassPackage.Literals.SUBSYSTEM;
        }

        else if (objectType.equals("ОбщийМодуль")) //$NON-NLS-1$
        {
            mdLiteral = MdClassPackage.Literals.COMMON_MODULE;
        }

        else if (objectType.equals("Справочник")) //$NON-NLS-1$
        {
            mdLiteral = MdClassPackage.Literals.CATALOG;
        }

        else if (objectType.equals("Документ")) //$NON-NLS-1$
        {
            mdLiteral = MdClassPackage.Literals.DOCUMENT;
        }

        else if (objectType.equals("Перечисление")) //$NON-NLS-1$
        {
            mdLiteral = MdClassPackage.Literals.ENUM;
        }

        else if (objectType.equals("ПланВидовХарактеристик")) //$NON-NLS-1$
        {
            mdLiteral = MdClassPackage.Literals.CHART_OF_CHARACTERISTIC_TYPES;
        }

        else if (objectType.equals("ПланВидовРасчета")) //$NON-NLS-1$
        {
            mdLiteral = MdClassPackage.Literals.CHART_OF_CALCULATION_TYPES;
        }

        else if (objectType.equals("РегистрСведений")) //$NON-NLS-1$
        {
            mdLiteral = MdClassPackage.Literals.INFORMATION_REGISTER;
        }

        return mdLiteral;
    }

    private MdUtils()
    {
        throw new IllegalStateException(Messages.Internal_class);
    }
}
