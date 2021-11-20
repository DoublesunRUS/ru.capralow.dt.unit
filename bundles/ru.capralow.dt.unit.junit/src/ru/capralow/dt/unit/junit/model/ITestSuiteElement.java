/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.junit.model;

/**
 * @author Aleksandr Kapralov
 *
 */
public interface ITestSuiteElement
    extends ITestElementContainer
{
    /**
     * @return String
     */
    String getSuiteTypeName();
}
