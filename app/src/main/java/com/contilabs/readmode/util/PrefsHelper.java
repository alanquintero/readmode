/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.contilabs.readmode.model.ColorSettings;
import com.contilabs.readmode.model.ReadModeSettings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * PrefsHelper is a utility class that simplifies access to the app's SharedPreferences.
 *
 * @author Alan Quintero
 */
public class PrefsHelper {

    private static final String TAG = PrefsHelper.class.getSimpleName();

    private final @NonNull SharedPreferences sharedPreferences;
    private final @NonNull Gson gson;

    private Map<String, ColorSettings> prefColorSettingsMap = new HashMap<>();

    public PrefsHelper(final @NonNull Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    /**
     * Loads color settings map.
     */
    public void initPrefColorSettingsMap() {
        final String json = sharedPreferences.getString(Constants.PREF_COLOR_SETTINGS, "{}");
        final Type type = new TypeToken<Map<String, ColorSettings>>() {
        }.getType();
        prefColorSettingsMap = gson.fromJson(json, type);
        if (prefColorSettingsMap.isEmpty()) {
            Log.d(TAG, "prefColorSettingsMap is empty, initializing map...");
            // There is no pref saved, create the map with default settings
            prefColorSettingsMap.put(Constants.COLOR_NONE, new ColorSettings(Constants.COLOR_NONE, Constants.COLOR_NONE, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            prefColorSettingsMap.put(Constants.SOFT_BEIGE, new ColorSettings(Constants.SOFT_BEIGE, Constants.COLOR_SOFT_BEIGE, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            prefColorSettingsMap.put(Constants.LIGHT_GRAY, new ColorSettings(Constants.LIGHT_GRAY, Constants.COLOR_LIGHT_GRAY, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            prefColorSettingsMap.put(Constants.PALE_YELLOW, new ColorSettings(Constants.PALE_YELLOW, Constants.COLOR_PALE_YELLOW, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            prefColorSettingsMap.put(Constants.WARM_SEPIA, new ColorSettings(Constants.WARM_SEPIA, Constants.COLOR_WARM_SEPIA, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            prefColorSettingsMap.put(Constants.SOFT_BLUE, new ColorSettings(Constants.SOFT_BLUE, Constants.COLOR_SOFT_BLUE, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            prefColorSettingsMap.put(Constants.CUSTOM_COLOR, new ColorSettings(Constants.CUSTOM_COLOR, Constants.CUSTOM_COLOR, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
        }
    }

    public boolean getIsReadModeOn() {
        return sharedPreferences.getBoolean(Constants.PREF_IS_READ_MODE_ON, Constants.DEFAULT_IS_READ_MODE_ENABLED);
    }

    public int getColorDropdownPosition() {
        return sharedPreferences.getInt(Constants.PREF_COLOR_DROPDOWN, Constants.DEFAULT_COLOR_DROPDOWN_POSITION);
    }

    public String getCustomColor() {
        return sharedPreferences.getString(Constants.PREF_CUSTOM_COLOR, Constants.DEFAULT_COLOR_WHITE);
    }

    public int getColorIntensity() {
        return sharedPreferences.getInt(Constants.PREF_COLOR_INTENSITY, Constants.DEFAULT_COLOR_INTENSITY);
    }

    public int getBrightness() {
        return sharedPreferences.getInt(Constants.PREF_BRIGHTNESS, Constants.DEFAULT_BRIGHTNESS);
    }

    public boolean getSameIntensityBrightnessForAll() {
        return sharedPreferences.getBoolean(Constants.PREF_SAME_INTENSITY_BRIGHTNESS_FOR_ALL, Constants.DEFAULT_SAME_INTENSITY_BRIGHTNESS_FOR_ALL);
    }

    public boolean getAutoStartReadMode() {
        return sharedPreferences.getBoolean(Constants.PREF_AUTO_START_READ_MODE, Constants.DEFAULT_AUTO_START_READ_MODE);
    }

    public ColorSettings getColorSettings(final int currentColorDropdownPosition) {
        return prefColorSettingsMap.get(Constants.COLOR_DROPDOWN_OPTIONS[currentColorDropdownPosition]);
    }

    /**
     * Saves an String value to {@link SharedPreferences} under the specified key.
     */
    public void saveProperty(final @NonNull String property, final @NonNull String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(property, value);
        editor.apply();
    }

    /**
     * Saves an boolean value to {@link SharedPreferences} under the specified key.
     */
    public void saveProperty(final @NonNull String property, final boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(property, value);
        editor.apply();
    }

    /**
     * Saves an integer value to {@link SharedPreferences} under the specified key.
     */
    public void saveProperty(final @NonNull String property, final int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(property, value);
        editor.apply();
    }

    /**
     * Saves the current brightness and color intensity settings for the selected color
     * into the {@link SharedPreferences}. Updates the ColorSettingsMap
     * with the latest values and serializes it as JSON for persistence.
     */
    public void saveColorSettingsProperty(final @NonNull ReadModeSettings readModeSettings) {
        // update the brightness and color intensity values for the selected color
        final String selectedColor = Constants.COLOR_DROPDOWN_OPTIONS[readModeSettings.getColorDropdownPosition()];
        final ColorSettings colorSettings = prefColorSettingsMap.get(selectedColor);
        if (colorSettings != null) {
            colorSettings.setBrightness(readModeSettings.getBrightness());
            colorSettings.setIntensity(readModeSettings.getColorIntensity());
            prefColorSettingsMap.put(selectedColor, colorSettings);
        } else {
            Log.d(TAG, "colorSettings not found in prefColorSettingsMap!");
        }

        final String json = gson.toJson(prefColorSettingsMap);

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREF_COLOR_SETTINGS, json);
        editor.apply();
    }
}
