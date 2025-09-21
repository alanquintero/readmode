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
 * Serves as the base implementation for read mode commands, providing
 * common functionality for pausing, resuming and stopping Read Mode.
 *
 * @author Alan Quintero
 */
abstract class BaseReadModeCommand implements ReadModeCommand {

    private static final String TAG = BaseReadModeCommand.class.getSimpleName();

    private final @NonNull ReadModeManager manager;

    public BaseReadModeCommand(final @NonNull Context context, final @NonNull View rootView) {
        manager = new ReadModeManager(context, rootView);
    }

    @Override
    public void pauseReadMode(final @NonNull ReadModeSettings readModeSettings) {
        Log.d(TAG, "pauseReadMode");
        readModeSettings.setWasReadModeOn(readModeSettings.isReadModeOn());
        manager.stopReadMode(readModeSettings);
    }

    @Override
    public void resumeReadMode(final @NonNull ReadModeSettings readModeSettings) {
        Log.d(TAG, "resumeReadMode");
        if (readModeSettings.wasReadModeOn()) {
            manager.startReadMode(readModeSettings);
        }
    }

    @Override
    public void stopReadMode(final @NonNull ReadModeSettings readModeSettings) {
        Log.d(TAG, "stopReadMode");
        manager.stopReadMode(readModeSettings);
    }
}
