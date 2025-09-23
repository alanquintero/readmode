/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.observer.settings;

import com.contilabs.readmode.util.Constants;

/**
 * Observer interface for classes that want to be notified about changes in Settings.
 *
 * <p>Implement this interface in any class that needs to react when Settings changed. </p>
 *
 * @author Alan Quintero
 */
public interface SettingsObserver {
    /**
     * Called when Settings has changed.
     */
    void onSettingsChanged(Constants.SETTING_OPTIONS setting);
}
