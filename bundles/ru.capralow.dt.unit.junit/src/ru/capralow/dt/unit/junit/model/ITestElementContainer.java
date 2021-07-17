package ru.capralow.dt.unit.junit.model;

public interface ITestElementContainer
    extends ITestElement
{
    public ITestElement[] getChildren();
}
