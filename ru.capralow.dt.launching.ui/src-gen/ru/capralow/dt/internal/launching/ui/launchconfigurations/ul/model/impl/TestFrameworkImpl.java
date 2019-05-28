/**
 */
package ru.capralow.dt.internal.launching.ui.launchconfigurations.ul.model.impl;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import ru.capralow.dt.internal.launching.ui.launchconfigurations.ul.model.TestFramework;
import ru.capralow.dt.internal.launching.ui.launchconfigurations.ul.model.ulPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Test Framework</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link ru.capralow.dt.internal.launching.ui.launchconfigurations.ul.model.impl.TestFrameworkImpl#getName <em>Name</em>}</li>
 *   <li>{@link ru.capralow.dt.internal.launching.ui.launchconfigurations.ul.model.impl.TestFrameworkImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link ru.capralow.dt.internal.launching.ui.launchconfigurations.ul.model.impl.TestFrameworkImpl#getResourcePath <em>Resource Path</em>}</li>
 *   <li>{@link ru.capralow.dt.internal.launching.ui.launchconfigurations.ul.model.impl.TestFrameworkImpl#getEpfName <em>Epf Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TestFrameworkImpl extends MinimalEObjectImpl.Container implements TestFramework {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = ""; //$NON-NLS-1$

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getVersion() <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersion()
	 * @generated
	 * @ordered
	 */
	protected static final String VERSION_EDEFAULT = ""; //$NON-NLS-1$

	/**
	 * The cached value of the '{@link #getVersion() <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersion()
	 * @generated
	 * @ordered
	 */
	protected String version = VERSION_EDEFAULT;

	/**
	 * The default value of the '{@link #getResourcePath() <em>Resource Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResourcePath()
	 * @generated
	 * @ordered
	 */
	protected static final String RESOURCE_PATH_EDEFAULT = ""; //$NON-NLS-1$

	/**
	 * The cached value of the '{@link #getResourcePath() <em>Resource Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResourcePath()
	 * @generated
	 * @ordered
	 */
	protected String resourcePath = RESOURCE_PATH_EDEFAULT;

	/**
	 * The default value of the '{@link #getEpfName() <em>Epf Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEpfName()
	 * @generated
	 * @ordered
	 */
	protected static final String EPF_NAME_EDEFAULT = ""; //$NON-NLS-1$

	/**
	 * The cached value of the '{@link #getEpfName() <em>Epf Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEpfName()
	 * @generated
	 * @ordered
	 */
	protected String epfName = EPF_NAME_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TestFrameworkImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ulPackage.Literals.TEST_FRAMEWORK;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ulPackage.TEST_FRAMEWORK__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getVersion() {
		return version;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setVersion(String newVersion) {
		String oldVersion = version;
		version = newVersion;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ulPackage.TEST_FRAMEWORK__VERSION, oldVersion, version));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getResourcePath() {
		return resourcePath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setResourcePath(String newResourcePath) {
		String oldResourcePath = resourcePath;
		resourcePath = newResourcePath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ulPackage.TEST_FRAMEWORK__RESOURCE_PATH, oldResourcePath, resourcePath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getEpfName() {
		return epfName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setEpfName(String newEpfName) {
		String oldEpfName = epfName;
		epfName = newEpfName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ulPackage.TEST_FRAMEWORK__EPF_NAME, oldEpfName, epfName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		String _name = this.getName();
		String _plus = (_name + " ");
		String _version = this.getVersion();
		return (_plus + _version);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ulPackage.TEST_FRAMEWORK__NAME:
				return getName();
			case ulPackage.TEST_FRAMEWORK__VERSION:
				return getVersion();
			case ulPackage.TEST_FRAMEWORK__RESOURCE_PATH:
				return getResourcePath();
			case ulPackage.TEST_FRAMEWORK__EPF_NAME:
				return getEpfName();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ulPackage.TEST_FRAMEWORK__NAME:
				setName((String)newValue);
				return;
			case ulPackage.TEST_FRAMEWORK__VERSION:
				setVersion((String)newValue);
				return;
			case ulPackage.TEST_FRAMEWORK__RESOURCE_PATH:
				setResourcePath((String)newValue);
				return;
			case ulPackage.TEST_FRAMEWORK__EPF_NAME:
				setEpfName((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case ulPackage.TEST_FRAMEWORK__NAME:
				setName(NAME_EDEFAULT);
				return;
			case ulPackage.TEST_FRAMEWORK__VERSION:
				setVersion(VERSION_EDEFAULT);
				return;
			case ulPackage.TEST_FRAMEWORK__RESOURCE_PATH:
				setResourcePath(RESOURCE_PATH_EDEFAULT);
				return;
			case ulPackage.TEST_FRAMEWORK__EPF_NAME:
				setEpfName(EPF_NAME_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ulPackage.TEST_FRAMEWORK__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case ulPackage.TEST_FRAMEWORK__VERSION:
				return VERSION_EDEFAULT == null ? version != null : !VERSION_EDEFAULT.equals(version);
			case ulPackage.TEST_FRAMEWORK__RESOURCE_PATH:
				return RESOURCE_PATH_EDEFAULT == null ? resourcePath != null : !RESOURCE_PATH_EDEFAULT.equals(resourcePath);
			case ulPackage.TEST_FRAMEWORK__EPF_NAME:
				return EPF_NAME_EDEFAULT == null ? epfName != null : !EPF_NAME_EDEFAULT.equals(epfName);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
			case ulPackage.TEST_FRAMEWORK___TO_STRING:
				return toString();
		}
		return super.eInvoke(operationID, arguments);
	}

} //TestFrameworkImpl
