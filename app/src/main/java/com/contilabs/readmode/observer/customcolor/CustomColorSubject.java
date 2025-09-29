/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.observer.customcolor;

import android.util.Log;

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

    private static final String TAG = CustomColorSubject.class.getSimpleName();

    private final List<CustomColorObserver> observers = new ArrayList<>();
    private String customColor;

    /**
     * Registers a new observer to receive Custom Color updates.
     *
     * @param observer the observer to add
     */
    public void registerObserver(final @NonNull CustomColorObserver observer) {
        Log.d(TAG, "registerObserver");
        observers.add(observer);
    }

    /**
     * Unregister all observers.
     */
    public void unregisterAllObservers() {
        Log.d(TAG, "unregisterAllObservers");
        observers.clear();
    }

    /**
     * Sets the Custom Color and notifies all registered observers.
     *
     * @param customColor the current selected Custom Color
     */
    public void setCustomColor(final @NonNull String customColor) {
        Log.d(TAG, "setCustomColor: " + customColor);
        this.customColor = customColor;
        notifyObservers();
    }


    /**
     * Notifies all registered observers about the state change.
     */
    private void notifyObservers() {
        Log.d(TAG, "notifyObservers");
        for (final CustomColorObserver observer : observers) {
            observer.onCustomColorChange(customColor);
        }
    }
}
