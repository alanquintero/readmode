/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.observer.readmode;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject (Observable) class for managing Read Mode state and notifying
 * registered observers of changes.
 *
 * <p>Maintains a list of Observers and notifies them
 * whenever the Read Mode is started or stopped.</p>
 *
 * @author Alan Quintero
 */
public class ReadModeSubject {

    private static final String TAG = ReadModeSubject.class.getSimpleName();

    private final List<ReadModeObserver> observers = new ArrayList<>();
    private boolean isReadModeOn;

    /**
     * Registers a new observer to receive Read Mode updates.
     *
     * @param observer the observer to add
     */
    public void registerObserver(final @NonNull ReadModeObserver observer) {
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
     * Sets the Read Mode state and notifies all registered observers.
     *
     * @param isReadModeOn true to start Read Mode, false to stop
     */
    public void setReadModeOn(final boolean isReadModeOn) {
        Log.d(TAG, "setReadModeOn: " + isReadModeOn);
        this.isReadModeOn = isReadModeOn;
        notifyObservers();
    }

    /**
     * Notifies all registered observers about the state change.
     */
    private void notifyObservers() {
        Log.d(TAG, "notifyObservers");
        for (ReadModeObserver observer : observers) {
            observer.onReadModeChanged(isReadModeOn);
        }
    }
}
