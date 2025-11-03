/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode.observer.dropdown;

/**
 * Observer interface for classes that want to be notified about changes
 * when the Color dropdown position has changed.
 *
 * <p>Implement this interface in any class that needs to react when
 * the Color dropdown position has changed. </p>
 *
 * @author Alan Quintero
 */
public interface ColorDropdownObserver {
    /**
     * Called when the Color Dropdown position has changed.
     */
    void onColorDropdownPositionChange(final int currentColorDropdownPosition);
}
