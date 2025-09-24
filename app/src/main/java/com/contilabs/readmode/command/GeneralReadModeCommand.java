/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.command;

import android.util.Log;

import androidx.annotation.NonNull;

import com.contilabs.readmode.manager.ReadModeManager;
import com.contilabs.readmode.model.ReadModeSettings;

/**
 * GeneralReadModeCommand is responsible for starting the Read Mode
 * without any restrictions or checks.
 *
 * @author Alan Quintero
 */
public class GeneralReadModeCommand extends BaseReadModeCommand {

    private static final String TAG = GeneralReadModeCommand.class.getSimpleName();

    private final @NonNull ReadModeManager readModeManager;

    public GeneralReadModeCommand(final @NonNull ReadModeManager readModeManager, final @NonNull ReadModeSettings readModeSettings) {
        super(readModeManager, readModeSettings);
        this.readModeManager = readModeManager;
    }

    @Override
    public void startReadMode() {
        Log.d(TAG, "startReadMode");
        readModeManager.startReadMode();
    }
}
