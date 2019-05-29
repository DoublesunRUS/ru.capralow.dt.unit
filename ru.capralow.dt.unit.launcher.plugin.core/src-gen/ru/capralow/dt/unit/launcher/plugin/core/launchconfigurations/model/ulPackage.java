/**
 */
package ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.ulFactory
 * @model kind="package"
 *        annotation="http://www.eclipse.org/emf/2002/GenModel loadInitialization='false' literalsInterface='true' nonNLSMarkers='true' prefix='ul' updateClasspath='false' basePackage='ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations'"
 * @generated
 */
public interface ulPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "model"; //$NON-NLS-1$

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model"; //$NON-NLS-1$

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "ul"; //$NON-NLS-1$

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ulPackage eINSTANCE = ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.impl.ulPackageImpl.init();

	/**
	 * The meta object id for the '{@link ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.impl.TestFrameworkImpl <em>Test Framework</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.impl.TestFrameworkImpl
	 * @see ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.impl.ulPackageImpl#getTestFramework()
	 * @generated
	 */
	int TEST_FRAMEWORK = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEST_FRAMEWORK__NAME = 0;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEST_FRAMEWORK__VERSION = 1;

	/**
	 * The feature id for the '<em><b>Resource Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEST_FRAMEWORK__RESOURCE_PATH = 2;

	/**
	 * The feature id for the '<em><b>Epf Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEST_FRAMEWORK__EPF_NAME = 3;

	/**
	 * The number of structural features of the '<em>Test Framework</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEST_FRAMEWORK_FEATURE_COUNT = 4;

	/**
	 * The operation id for the '<em>To String</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEST_FRAMEWORK___TO_STRING = 0;

	/**
	 * The number of operations of the '<em>Test Framework</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEST_FRAMEWORK_OPERATION_COUNT = 1;


	/**
	 * Returns the meta object for class '{@link ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.TestFramework <em>Test Framework</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Test Framework</em>'.
	 * @see ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.TestFramework
	 * @generated
	 */
	EClass getTestFramework();

	/**
	 * Returns the meta object for the attribute '{@link ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.TestFramework#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.TestFramework#getName()
	 * @see #getTestFramework()
	 * @generated
	 */
	EAttribute getTestFramework_Name();

	/**
	 * Returns the meta object for the attribute '{@link ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.TestFramework#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.TestFramework#getVersion()
	 * @see #getTestFramework()
	 * @generated
	 */
	EAttribute getTestFramework_Version();

	/**
	 * Returns the meta object for the attribute '{@link ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.TestFramework#getResourcePath <em>Resource Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Resource Path</em>'.
	 * @see ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.TestFramework#getResourcePath()
	 * @see #getTestFramework()
	 * @generated
	 */
	EAttribute getTestFramework_ResourcePath();

	/**
	 * Returns the meta object for the attribute '{@link ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.TestFramework#getEpfName <em>Epf Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Epf Name</em>'.
	 * @see ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.TestFramework#getEpfName()
	 * @see #getTestFramework()
	 * @generated
	 */
	EAttribute getTestFramework_EpfName();

	/**
	 * Returns the meta object for the '{@link ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.TestFramework#toString() <em>To String</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>To String</em>' operation.
	 * @see ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.TestFramework#toString()
	 * @generated
	 */
	EOperation getTestFramework__ToString();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ulFactory getulFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.impl.TestFrameworkImpl <em>Test Framework</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.impl.TestFrameworkImpl
		 * @see ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.impl.ulPackageImpl#getTestFramework()
		 * @generated
		 */
		EClass TEST_FRAMEWORK = eINSTANCE.getTestFramework();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TEST_FRAMEWORK__NAME = eINSTANCE.getTestFramework_Name();

		/**
		 * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TEST_FRAMEWORK__VERSION = eINSTANCE.getTestFramework_Version();

		/**
		 * The meta object literal for the '<em><b>Resource Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TEST_FRAMEWORK__RESOURCE_PATH = eINSTANCE.getTestFramework_ResourcePath();

		/**
		 * The meta object literal for the '<em><b>Epf Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TEST_FRAMEWORK__EPF_NAME = eINSTANCE.getTestFramework_EpfName();

		/**
		 * The meta object literal for the '<em><b>To String</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation TEST_FRAMEWORK___TO_STRING = eINSTANCE.getTestFramework__ToString();

	}

} //ulPackage
