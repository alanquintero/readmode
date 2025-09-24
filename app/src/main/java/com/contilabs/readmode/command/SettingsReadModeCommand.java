/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.command;

import android.util.Log;

import androidx.annotation.NonNull;

import com.contilabs.readmode.manager.ReadModeManager;
import com.contilabs.readmode.model.ReadModeSettings;

/**
 * SettingsReadModeCommand is responsible for starting the Read Mode
 * according to the user's settings.
 *
 * @author Alan Quintero
 */
public class SettingsReadModeCommand extends BaseReadModeCommand {

    private static final String TAG = SettingsReadModeCommand.class.getSimpleName();

    private final @NonNull ReadModeManager readModeManager;
    private final @NonNull ReadModeSettings readModeSettings;

    public SettingsReadModeCommand(final @NonNull ReadModeManager readModeManager, final @NonNull ReadModeSettings readModeSettings) {
        super(readModeManager, readModeSettings);
        this.readModeSettings = readModeSettings;
        this.readModeManager = readModeManager;
    }

    @Override
    public void startReadMode() {
        Log.d(TAG, "startReadMode");
        if (readModeSettings.isAutoStartReadMode() || readModeSettings.isReadModeOn()) {
            readModeManager.startReadMode();
        }
    }
}
