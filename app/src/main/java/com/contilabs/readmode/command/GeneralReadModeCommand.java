/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.command;

import android.content.Context;
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

    private final @NonNull ReadModeManager manager;


    public GeneralReadModeCommand(final @NonNull Context context) {
        super(context);
        manager = new ReadModeManager(context);
    }

    @Override
    public void startReadMode(final @NonNull ReadModeSettings readModeSettings) {
        Log.d(TAG, "startReadMode");
        manager.startReadMode(readModeSettings);
    }
}
