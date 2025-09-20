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

/**
 * GeneralReadModeCommand is responsible for starting the Read Mode
 * without any restrictions or checks.
 *
 * @author Alan Quintero
 */
public class GeneralReadModeCommand extends BaseReadModeCommand {

    private static final String TAG = GeneralReadModeCommand.class.getSimpleName();

    private final @NonNull ReadModeManager manager;


    public GeneralReadModeCommand(final @NonNull Context context, final @NonNull View rootView) {
        super(context, rootView);
        manager = new ReadModeManager(context, rootView);
    }

    @Override
    public void startReadMode(final @NonNull ReadModeSettings readModeSettings) {
        Log.d(TAG, "startReadMode");
        manager.startReadMode(readModeSettings);
    }
}
