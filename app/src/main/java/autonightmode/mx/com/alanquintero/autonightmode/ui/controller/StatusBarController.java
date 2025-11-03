/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode.ui.controller;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import autonightmode.mx.com.alanquintero.autonightmode.R;

/**
 * StatusBarController is responsible for applying consistent visual style to the Status Bar.
 *
 * @author Alan Quintero
 */
public class StatusBarController {

    private final @NonNull Context context;
    private final @NonNull FragmentActivity activity;

    public StatusBarController(final @NonNull Context context, final @NonNull FragmentActivity activity) {
        this.context = context;
        this.activity = activity;
    }

    /**
     * Sets up the status bar color based on the current system theme (light or dark mode).
     */
    public void setupStatusBarColor() {
        int lightModeColor = context.getResources().getColor(R.color.status_bar_light); // light background
        int darkModeColor = context.getResources().getColor(R.color.status_bar_dark);  // dark gray, not pure black

        int currentNightMode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;

        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            // Light mode
            activity.getWindow().setStatusBarColor(lightModeColor);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } else {
            // Dark mode
            activity.getWindow().setStatusBarColor(darkModeColor);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Remove light status bar flag to make icons white
                activity.getWindow().getDecorView().setSystemUiVisibility(0);
            }
        }
    }
}
