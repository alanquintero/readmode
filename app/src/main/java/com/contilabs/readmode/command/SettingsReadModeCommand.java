/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.command;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.contilabs.readmode.manager.ReadModeManager;
import com.contilabs.readmode.model.ReadModeSettings;
import com.contilabs.readmode.observer.readmode.ReadModeSubject;

/**
 * SettingsReadModeCommand is responsible for starting the Read Mode
 * according to the user's settings.
 *
 * @author Alan Quintero
 */
public class SettingsReadModeCommand extends BaseReadModeCommand {

    private static final String TAG = SettingsReadModeCommand.class.getSimpleName();

    private final @NonNull ReadModeManager manager;
    private final @NonNull ReadModeSettings readModeSettings;

    public SettingsReadModeCommand(final @NonNull Context context, final @NonNull ReadModeSubject readModeSubject, final @NonNull ReadModeSettings readModeSettings) {
        super(context, readModeSubject, readModeSettings);
        this.readModeSettings = readModeSettings;
        manager = new ReadModeManager(context, readModeSubject, readModeSettings);
    }

    @Override
    public void startReadMode() {
        Log.d(TAG, "startReadMode");
        if (readModeSettings.isAutoStartReadMode() || readModeSettings.isReadModeOn()) {
            manager.startReadMode();
        }
    }
}
