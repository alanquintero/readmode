/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.observer.settings;

import androidx.annotation.NonNull;

import com.contilabs.readmode.observer.readmode.ReadModeObserver;
import com.contilabs.readmode.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject (Observable) class for managing Settings changes and notifying
 * registered observers of changes.
 *
 * <p>Maintains a list of Observers and notifies them
 * whenever something on Settings has changed.</p>
 *
 * @author Alan Quintero
 */
public class SettingsSubject {
    private final List<SettingsObserver> observers = new ArrayList<>();

    private Constants.SETTING_OPTIONS setting;

    /**
     * Registers a new observer to receive Settings updates.
     *
     * @param observer the observer to add
     */
    public void registerObserver(final @NonNull SettingsObserver observer) {
        observers.add(observer);
    }

    /**
     * Notifies all registered observers.
     */
    public void onSettingsChanged(Constants.SETTING_OPTIONS setting) {
        this.setting = setting;
        notifyObservers();
    }


    /**
     * Notifies all registered observers about the state change.
     */
    private void notifyObservers() {
        for (SettingsObserver observer : observers) {
            observer.onSettingsChanged(setting);
        }
    }
}
