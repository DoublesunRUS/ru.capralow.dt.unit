/*******************************************************************************
 * Copyright (c) 2006, 2019 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *
 ******************************************************************************/
package methodlocator;

import java.util.Date;

/**
 * Collections of methods with different Signatures.
 */
public class Samples {

  /* <init>()V */
  Samples() {
  }

  /* <init>(Ljava/lang/String;)V */
  Samples(String param) {
  }

  /* <init>(I)V */
  Samples(int param) {
  }

  /* (Ljava/lang/String;)V */
  void m1(String s) {
  }

  /* (Ljava/lang/Integer;)V */
  void m2(Integer i) {
  }

  /* (Ljava/lang/Number;)V */
  void m2(Number n) {
  }

}
