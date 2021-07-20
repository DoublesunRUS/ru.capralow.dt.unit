/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.junit.model;

public interface ITestCaseElement
    extends ITestElement
{
    String getTestClassName();

    String getTestMethodName();
}
