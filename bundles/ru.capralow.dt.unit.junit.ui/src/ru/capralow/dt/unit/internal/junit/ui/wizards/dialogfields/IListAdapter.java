/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package ru.capralow.dt.unit.internal.junit.ui.wizards.dialogfields;

/**
 * Change listener used by <code>ListDialogField</code> and <code>CheckedListDialogField</code>
 *
 * @param <E> the type of the list elements
 */
public interface IListAdapter<E> {

	/**
	 * A button from the button bar has been pressed.
	 *
	 * @param field the dialog field
	 * @param index the button index
	 */
	void customButtonPressed(ListDialogField<E> field, int index);

	/**
	 * The selection of the list has changed.
	 *
	 * @param field the dialog field
	 */
	void selectionChanged(ListDialogField<E> field);

	/**
	 * An entry in the list has been double clicked
	 *
	 * @param field the dialog field
	 */
	void doubleClicked(ListDialogField<E> field);

}
