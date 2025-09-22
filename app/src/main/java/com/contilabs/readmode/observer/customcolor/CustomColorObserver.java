/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.observer.customcolor;

import androidx.annotation.NonNull;

/**
 * Observer interface for classes that want to be notified about changes
 * when the Custom Color has changed.
 *
 * <p>Implement this interface in any class that needs to react when
 * the Custom Color has changed. </p>
 *
 * @author Alan Quintero
 */
public interface CustomColorObserver {
    /**
     * Called when the Custom Color has changed.
     */
    void onCustomColorChange(final @NonNull String customColor);
}
