/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.observer.customcolor;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject (Observable) class for managing Custom Color and notifying
 * registered observers of changes.
 *
 * <p>Maintains a list of Observers and notifies them
 * whenever the Custom Color has changed.</p>
 *
 * @author Alan Quintero
 */
public class CustomColorSubject {
    private final List<CustomColorObserver> observers = new ArrayList<>();
    private String customColor;

    /**
     * Registers a new observer to receive Read Mode updates.
     *
     * @param observer the observer to add
     */
    public void registerObserver(final @NonNull CustomColorObserver observer) {
        observers.add(observer);
    }

    /**
     * Sets the Custom Color and notifies all registered observers.
     *
     * @param customColor the current position in the Color dropdown
     */
    public void setCustomColor(final @NonNull String customColor) {
        this.customColor = customColor;
        notifyObservers();
    }


    /**
     * Notifies all registered observers about the state change.
     */
    private void notifyObservers() {
        for (final CustomColorObserver observer : observers) {
            observer.onCustomColorChange(customColor);
        }
    }
}
