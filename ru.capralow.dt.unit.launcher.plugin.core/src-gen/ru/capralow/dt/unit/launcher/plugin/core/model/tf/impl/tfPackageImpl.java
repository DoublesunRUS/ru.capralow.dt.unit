/**
 */
package ru.capralow.dt.unit.launcher.plugin.core.model.tf.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import ru.capralow.dt.unit.launcher.plugin.core.model.tf.TestFramework;
import ru.capralow.dt.unit.launcher.plugin.core.model.tf.tfFactory;
import ru.capralow.dt.unit.launcher.plugin.core.model.tf.tfPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class tfPackageImpl extends EPackageImpl implements tfPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass testFrameworkEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see ru.capralow.dt.unit.launcher.plugin.core.model.tf.tfPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private tfPackageImpl() {
		super(eNS_URI, tfFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link tfPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static tfPackage init() {
		if (isInited) return (tfPackage)EPackage.Registry.INSTANCE.getEPackage(tfPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredtfPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		tfPackageImpl thetfPackage = registeredtfPackage instanceof tfPackageImpl ? (tfPackageImpl)registeredtfPackage : new tfPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		EcorePackage.eINSTANCE.eClass();

		// Create package meta-data objects
		thetfPackage.createPackageContents();

		// Initialize created meta-data
		thetfPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		thetfPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(tfPackage.eNS_URI, thetfPackage);
		return thetfPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTestFramework() {
		return testFrameworkEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTestFramework_Name() {
		return (EAttribute)testFrameworkEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTestFramework_Version() {
		return (EAttribute)testFrameworkEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTestFramework_ResourcePath() {
		return (EAttribute)testFrameworkEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTestFramework_EpfName() {
		return (EAttribute)testFrameworkEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getTestFramework__ToString() {
		return testFrameworkEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public tfFactory gettfFactory() {
		return (tfFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		testFrameworkEClass = createEClass(TEST_FRAMEWORK);
		createEAttribute(testFrameworkEClass, TEST_FRAMEWORK__NAME);
		createEAttribute(testFrameworkEClass, TEST_FRAMEWORK__VERSION);
		createEAttribute(testFrameworkEClass, TEST_FRAMEWORK__RESOURCE_PATH);
		createEAttribute(testFrameworkEClass, TEST_FRAMEWORK__EPF_NAME);
		createEOperation(testFrameworkEClass, TEST_FRAMEWORK___TO_STRING);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes, features, and operations; add parameters
		initEClass(testFrameworkEClass, TestFramework.class, "TestFramework", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getTestFramework_Name(), theEcorePackage.getEString(), "name", "", 0, 1, TestFramework.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getTestFramework_Version(), theEcorePackage.getEString(), "version", "", 0, 1, TestFramework.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getTestFramework_ResourcePath(), theEcorePackage.getEString(), "resourcePath", "", 0, 1, TestFramework.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
		initEAttribute(getTestFramework_EpfName(), theEcorePackage.getEString(), "epfName", "", 0, 1, TestFramework.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$

		initEOperation(getTestFramework__ToString(), theEcorePackage.getEString(), "toString", 0, 1, !IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

		// Create resource
		createResource(eNS_URI);
	}

} //tfPackageImpl
