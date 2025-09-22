/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.observer.readmode;

/**
 * Observer interface for classes that want to be notified about changes
 * in the Read Mode state (on/off).
 *
 * <p>Implement this interface in any class that needs to react when
 * the Read Mode is started or stopped, such as UI controllers or
 * other services.</p>
 *
 * @author Alan Quintero
 */
public interface ReadModeObserver {
    /**
     * Called when the Read Mode state has changed.
     */
    void onReadModeChanged(boolean isReadModeOn);
}
