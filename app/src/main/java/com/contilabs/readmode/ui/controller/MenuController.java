/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.contilabs.readmode.R;
import com.contilabs.readmode.model.ReadModeSettings;
import com.contilabs.readmode.observer.settings.SettingsSubject;
import com.contilabs.readmode.ui.dialog.SettingsDialog;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

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
            final PowerMenu powerMenu = new PowerMenu.Builder(context)
                    .addItem(new PowerMenuItem(context.getString(R.string.menu_settings), false, R.drawable.menu_settings))
                    .addItem(new PowerMenuItem(context.getString(R.string.menu_support), false, R.drawable.menu_support))
                    .addItem(new PowerMenuItem(context.getString(R.string.menu_feedback), false, R.drawable.menu_feedback))
                    .setMenuRadius(10f)
                    .setMenuShadow(10f)
                    .setTextColor(Color.BLACK)
                    .setTextSize(14)
                    .setBackgroundColor(Color.WHITE)
                    .setAutoDismiss(true)
                    .setOnMenuItemClickListener(new OnMenuItemClickListener() {
                        @Override
                        public void onItemClick(int position, Object item) {
                            Log.d(TAG, "Menu item position: " + position);
                            switch (position) {
                                case 0: // Settings
                                    Log.d(TAG, "Settings selected");
                                    final SettingsDialog dialog = new SettingsDialog(settingsSubject, readModeSettings);
                                    dialog.show(activity.getSupportFragmentManager(), "settingsDialog");
                                    break;
                                case 1: // Support
                                    Log.d(TAG, "Support selected");
                                    final Intent supportIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.buymeacoffee.com/alanquintero"));
                                    context.startActivity(supportIntent);
                                    break;
                                case 2: // Feedback
                                    Log.d(TAG, "Feedback selected");
                                    final Intent feedbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://forms.gle/3BcGuBZyWer7m8Gx5"));
                                    context.startActivity(feedbackIntent);
                                    break;
                                default:
                                    Log.w(TAG, "Invalid menu item selected");
                                    break;
                            }
                        }
                    }).build();

            powerMenu.showAsDropDown(v);
        });
    }
}
