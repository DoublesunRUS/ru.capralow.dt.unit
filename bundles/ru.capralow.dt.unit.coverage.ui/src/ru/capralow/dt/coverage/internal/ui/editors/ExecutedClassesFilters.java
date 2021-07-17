/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.editors;

import org.eclipse.jface.viewers.AcceptAllFilter;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.runtime.WildcardMatcher;

/**
 * Filters for executed classes.
 */
final class ExecutedClassesFilters
{

    public static IFilter filterFromPatternString(String pattern)
    {
        if (pattern.length() == 0)
        {
            return AcceptAllFilter.getInstance();
        }

        if (pattern.startsWith("0x")) //$NON-NLS-1$
        {
            return new ClassIdMatcher(pattern);
        }

        return new ClassNameMatcher(pattern);
    }

    public static ViewerFilter fromPatternString(final String pattern)
    {
        return new ViewerFilter()
        {

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element)
            {
                return filterFromPatternString(pattern).select(element);
            }
        };
    }

    // No instances
    private ExecutedClassesFilters()
    {
    }

    private static class ClassIdMatcher
        extends PatternMatchingFilter
    {

        ClassIdMatcher(String patternString)
        {
            super(patternString);
        }

        @Override
        protected String getMatchedValue(Object toTest)
        {
            return String.format("0x%016x", //$NON-NLS-1$
                Long.valueOf(((ExecutionData)toTest).getId()));
        }

    }

    private static class ClassNameMatcher
        extends PatternMatchingFilter
    {
        ClassNameMatcher(String patternString)
        {
            super(patternString);
        }

        @Override
        protected String getMatchedValue(Object toTest)
        {
            return ((ExecutionData)toTest).getName();
        }
    }

    private abstract static class PatternMatchingFilter
        implements IFilter
    {
        private WildcardMatcher matcher;

        PatternMatchingFilter(String patternString)
        {
            matcher = new WildcardMatcher(patternString);
        }

        @Override
        public boolean select(Object toTest)
        {
            return matcher.matches(getMatchedValue(toTest));
        }

        protected abstract String getMatchedValue(Object toTest);

    }

}
