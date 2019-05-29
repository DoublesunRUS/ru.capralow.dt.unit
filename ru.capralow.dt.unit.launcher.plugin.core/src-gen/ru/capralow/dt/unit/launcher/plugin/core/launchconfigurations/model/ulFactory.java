/**
 */
package ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.ulPackage
 * @generated
 */
public interface ulFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ulFactory eINSTANCE = ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.impl.ulFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Test Framework</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Test Framework</em>'.
	 * @generated
	 */
	TestFramework createTestFramework();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	ulPackage getulPackage();

} //ulFactory
