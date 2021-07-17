/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui.xtextbuilder.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ru.capralow.dt.unit.internal.junit.ui.xtextbuilder.UnitLauncherXtextBuilderParticipant;

public class UnitLauncherXtextBuilderParticipantTest
{
    private static final String UNIT_TEST = "// @unit-test"; //$NON-NLS-1$

    @Test
    public void testGetUnitTestKeyFromMethodTextDefaultKey()
    {

        String methodText = "// @unit-test:all"; //$NON-NLS-1$

        String keyName = UnitLauncherXtextBuilderParticipant.getUnitTestKeyFromMethodText(methodText);

        assertEquals("Ключ теста: ключ по умолчанию", "", keyName); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testGetUnitTestKeyFromMethodTextEmpty()
    {

        String keyName = UnitLauncherXtextBuilderParticipant.getUnitTestKeyFromMethodText(""); //$NON-NLS-1$

        assertEquals("Ключ теста: пустой", null, keyName); //$NON-NLS-1$
    }

    @Test
    public void testGetUnitTestKeyFromMethodTextKeyAndSubkey()
    {

        String methodText = "// @unit-test:slow"; //$NON-NLS-1$

        String keyName = UnitLauncherXtextBuilderParticipant.getUnitTestKeyFromMethodText(methodText);

        assertEquals("Ключ теста: одна строка с модификаторами", "slow", keyName); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testGetUnitTestKeyFromMethodTextKeyAndSubkeyAndTrash()
    {

        String methodText = "// @unit-test:slow медленный тест"; //$NON-NLS-1$

        String keyName = UnitLauncherXtextBuilderParticipant.getUnitTestKeyFromMethodText(methodText);

        assertEquals("Ключ теста: одна строка с модификаторами и мусорным текстом", "slow", keyName); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testGetUnitTestKeyFromMethodTextKeyAndSubkeyNonCanonical()
    {

        String methodText = "//   @UniT-TeSt:slow"; //$NON-NLS-1$

        String keyName = UnitLauncherXtextBuilderParticipant.getUnitTestKeyFromMethodText(methodText);

        assertEquals("Ключ теста: одна строка с модификаторами неканоническая", "slow", keyName); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testGetUnitTestKeyFromMethodTextKeyAndTrash()
    {

        String methodText = "// @unit-test   "; //$NON-NLS-1$

        String keyName = UnitLauncherXtextBuilderParticipant.getUnitTestKeyFromMethodText(methodText);

        assertEquals("Ключ теста: одна строка c мусорным текстом", "", keyName); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testGetUnitTestKeyFromMethodTextKeyOnly()
    {

        String methodText = UNIT_TEST;

        String keyName = UnitLauncherXtextBuilderParticipant.getUnitTestKeyFromMethodText(methodText);

        assertEquals("Ключ теста: одна строка без модификаторов", "", keyName); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testGetUnitTestKeyFromMethodTextKeyOnlyMultiline1()
    {

        String methodText = String.join(System.lineSeparator(), UNIT_TEST, "// Описание процедуры 1", //$NON-NLS-1$
            "// Модульное тестирование 1"); //$NON-NLS-1$

        String keyName = UnitLauncherXtextBuilderParticipant.getUnitTestKeyFromMethodText(methodText);

        assertEquals("Ключ теста: несколько строк 1", "", keyName); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testGetUnitTestKeyFromMethodTextKeyOnlyMultiline2()
    {

        String methodText = String.join(System.lineSeparator(), "// Описание процедуры 2", UNIT_TEST, //$NON-NLS-1$
            "// Модульное тестирование 2"); //$NON-NLS-1$

        String keyName = UnitLauncherXtextBuilderParticipant.getUnitTestKeyFromMethodText(methodText);

        assertEquals("Ключ теста: несколько строк 2", "", keyName); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testGetUnitTestKeyFromMethodTextKeyOnlyMultiline3()
    {

        String methodText = String.join(System.lineSeparator(), "// Описание процедуры 3", //$NON-NLS-1$
            "// Модульное тестирование 3", UNIT_TEST); //$NON-NLS-1$

        String keyName = UnitLauncherXtextBuilderParticipant.getUnitTestKeyFromMethodText(methodText);

        assertEquals("Ключ теста: несколько строк 3", "", keyName); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testGetUnitTestKeyFromMethodTextKeyOnlyNonCanonical()
    {

        String methodText = "//   @UniT-TeSt"; //$NON-NLS-1$

        String keyName = UnitLauncherXtextBuilderParticipant.getUnitTestKeyFromMethodText(methodText);

        assertEquals("Ключ теста: одна строка без модификаторов неканоническая", "", keyName); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
