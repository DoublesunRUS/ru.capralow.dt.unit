/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;

/**
 * Defines constants which are used to refer to values in the plugin's preference store.
 */
public final class JUnitPreferencesConstants
{
    /**
     * Boolean preference controlling whether the failure stack should be
     * filtered.
     */
    public static final String DO_FILTER_STACK = JUnitPlugin.ID + ".do_filter_stack"; //$NON-NLS-1$

    /**
     * Boolean preference controlling whether the JUnit view should be shown on
     * errors only.
     */
    public static final String SHOW_ON_ERROR_ONLY = JUnitPlugin.ID + ".show_on_error"; //$NON-NLS-1$

    /**
     * Boolean preference controlling whether '-ea' should be added to VM arguments when creating a
     * new JUnit launch configuration.
     */
    public static final String ENABLE_ASSERTIONS = JUnitPlugin.ID + ".enable_assertions"; //$NON-NLS-1$

    public static final boolean ENABLE_ASSERTIONS_DEFAULT = true;

    /**
     * List of active stack filters. A String containing a comma separated list
     * of fully qualified type names/patterns.
     */
    public static final String PREF_ACTIVE_FILTERS_LIST = JUnitPlugin.ID + ".active_filters"; //$NON-NLS-1$

    /**
     * List of inactive stack filters. A String containing a comma separated
     * list of fully qualified type names/patterns.
     */
    public static final String PREF_INACTIVE_FILTERS_LIST = JUnitPlugin.ID + ".inactive_filters"; //$NON-NLS-1$

    /**
     * Maximum number of remembered test runs.
     */
    public static final String MAX_TEST_RUNS = JUnitPlugin.ID + ".max_test_runs"; //$NON-NLS-1$

    private static final String[] FG_DEFAULT_FILTER_PATTERNS = new String[] { };

    /**
     * Returns the default list of active stack filters.
     *
     * @return list
     */
    public static List<String> createDefaultStackFiltersList()
    {
        return Arrays.asList(FG_DEFAULT_FILTER_PATTERNS);
    }

    public static String[] getFilterPatterns()
    {
        return JUnitPreferencesConstants.parseList(
            Platform.getPreferencesService().getString(JUnitPlugin.ID, PREF_ACTIVE_FILTERS_LIST, null, null));
    }

    public static boolean getFilterStack()
    {
        return Platform.getPreferencesService().getBoolean(JUnitPlugin.ID, DO_FILTER_STACK, true, null);
    }

    /**
     * Parses the comma-separated string into an array of strings.
     *
     * @param listString a comma-separated string
     * @return an array of strings
     */
    public static String[] parseList(String listString)
    {
        List<String> list = new ArrayList<>(10);
        StringTokenizer tokenizer = new StringTokenizer(listString, ","); //$NON-NLS-1$
        while (tokenizer.hasMoreTokens())
            list.add(tokenizer.nextToken());
        return list.toArray(new String[list.size()]);
    }

    /**
     * Serializes the array of strings into one comma-separated string.
     *
     * @param list array of strings
     * @return a single string composed of the given list
     */
    public static String serializeList(String[] list)
    {
        if (list == null)
            return ""; //$NON-NLS-1$

        return String.join(String.valueOf(','), list);
    }

    public static void setFilterStack(boolean filter)
    {
        InstanceScope.INSTANCE.getNode(JUnitPlugin.ID).putBoolean(DO_FILTER_STACK, filter);
    }

    private JUnitPreferencesConstants()
    {
        // no instance
    }
}
