/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui.spinner;

import androidx.annotation.NonNull;

/**
 * Represents a single item in a color spinner.
 * Each item has a name and a corresponding color for the icon.
 *
 * @author Alan Quintero
 */
public class ColorItem {
    private final @NonNull String name;
    private int iconColor;

    public ColorItem(final @NonNull String name, final int iconColor) {
        this.name = name;
        this.iconColor = iconColor;
    }

    public @NonNull String getName() {
        return name;
    }

    public int getIconColor() {
        return iconColor;
    }

    public void setIconColor(final int iconColor) {
        this.iconColor = iconColor;
    }
}
