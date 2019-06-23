/**
 */
package ru.capralow.dt.unit.launcher.plugin.core.model.tf;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Test Framework</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ru.capralow.dt.unit.launcher.plugin.core.model.tf.TestFramework#getName <em>Name</em>}</li>
 *   <li>{@link ru.capralow.dt.unit.launcher.plugin.core.model.tf.TestFramework#getVersion <em>Version</em>}</li>
 *   <li>{@link ru.capralow.dt.unit.launcher.plugin.core.model.tf.TestFramework#getResourcePath <em>Resource Path</em>}</li>
 *   <li>{@link ru.capralow.dt.unit.launcher.plugin.core.model.tf.TestFramework#getEpfName <em>Epf Name</em>}</li>
 * </ul>
 *
 * @see ru.capralow.dt.unit.launcher.plugin.core.model.tf.tfPackage#getTestFramework()
 * @model
 * @generated
 */
public interface TestFramework extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see ru.capralow.dt.unit.launcher.plugin.core.model.tf.tfPackage#getTestFramework_Name()
	 * @model default="" unique="false"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link ru.capralow.dt.unit.launcher.plugin.core.model.tf.TestFramework#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Version</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Version</em>' attribute.
	 * @see #setVersion(String)
	 * @see ru.capralow.dt.unit.launcher.plugin.core.model.tf.tfPackage#getTestFramework_Version()
	 * @model default="" unique="false"
	 * @generated
	 */
	String getVersion();

	/**
	 * Sets the value of the '{@link ru.capralow.dt.unit.launcher.plugin.core.model.tf.TestFramework#getVersion <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Version</em>' attribute.
	 * @see #getVersion()
	 * @generated
	 */
	void setVersion(String value);

	/**
	 * Returns the value of the '<em><b>Resource Path</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Resource Path</em>' attribute.
	 * @see #setResourcePath(String)
	 * @see ru.capralow.dt.unit.launcher.plugin.core.model.tf.tfPackage#getTestFramework_ResourcePath()
	 * @model default="" unique="false"
	 * @generated
	 */
	String getResourcePath();

	/**
	 * Sets the value of the '{@link ru.capralow.dt.unit.launcher.plugin.core.model.tf.TestFramework#getResourcePath <em>Resource Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Resource Path</em>' attribute.
	 * @see #getResourcePath()
	 * @generated
	 */
	void setResourcePath(String value);

	/**
	 * Returns the value of the '<em><b>Epf Name</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Epf Name</em>' attribute.
	 * @see #setEpfName(String)
	 * @see ru.capralow.dt.unit.launcher.plugin.core.model.tf.tfPackage#getTestFramework_EpfName()
	 * @model default="" unique="false"
	 * @generated
	 */
	String getEpfName();

	/**
	 * Sets the value of the '{@link ru.capralow.dt.unit.launcher.plugin.core.model.tf.TestFramework#getEpfName <em>Epf Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Epf Name</em>' attribute.
	 * @see #getEpfName()
	 * @generated
	 */
	void setEpfName(String value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model unique="false"
	 * @generated
	 */
	String toString();

} // TestFramework
