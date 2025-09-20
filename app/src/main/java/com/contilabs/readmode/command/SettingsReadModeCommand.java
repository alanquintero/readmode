/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.command;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.contilabs.readmode.manager.ReadModeManager;
import com.contilabs.readmode.model.ReadModeSettings;
import com.contilabs.readmode.util.PrefsHelper;

/**
 * SettingsReadModeCommand is responsible for starting the Read Mode
 * according to the user's settings.
 *
 * @author Alan Quintero
 */
public class SettingsReadModeCommand extends BaseReadModeCommand {

    private static final String TAG = SettingsReadModeCommand.class.getSimpleName();

    private final @NonNull ReadModeManager manager;
    private final @NonNull PrefsHelper prefsHelper;

    public SettingsReadModeCommand(final @NonNull Context context, final @NonNull View rootView) {
        super(context, rootView);
        manager = new ReadModeManager(context, rootView);
        prefsHelper = new PrefsHelper(context);
    }

    @Override
    public void startReadMode(final @NonNull ReadModeSettings readModeSettings) {
        Log.d(TAG, "startReadMode");
        final boolean isAutoStartReadModeEnabled = prefsHelper.getAutoStartReadMode();
        if (isAutoStartReadModeEnabled) {
            manager.startReadMode(readModeSettings);
        }
    }
}
