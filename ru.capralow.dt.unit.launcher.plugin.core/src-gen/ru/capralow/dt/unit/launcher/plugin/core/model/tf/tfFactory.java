/**
 */
package ru.capralow.dt.unit.launcher.plugin.core.model.tf;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see ru.capralow.dt.unit.launcher.plugin.core.model.tf.tfPackage
 * @generated
 */
public interface tfFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	tfFactory eINSTANCE = ru.capralow.dt.unit.launcher.plugin.core.model.tf.impl.tfFactoryImpl.init();

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
	tfPackage gettfPackage();

} //tfFactory
