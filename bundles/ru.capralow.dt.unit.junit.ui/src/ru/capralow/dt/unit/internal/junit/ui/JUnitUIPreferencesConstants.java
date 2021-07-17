package ru.capralow.dt.unit.internal.junit.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Defines constants which are used to refer to values in the plugin's preference store.
 *
 * @since 3.7
 */
public class JUnitUIPreferencesConstants
{
    /**
     * Boolean preference controlling whether newly launched JUnit tests should be shown in all
     * JUnit views (in all windows).
     */
    public static final String SHOW_IN_ALL_VIEWS = JUnitUiPlugin.ID + ".show_in_all_views"; //$NON-NLS-1$

    public static final boolean SHOW_IN_ALL_VIEWS_DEFAULT = false; // would need a PreferenceInitializer if this was changed to true!

    private static final String CODEASSIST_FAVORITE_STATIC_MEMBERS_MIGRATED =
        JUnitUiPlugin.ID + ".content_assist_favorite_static_members_migrated"; //$NON-NLS-1$

    public static boolean getShowInAllViews()
    {
        return Platform.getPreferencesService()
            .getBoolean(JUnitUiPlugin.ID, SHOW_IN_ALL_VIEWS, SHOW_IN_ALL_VIEWS_DEFAULT, null);
    }

    public static boolean isCodeassistFavoriteStaticMembersMigrated()
    {
        return Platform.getPreferencesService()
            .getBoolean(JUnitUiPlugin.ID, CODEASSIST_FAVORITE_STATIC_MEMBERS_MIGRATED, false, null);
    }

    public static void setCodeassistFavoriteStaticMembersMigrated(boolean migrated)
    {
        IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(JUnitUiPlugin.ID);
        preferences.putBoolean(CODEASSIST_FAVORITE_STATIC_MEMBERS_MIGRATED, migrated);
        try
        {
            preferences.flush();
        }
        catch (BackingStoreException e)
        {
            JUnitUiPlugin.log(e);
        }
    }

    public static void setShowInAllViews(boolean show)
    {
        IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(JUnitUiPlugin.ID);
        preferences.putBoolean(SHOW_IN_ALL_VIEWS, show);
        try
        {
            preferences.flush();
        }
        catch (BackingStoreException e)
        {
            JUnitUiPlugin.log(e);
        }
    }

    private JUnitUIPreferencesConstants()
    {
        // no instance
    }
}
