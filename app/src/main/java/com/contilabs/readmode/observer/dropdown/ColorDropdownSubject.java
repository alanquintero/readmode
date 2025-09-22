/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.observer.dropdown;

import androidx.annotation.NonNull;

import com.contilabs.readmode.model.ReadModeSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject (Observable) class for managing Color dropdown position and notifying
 * registered observers of changes.
 *
 * <p>Maintains a list of Observers and notifies them
 * whenever the position of the Color dropdown has changed.</p>
 *
 * @author Alan Quintero
 */
public class ColorDropdownSubject {
    private final List<ColorDropdownObserver> observers = new ArrayList<>();
    private int currentColorDropdownPosition;
    private final @NonNull ReadModeSettings readModeSettings;

    public ColorDropdownSubject(final @NonNull ReadModeSettings readModeSettings) {
        this.readModeSettings = readModeSettings;
    }

    /**
     * Registers a new observer to receive Read Mode updates.
     *
     * @param observer the observer to add
     */
    public void registerObserver(final @NonNull ColorDropdownObserver observer) {
        observers.add(observer);
    }

    /**
     * Sets the currentColorDropdownPosition and notifies all registered observers.
     *
     * @param currentColorDropdownPosition the current position in the Color dropdown
     */
    public void setCurrentColorDropdownPosition(final int currentColorDropdownPosition) {
        this.currentColorDropdownPosition = currentColorDropdownPosition;
        readModeSettings.setColorDropdownPosition(currentColorDropdownPosition);
        notifyObservers();
    }


    /**
     * Notifies all registered observers about the state change.
     */
    private void notifyObservers() {
        for (final ColorDropdownObserver observer : observers) {
            observer.onColorDropdownPositionChange(currentColorDropdownPosition);
        }
    }
}
