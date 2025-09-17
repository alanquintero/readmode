/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Represents the settings for a specific color configuration in the application.
 *
 * <p>This class stores information about the chosen color, its hexadecimal value,
 * and its adjustable intensity and brightness levels. Instances of this class
 * are typically saved and restored from persistent storage (e.g., SharedPreferences)
 * to allow users to keep their custom preferences.</p>
 *
 * <ul>
 *   <li>{@code color} - The name or identifier of the color (e.g., "Custom", "Blue").</li>
 *   <li>{@code colorHex} - The hexadecimal representation of the color (e.g., "#FF5733").</li>
 *   <li>{@code intensity} - The user-selected intensity level for this color.</li>
 *   <li>{@code brightness} - The user-selected brightness level for this color.</li>
 * </ul>
 *
 * @author Alan Quintero
 */
public class ColorSettings {

    private final @NonNull String color;
    private final @NonNull String colorHex;
    private int intensity;
    private int brightness;

    ColorSettings(final @NonNull String color, final @NonNull String colorHex, final int intensity, final int brightness) {
        this.color = color;
        this.colorHex = colorHex;
        this.intensity = intensity;
        this.brightness = brightness;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(final int intensity) {
        this.intensity = intensity;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(final int brightness) {
        this.brightness = brightness;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ColorSettings that = (ColorSettings) o;
        return Objects.equals(color, that.color) && Objects.equals(colorHex, that.colorHex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, colorHex);
    }
}
