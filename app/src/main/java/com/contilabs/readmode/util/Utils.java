/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Utility class that provides common helper methods used throughout the app.
 *
 * @author Alan Quintero
 */
public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    /**
     * Apply the selected theme to the app.
     */
    public static void setAppTheme(final @NonNull Constants.ThemeMode themeMode) {
        Log.d(TAG, "Applying theme...");
        if (themeMode == Constants.ThemeMode.SYSTEM_DEFAULT) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            Log.d(TAG, "Applied System Default theme");
        } else if (themeMode == Constants.ThemeMode.LIGHT) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Log.d(TAG, "Applied Light theme");
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Log.d(TAG, "Applied Dark theme");
        }
    }
}
