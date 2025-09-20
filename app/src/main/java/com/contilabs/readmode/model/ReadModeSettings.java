/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.model;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.contilabs.readmode.util.Constants;

/**
 * Represents the configuration settings for "Read Mode" in the app.
 *
 * <p>This class serves as a data container for all user-adjustable properties
 * of Read Mode, including whether the mode is enabled, screen brightness,
 * color intensity, and the selected overlay color. It can be passed around
 * to services or helpers to persist or apply these settings without having
 * to pass multiple individual parameters.</p>
 *
 * @author Alan Quintero
 */
public class ReadModeSettings {

    private boolean isReadModeOn = Constants.DEFAULT_IS_READ_MODE_ENABLED;

    private boolean wasReadModeOn = false;
    private @NonNull String customColor = Constants.DEFAULT_COLOR_WHITE;
    private int colorDropdownPosition = Constants.DEFAULT_COLOR_DROPDOWN_POSITION;
    private int colorIntensity = Constants.DEFAULT_COLOR_INTENSITY;
    private int brightness = Constants.DEFAULT_BRIGHTNESS;
    private boolean autoStartReadMode = Constants.DEFAULT_AUTO_START_READ_MODE;
    private boolean sameIntensityBrightnessForAll = Constants.DEFAULT_SAME_INTENSITY_BRIGHTNESS_FOR_ALL;
    private @Nullable Intent readModeIntent = null;

    public boolean isReadModeOn() {
        return isReadModeOn;
    }

    public void setIsReadModeOn(boolean readModeOn) {
        isReadModeOn = readModeOn;
    }

    public boolean wasReadModeOn() {
        return wasReadModeOn;
    }

    public void setWasReadModeOn(boolean wasReadModeOn) {
        this.wasReadModeOn = wasReadModeOn;
    }

    @NonNull
    public String getCustomColor() {
        return customColor;
    }

    public void setCustomColor(@NonNull String customColor) {
        this.customColor = customColor;
    }

    public int getColorDropdownPosition() {
        return colorDropdownPosition;
    }

    public void setColorDropdownPosition(int colorDropdownPosition) {
        this.colorDropdownPosition = colorDropdownPosition;
    }

    public int getColorIntensity() {
        return colorIntensity;
    }

    public void setColorIntensity(int colorIntensity) {
        this.colorIntensity = colorIntensity;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public boolean isAutoStartReadMode() {
        return autoStartReadMode;
    }

    public void setAutoStartReadMode(boolean autoStartReadMode) {
        this.autoStartReadMode = autoStartReadMode;
    }

    public boolean isSameIntensityBrightnessForAll() {
        return sameIntensityBrightnessForAll;
    }

    public void setSameIntensityBrightnessForAll(boolean sameIntensityBrightnessForAll) {
        this.sameIntensityBrightnessForAll = sameIntensityBrightnessForAll;
    }

    @Nullable
    public Intent getReadModeIntent() {
        return readModeIntent;
    }

    public void setReadModeIntent(final @Nullable Intent readModeIntent) {
        this.readModeIntent = readModeIntent;
    }
}
