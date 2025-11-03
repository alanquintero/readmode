/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode.command;

import android.util.Log;

import androidx.annotation.NonNull;

import autonightmode.mx.com.alanquintero.autonightmode.manager.ReadModeManager;
import autonightmode.mx.com.alanquintero.autonightmode.model.ReadModeSettings;

/**
 * Serves as the base implementation for read mode commands, providing
 * common functionality for pausing, resuming and stopping Read Mode.
 *
 * @author Alan Quintero
 */
abstract class BaseReadModeCommand implements ReadModeCommand {

    private static final String TAG = BaseReadModeCommand.class.getSimpleName();

    private final @NonNull ReadModeManager readModeManager;
    private final @NonNull ReadModeSettings readModeSettings;

    public BaseReadModeCommand(final @NonNull ReadModeManager readModeManager, final @NonNull ReadModeSettings readModeSettings) {
        this.readModeSettings = readModeSettings;
        this.readModeManager = readModeManager;
    }

    @Override
    public void updateReadMode() {
        Log.d(TAG, "updateReadMode");
        readModeManager.updateOverlay();
    }


    @Override
    public void pauseReadMode() {
        Log.d(TAG, "pauseReadMode");
        readModeSettings.setWasReadModeOn(readModeSettings.isReadModeOn());
        readModeManager.stopReadMode();
    }

    @Override
    public void resumeReadMode() {
        Log.d(TAG, "resumeReadMode");
        if (readModeSettings.wasReadModeOn()) {
            readModeManager.startReadMode();
        }
    }

    @Override
    public void stopReadMode() {
        Log.d(TAG, "stopReadMode");
        readModeManager.stopReadMode();
    }
}
