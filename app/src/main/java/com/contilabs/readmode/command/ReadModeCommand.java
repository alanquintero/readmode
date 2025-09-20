/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.command;

import androidx.annotation.NonNull;

import com.contilabs.readmode.model.ReadModeSettings;

/**
 * ReadModeCommand defines the contract for starting and stopping "Read Mode"
 * in the application. Implementations can define different behaviors or
 * restrictions for activating or deactivating Read Mode.
 *
 * @author Alan Quintero
 */
interface ReadModeCommand {
    void startReadMode(final @NonNull ReadModeSettings readModeSettings);

    void pauseReadMode(final @NonNull ReadModeSettings readModeSettings);

    void resumeReadMode(final @NonNull ReadModeSettings readModeSettings);

    void stopReadMode(final @NonNull ReadModeSettings readModeSettings);
}
