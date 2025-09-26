/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDelegate;

import com.contilabs.readmode.R;

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

    /**
     * Returns a title with style for the dialog.
     */
    public static @NonNull View createDialogTitle(final @NonNull Context context, final @StringRes int titleRes) {
        final View customTitle = LayoutInflater.from(context).inflate(R.layout.dialog_title, null);
        final TextView titleView = customTitle.findViewById(R.id.dialog_title);
        titleView.setText(titleRes);
        return customTitle;
    }
}
