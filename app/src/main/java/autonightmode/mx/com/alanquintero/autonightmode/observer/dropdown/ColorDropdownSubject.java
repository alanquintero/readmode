/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode.observer.dropdown;

import android.util.Log;

import androidx.annotation.NonNull;

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

    private static final String TAG = ColorDropdownSubject.class.getSimpleName();

    private final List<ColorDropdownObserver> observers = new ArrayList<>();
    private int currentColorDropdownPosition;

    /**
     * Registers a new observer to receive Color Dropdown position updates.
     *
     * @param observer the observer to add
     */
    public void registerObserver(final @NonNull ColorDropdownObserver observer) {
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
     * Sets the currentColorDropdownPosition and notifies all registered observers.
     *
     * @param currentColorDropdownPosition the current position in the Color dropdown
     */
    public void setCurrentColorDropdownPosition(final int currentColorDropdownPosition) {
        Log.d(TAG, "setCurrentColorDropdownPosition: " + currentColorDropdownPosition);
        this.currentColorDropdownPosition = currentColorDropdownPosition;
        notifyObservers();
    }


    /**
     * Notifies all registered observers about the state change.
     */
    private void notifyObservers() {
        Log.d(TAG, "notifyObservers");
        for (final ColorDropdownObserver observer : observers) {
            observer.onColorDropdownPositionChange(currentColorDropdownPosition);
        }
    }
}
