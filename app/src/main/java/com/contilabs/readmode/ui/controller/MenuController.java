/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui.controller;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;

import com.contilabs.readmode.R;
import com.contilabs.readmode.model.ReadModeSettings;
import com.contilabs.readmode.observer.settings.SettingsSubject;
import com.contilabs.readmode.ui.dialog.SettingsDialog;

/**
 * MenuController is responsible for applying consistent visual styles the menu.
 *
 * @author Alan Quintero
 */
public class MenuController {

    private static final String TAG = MenuController.class.getSimpleName();

    private final @NonNull Context context;
    private final @NonNull FragmentActivity activity;
    private final @NonNull SettingsSubject settingsSubject;
    private final @NonNull ReadModeSettings readModeSettings;
    private final @NonNull ImageView menu;

    public MenuController(final @NonNull Context context, final @NonNull FragmentActivity activity, final @NonNull View rootView, final @NonNull SettingsSubject settingsSubject, final @NonNull ReadModeSettings readModeSettings) {
        this.context = context;
        this.activity = activity;
        this.settingsSubject = settingsSubject;
        this.readModeSettings = readModeSettings;
        this.menu = rootView.findViewById(R.id.bannerMenu);
    }

    public void setupMenu() {
        Log.e(TAG, "Opening menu...");
        menu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, v);
            popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_settings) {
                    Log.e(TAG, "Settings selected");
                    final SettingsDialog dialog = new SettingsDialog(settingsSubject, readModeSettings);
                    dialog.show(activity.getSupportFragmentManager(), "settingsDialog");
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }
}
