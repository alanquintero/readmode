/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.util;

import android.graphics.Color;

import androidx.annotation.ColorInt;
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
     * Returns the Hex color for the given ColorInt
     *
     * @param color the ColorInt
     * @return the Hex color
     */
    public static @NonNull String getHexColor(final @ColorInt int color) {
        return String.format("#%08X", color);
    }

    /**
     * Converts any color into a color compatible with MaterialButton background.
     * MaterialButton may ignore very transparent colors, so this ensures proper alpha blending.
     *
     * @param colorHex Any Hex color.
     * @return A "safe" color that works with MaterialButton.
     */
    public static int toButtonCompatibleColor(final @NonNull String colorHex) {
        final int color = Color.parseColor(colorHex);
        // If the color is too transparent, increase alpha to at least 30%
        int alpha = Color.alpha(color);
        if (alpha < 77) { // 30% of 255
            alpha = 77;
        }
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    /**
     * Adjusts a base color by applying intensity (darker values) and brightness (overall dimming).
     *
     * @param baseColor  the selected color or a custom color
     * @param intensity  amount to subtract from each channel (0 = no change)
     * @param brightness brightness factor from 0 (dark) to 1 (original)
     * @return the adjusted color as an int
     */
    public static int adjustColor(final int baseColor, final int intensity, final int brightness) {
        final int red = Math.max(0, Color.red(baseColor) - intensity);
        final int green = Math.max(0, Color.green(baseColor) - intensity);
        final int blue = Math.max(0, Color.blue(baseColor) - intensity);
        final int alpha = 150 - brightness;

        return Color.argb(alpha, red, green, blue);
    }

    /**
     * Returns true is the given color is a very light color.
     *
     * @param color the given color
     * @return true if given color is very light, false otherwise
     */
    public static boolean isVeryLightColor(final int color) {
        // Calculate brightness
        final int r = Color.red(color);
        final int g = Color.green(color);
        final int b = Color.blue(color);
        final double perceivedBrightness = (0.299 * r + 0.587 * g + 0.114 * b);

        return perceivedBrightness > 200;
    }

    /**
     * Determines whether a given color is considered dark.
     */
    public static boolean isColorDark(int color) {
        // Convert RGB components to values between 0 and 1
        double r = Color.red(color) / 255.0;
        double g = Color.green(color) / 255.0;
        double b = Color.blue(color) / 255.0;

        // Apply gamma correction
        r = r <= 0.03928 ? r / 12.92 : Math.pow((r + 0.055) / 1.055, 2.4);
        g = g <= 0.03928 ? g / 12.92 : Math.pow((g + 0.055) / 1.055, 2.4);
        b = b <= 0.03928 ? b / 12.92 : Math.pow((b + 0.055) / 1.055, 2.4);

        // Calculate relative luminance
        double luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b;

        // If luminance is less than 0.5 → dark color
        return luminance < 0.4;
    }
}
