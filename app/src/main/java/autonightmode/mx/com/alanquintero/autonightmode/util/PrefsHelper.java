/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import autonightmode.mx.com.alanquintero.autonightmode.model.ColorSettings;
import autonightmode.mx.com.alanquintero.autonightmode.model.ReadModeSettings;
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

    private static PrefsHelper instance = null;

    private final @NonNull SharedPreferences sharedPreferences;
    private final @NonNull Gson gson;

    private Map<String, ColorSettings> prefColorSettingsMap = new HashMap<>();


    public static PrefsHelper init(final @NonNull Context context) {
        if (instance == null) {
            instance = new PrefsHelper(context);
        }
        return instance;
    }

    private PrefsHelper(final @NonNull Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    /**
     * IMPORTANT: use it for testing ONLY
     */
    @VisibleForTesting
    static void cleanUp() {
        instance = null;
    }

    /**
     * Loads color settings map.
     */
    public void initPrefColorSettingsMap() {
        final String json = sharedPreferences.getString(Constants.PREF_COLOR_SETTINGS, Constants.DEFAULT_COLOR_SETTINGS);
        final Type type = new TypeToken<Map<String, ColorSettings>>() {
        }.getType();
        prefColorSettingsMap = gson.fromJson(json, type);
        if (prefColorSettingsMap.isEmpty()) {
            Log.d(TAG, "prefColorSettingsMap is empty, initializing map...");
            // There is no pref saved, create the map with default settings
            prefColorSettingsMap.put(Constants.SOFT_BEIGE, new ColorSettings(Constants.SOFT_BEIGE, Constants.COLOR_SOFT_BEIGE, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            prefColorSettingsMap.put(Constants.LIGHT_GRAY, new ColorSettings(Constants.LIGHT_GRAY, Constants.COLOR_LIGHT_GRAY, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            prefColorSettingsMap.put(Constants.PALE_YELLOW, new ColorSettings(Constants.PALE_YELLOW, Constants.COLOR_PALE_YELLOW, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            prefColorSettingsMap.put(Constants.WARM_SEPIA, new ColorSettings(Constants.WARM_SEPIA, Constants.COLOR_WARM_SEPIA, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            prefColorSettingsMap.put(Constants.SOFT_BLUE, new ColorSettings(Constants.SOFT_BLUE, Constants.COLOR_SOFT_BLUE, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            prefColorSettingsMap.put(Constants.CUSTOM_COLOR, new ColorSettings(Constants.CUSTOM_COLOR, Constants.CUSTOM_COLOR, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
        } else {
            Log.d(TAG, "prefColorSettingsMap loaded from shared preferences!");
        }
    }

    public boolean isReadModeOn() {
        return sharedPreferences.getBoolean(Constants.PREF_IS_READ_MODE_ON, Constants.DEFAULT_IS_READ_MODE_ENABLED);
    }

    public int getColorDropdownPosition() {
        return sharedPreferences.getInt(Constants.PREF_COLOR_DROPDOWN, Constants.DEFAULT_COLOR_DROPDOWN_POSITION);
    }

    public String getCustomColor() {
        return sharedPreferences.getString(Constants.PREF_CUSTOM_COLOR, Constants.DEFAULT_CUSTOM_COLOR);
    }

    public int getColorIntensity() {
        return sharedPreferences.getInt(Constants.PREF_COLOR_INTENSITY, Constants.DEFAULT_COLOR_INTENSITY);
    }

    public int getBrightness() {
        return sharedPreferences.getInt(Constants.PREF_BRIGHTNESS, Constants.DEFAULT_BRIGHTNESS);
    }

    public boolean shouldUseSameIntensityBrightnessForAll() {
        return sharedPreferences.getBoolean(Constants.PREF_SAME_INTENSITY_BRIGHTNESS_FOR_ALL, Constants.DEFAULT_SAME_INTENSITY_BRIGHTNESS_FOR_ALL);
    }

    public boolean getAutoStartReadMode() {
        return sharedPreferences.getBoolean(Constants.PREF_AUTO_START_READ_MODE, Constants.DEFAULT_AUTO_START_READ_MODE);
    }

    public @NonNull Constants.ThemeMode getTheme() {
        return Constants.ThemeMode.fromInt(sharedPreferences.getInt(Constants.PREF_THEME, Constants.DEFAULT_THEME));
    }

    public @NonNull String getColor() {
        return sharedPreferences.getString(Constants.PREF_COLOR, Constants.DEFAULT_COLOR_WHITE);
    }

    public @Nullable ColorSettings getColorSettings(final int currentColorDropdownPosition) {
        return prefColorSettingsMap.get(Constants.COLOR_DROPDOWN_OPTIONS[currentColorDropdownPosition]);
    }


    /**
     * Saves an String value to {@link SharedPreferences} under the specified key.
     */
    public void saveProperty(final @NonNull String property, final @NonNull String value) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(property, value);
        editor.apply();
    }

    /**
     * Saves an boolean value to {@link SharedPreferences} under the specified key.
     */
    public void saveProperty(final @NonNull String property, final boolean value) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(property, value);
        editor.apply();
    }

    /**
     * Saves an integer value to {@link SharedPreferences} under the specified key.
     */
    public void saveProperty(final @NonNull String property, final int value) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(property, value);
        editor.apply();
    }

    /**
     * Tries to save (based on settings) the current brightness and color intensity settings
     * for the selected color into the {@link SharedPreferences}. Updates the ColorSettingsMap
     * with the latest values and serializes it as JSON for persistence.
     */
    public void tryToSaveColorSettingsProperty(final @NonNull ReadModeSettings readModeSettings) {
        if (readModeSettings.shouldUseSameIntensityBrightnessForAll()) {
            Log.w(TAG, "Not saving values for each color as all colors use the same values!");
            // Only save the properties for each color when setting is disabled
            return;
        }
        // update the brightness and color intensity values for the selected color
        final String selectedColor = Constants.COLOR_DROPDOWN_OPTIONS[readModeSettings.getColorDropdownPosition()];
        final ColorSettings colorSettings = prefColorSettingsMap.get(selectedColor);
        if (colorSettings != null) {
            colorSettings.setColorIntensity(readModeSettings.getColorIntensity());
            colorSettings.setBrightness(readModeSettings.getBrightness());
            prefColorSettingsMap.put(selectedColor, colorSettings);

            final String json = gson.toJson(prefColorSettingsMap);

            final SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.PREF_COLOR_SETTINGS, json);
            editor.apply();
        } else {
            Log.d(TAG, "colorSettings not found in prefColorSettingsMap!");
        }
    }

    /**
     * Reset all shared preferences to default values
     */
    public void resetAppData() {
        saveProperty(Constants.PREF_IS_READ_MODE_ON, Constants.DEFAULT_IS_READ_MODE_ENABLED);
        saveProperty(Constants.PREF_COLOR_DROPDOWN, Constants.DEFAULT_COLOR_DROPDOWN_POSITION);
        saveProperty(Constants.PREF_COLOR, Constants.DEFAULT_COLOR_WHITE);
        saveProperty(Constants.PREF_CUSTOM_COLOR, Constants.DEFAULT_CUSTOM_COLOR);
        saveProperty(Constants.PREF_COLOR_SETTINGS, Constants.DEFAULT_COLOR_SETTINGS);
        saveProperty(Constants.PREF_COLOR_INTENSITY, Constants.DEFAULT_COLOR_INTENSITY);
        saveProperty(Constants.PREF_BRIGHTNESS, Constants.DEFAULT_BRIGHTNESS);
        saveProperty(Constants.PREF_THEME, Constants.DEFAULT_THEME);
        saveProperty(Constants.PREF_AUTO_START_READ_MODE, Constants.DEFAULT_AUTO_START_READ_MODE);
        saveProperty(Constants.PREF_SAME_INTENSITY_BRIGHTNESS_FOR_ALL, Constants.DEFAULT_SAME_INTENSITY_BRIGHTNESS_FOR_ALL);
    }
}
