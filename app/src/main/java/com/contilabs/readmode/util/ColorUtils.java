/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.util;

import android.graphics.Color;

import androidx.annotation.NonNull;

/**
 * Utility class for applying color transformations such as intensity reduction and brightness adjustment.
 * <p>
 * This class provides methods to modify colors based on user preferences
 * or UI requirements, ensuring consistency across components like previews,
 * backgrounds, or overlays.
 * </p>
 *
 * @author Alan Quintero
 */
public class ColorUtils {

    /**
     * Adjusts a base color by applying intensity (darker values) and brightness (overall dimming).
     *
     * @param baseColor     the selected color or a custom color
     * @param intensity     amount to subtract from each channel (0 = no change)
     * @param brightness    brightness factor from 0 (dark) to 1 (original)
     * @return the adjusted color as an int
     */
    public static int adjustColor(final int baseColor, final int intensity, final int brightness) {
        int red = Math.max(0, Color.red(baseColor) - intensity);
        int green = Math.max(0, Color.green(baseColor) - intensity);
        int blue = Math.max(0, Color.blue(baseColor) - intensity);
        int alpha = 150 - brightness;

        return  Color.argb(alpha, red, green, blue);
    }
}
