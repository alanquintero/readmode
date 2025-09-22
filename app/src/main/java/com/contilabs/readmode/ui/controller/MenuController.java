/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui.controller;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;

import com.contilabs.readmode.R;
import com.contilabs.readmode.model.ReadModeSettings;
import com.contilabs.readmode.ui.SettingsDialog;
import com.contilabs.readmode.util.PrefsHelper;

/**
 * MenuController is responsible for applying consistent visual styles the menu.
 *
 * @author Alan Quintero
 */
public class MenuController {

    private static final String TAG = MenuController.class.getSimpleName();

    private final @NonNull Context context;
    private final @NonNull FragmentActivity activity;
    private final @NonNull PrefsHelper prefsHelper;
    private final @NonNull ReadModeSettings readModeSettings;
    private final @Nullable ImageView menu;

    public MenuController(final @NonNull Context context, final @NonNull FragmentActivity activity, final @NonNull View rootView, final @NonNull ReadModeSettings readModeSettings) {
        this.context = context;
        this.activity = activity;
        this.prefsHelper = PrefsHelper.init(context);
        this.readModeSettings = readModeSettings;
        this.menu = rootView.findViewById(R.id.bannerMenu);
    }

    public void setupMenu() {
        if (menu != null) {
            menu.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(context, v);
                popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.action_settings) {
                        final SettingsDialog dialog = new SettingsDialog();
                        // Code to run after dialog is closed
                        dialog.setOnSettingsClosedListener(this::reloadSettings);
                        dialog.show(activity.getSupportFragmentManager(), "settingsDialog");
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }
    }

    private void reloadSettings() {
        Log.d(TAG, "reloading Settings");
        readModeSettings.setAutoStartReadMode(prefsHelper.getAutoStartReadMode());
        readModeSettings.setShouldUseSameIntensityBrightnessForAll(prefsHelper.shouldUseSameIntensityBrightnessForAll());
        // TODO create listeners
        // textViewStyler.setColorSettingsText(readModeSettings, colors);
        // seekBarsStyler.updateSeekBarsForSelectedColor(readModeSettings);
    }
}
