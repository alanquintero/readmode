/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.command;

/**
 * ReadModeCommand defines the contract for starting and stopping "Read Mode"
 * in the application. Implementations can define different behaviors or
 * restrictions for activating or deactivating Read Mode.
 *
 * @author Alan Quintero
 */
public interface ReadModeCommand {
    void startReadMode();

    void updateReadMode();

    void pauseReadMode();

    void resumeReadMode();

    void stopReadMode();
}
