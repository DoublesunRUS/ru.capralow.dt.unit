/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.junit.model;

/**
 * @author Aleksandr Kapralov
 *
 */
public interface ITestElementContainer
    extends ITestElement
{
    /**
     * @return ITestElement
     */
    ITestElement[] getChildren();
}
