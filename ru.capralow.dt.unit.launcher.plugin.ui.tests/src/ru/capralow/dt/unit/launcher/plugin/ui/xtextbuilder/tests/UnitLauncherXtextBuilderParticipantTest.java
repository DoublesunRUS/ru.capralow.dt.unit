package ru.capralow.dt.unit.launcher.plugin.ui.xtextbuilder.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ru.capralow.dt.unit.launcher.plugin.ui.xtextbuilder.UnitLauncherXtextBuilderParticipant;

public class UnitLauncherXtextBuilderParticipantTest {
	private static final String UNIT_TEST = "// @unit-test";

	@Test
	public void testGetUnitTestKeyFromMethodTextEmpty() {

		String keyName = UnitLauncherXtextBuilderParticipant.getUnitTestKeyFromMethodText("");

		assertEquals("Ключ теста: пустой", null, keyName);
	}

	@Test
	public void testGetUnitTestKeyFromMethodTextKeyAndSubkey() {

		String methodText = "// @unit-test:slow";

		String keyName = UnitLauncherXtextBuilderParticipant.getUnitTestKeyFromMethodText(methodText);

		assertEquals("Ключ теста: одна строка с модификаторами", "slow", keyName);
	}

	@Test
	public void testGetUnitTestKeyFromMethodTextKeyAndSubkeyAndTrash() {

		String methodText = "// @unit-test:slow медленный тест";

		String keyName = UnitLauncherXtextBuilderParticipant.getUnitTestKeyFromMethodText(methodText);

		assertEquals("Ключ теста: одна строка с модификаторами и мусорным текстом", "slow", keyName);
	}

	@Test
	public void testGetUnitTestKeyFromMethodTextKeyAndSubkeyNonCanonical() {

		String methodText = "//   @UniT-TeSt:slow";

		String keyName = UnitLauncherXtextBuilderParticipant.getUnitTestKeyFromMethodText(methodText);

		assertEquals("Ключ теста: одна строка с модификаторами неканоническая", "slow", keyName);
	}

	@Test
	public void testGetUnitTestKeyFromMethodTextKeyOnly() {

		String methodText = UNIT_TEST;

		String keyName = UnitLauncherXtextBuilderParticipant.getUnitTestKeyFromMethodText(methodText);

		assertEquals("Ключ теста: одна строка без модификаторов", "", keyName);
	}

	@Test
	public void testGetUnitTestKeyFromMethodTextKeyAndTrash() {

		String methodText = "// @unit-test   ";

		String keyName = UnitLauncherXtextBuilderParticipant.getUnitTestKeyFromMethodText(methodText);

		assertEquals("Ключ теста: одна строка c мусорным текстом", "", keyName);
	}

	@Test
	public void testGetUnitTestKeyFromMethodTextKeyOnlyMultiline1() {

		String methodText = String
				.join(System.lineSeparator(), UNIT_TEST, "// Описание процедуры 1", "// Модульное тестирование 1");

		String keyName = UnitLauncherXtextBuilderParticipant.getUnitTestKeyFromMethodText(methodText);

		assertEquals("Ключ теста: несколько строк 1", "", keyName);
	}

	@Test
	public void testGetUnitTestKeyFromMethodTextKeyOnlyMultiline2() {

		String methodText = String
				.join(System.lineSeparator(), "// Описание процедуры 2", UNIT_TEST, "// Модульное тестирование 2");

		String keyName = UnitLauncherXtextBuilderParticipant.getUnitTestKeyFromMethodText(methodText);

		assertEquals("Ключ теста: несколько строк 2", "", keyName);
	}

	@Test
	public void testGetUnitTestKeyFromMethodTextKeyOnlyMultiline3() {

		String methodText = String
				.join(System.lineSeparator(), "// Описание процедуры 3", "// Модульное тестирование 3", UNIT_TEST);

		String keyName = UnitLauncherXtextBuilderParticipant.getUnitTestKeyFromMethodText(methodText);

		assertEquals("Ключ теста: несколько строк 3", "", keyName);
	}

	@Test
	public void testGetUnitTestKeyFromMethodTextKeyOnlyNonCanonical() {

		String methodText = "//   @UniT-TeSt";

		String keyName = UnitLauncherXtextBuilderParticipant.getUnitTestKeyFromMethodText(methodText);

		assertEquals("Ключ теста: одна строка без модификаторов неканоническая", "", keyName);
	}
}
