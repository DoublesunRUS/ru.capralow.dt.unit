/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.junit.model;

/**
 * @author Aleksandr Kapralov
 *
 */
public interface ITestCaseElement
    extends ITestElement
{
    /**
     * @return String
     */
    String getTestClassName();

    /**
     * @return String
     */
    String getTestMethodName();
}
