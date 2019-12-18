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
package ru.capralow.dt.coverage.internal.core.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Version;

import ru.capralow.dt.coverage.core.JavaProjectKit;

/**
 * Tests for {@link MethodLocator}.
 */
public class MethodLocatorTest {

	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=381503
	 */
	private static final boolean JDT_3_13 = JavaCore.getPlugin().getBundle().getVersion()
			.compareTo(new Version("3.13.0")) >= 0;

	private JavaProjectKit javaProject;

	private MethodLocator methodLocator;

	@Before
	public void setup() throws Exception {
		javaProject = new JavaProjectKit();
		javaProject.enableJava5();
		final IPackageFragmentRoot root = javaProject.createSourceFolder("src");
		final ICompilationUnit compilationUnit = javaProject
				.createCompilationUnit(root, "testdata/src", "methodlocator/Samples.java");
		JavaProjectKit.waitForBuild();
		javaProject.assertNoErrors();
		methodLocator = new MethodLocator(compilationUnit.getTypes()[0]);
	}

	@After
	public void teardown() throws Exception {
		javaProject.destroy();
	}

	private final void assertMethod(final String expectedKey, final String name, final String signature) {
		final IMethod method = methodLocator.findMethod(name, signature);
		assertNotNull(method);
		assertEquals(expectedKey, method.getKey());
	}

	@Test
	public void testUnambiguousConstructor() {
		assertMethod(JDT_3_13 ? "Lmethodlocator/Samples;.()V" : "Lmethodlocator/Samples;.Samples()V", "<init>", "()V");
	}

	@Test
	public void testAmbiguousConstructor1() {
		assertMethod(JDT_3_13 ? "Lmethodlocator/Samples;.(QString;)V" : "Lmethodlocator/Samples;.Samples(QString;)V",
				"<init>",
				"(Ljava/lang/String;)V");
	}

	@Test
	public void testAmbiguousConstructor2() {
		assertMethod(JDT_3_13 ? "Lmethodlocator/Samples;.(I)V" : "Lmethodlocator/Samples;.Samples(I)V",
				"<init>",
				"(I)V");
	}

	@Test
	public void testUnambiguousMethod() {
		assertMethod("Lmethodlocator/Samples;.m1(QString;)V", "m1", "(Ljava/lang/String;)V");
	}

	@Test
	public void testAmbiguousMethod1() {
		assertMethod("Lmethodlocator/Samples;.m2(QInteger;)V", "m2", "(Ljava/lang/Integer;)V");
	}

	@Test
	public void testAmbiguousMethod2() {
		assertMethod("Lmethodlocator/Samples;.m2(QNumber;)V", "m2", "(Ljava/lang/Number;)V");
	}

}
