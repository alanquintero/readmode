/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode.util;

import android.graphics.Color;

/**
 * A utility class that holds constant values used across the application.
 * <p>
 * This class provides predefined keys, color identifiers, and other fixed values
 * that are reused in multiple parts of the app to ensure consistency and avoid
 * duplication.
 * </p>
 *
 * @author Alan Quintero
 */
public class Constants {
    // ---------- Screen filter colors ----------
    public static final String YELLOW = "YELLOW";
    public static final String COLOR_YELLOW = "#FFF176";
    public static final String PINK = "PINK";
    public static final String COLOR_PINK = "#FFD1DC";
    public static final String GREEN = "GREEN";
    public static final String COLOR_GREEN = "#A8E6CF";
    public static final String GRAY = "GRAY";
    public static final String COLOR_GRAY = "#B0BEC5";
    public static final String WHITE = "WHITE";
    public static final String COLOR_WHITE = "#FFFFFF";
    public static final String CUSTOM_COLOR = "CUSTOM_COLOR";


    // ---------- Colors ----------
    public final static String[] COLOR_DROPDOWN_OPTIONS = new String[]{YELLOW, PINK, GREEN, GRAY, WHITE, CUSTOM_COLOR};
    // Color hex corresponding to each dropdown item
    public static final String[] COLOR_HEX_ARRAY = {COLOR_YELLOW, COLOR_PINK, COLOR_GREEN, COLOR_GRAY, COLOR_WHITE, CUSTOM_COLOR};
    // Background color to each dropdown item for better visual distinction
    public static final int[] BACKGROUND_COLOR_FOR_DROPDOWN_ITEMS = {
            Color.parseColor(COLOR_YELLOW),  // YELLOW
            Color.parseColor(COLOR_PINK),  // PINK
            Color.parseColor(COLOR_GREEN),  // GREEN
            Color.parseColor(COLOR_GRAY),  // GRAY
            Color.parseColor(COLOR_WHITE),  // WHITE
            Color.WHITE             // CUSTOM
    };
    public final static int CUSTOM_COLOR_DROPDOWN_POSITION = BACKGROUND_COLOR_FOR_DROPDOWN_ITEMS.length - 1;


    // ---------- Android Settings ----------
    public static final int NOTIFICATION_ID = 10;

    // ---------- Shared Preference ----------
    public static final String SETTINGS = "SETTINGS";
    public static final String PREF_IS_READ_MODE_ON = "IS_READ_MODE_ON";
    public static final String PREF_COLOR_DROPDOWN = "COLOR_DROPDOWN";
    public static final String PREF_COLOR = "COLOR";
    public static final String PREF_CUSTOM_COLOR = "CUSTOM_COLOR";
    public static final String PREF_COLOR_SETTINGS = "COLOR_SETTINGS";
    public static final String PREF_COLOR_INTENSITY = "COLOR_INTENSITY";
    public static final String PREF_BRIGHTNESS = "BRIGHTNESS";

    // ---------- App Theme ----------
    public enum ThemeMode {
        SYSTEM_DEFAULT(0),
        LIGHT(1),
        DARK(2);

        private final int value;

        ThemeMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static ThemeMode fromInt(int value) {
            for (ThemeMode mode : ThemeMode.values()) {
                if (mode.getValue() == value) {
                    return mode;
                }
            }
            return SYSTEM_DEFAULT; // fallback
        }

    }

    public static final String PREF_THEME = "THEME";


    // ---------- App Settings ----------

    public enum SETTING_OPTIONS {
        AUTO_READ_MODE,
        SAME_SETTINGS_FOR_ALL,
        RESET_APP_DATA
    }

    public static final String PREF_AUTO_START_READ_MODE = "AUTO_START_READ_MODE";
    public static final String PREF_SAME_INTENSITY_BRIGHTNESS_FOR_ALL = "SAME_INTENSITY_BRIGHTNESS_FOR_ALL";


    // ---------- Default values ----------
    public static final String DEFAULT_COLOR_WHITE = "WHITE";
    public static final boolean DEFAULT_IS_READ_MODE_ENABLED = false;
    public static final int DEFAULT_COLOR_DROPDOWN_POSITION = 0;
    public static final int DEFAULT_BRIGHTNESS = 50;
    public static final int DEFAULT_COLOR_INTENSITY = 50;
    public static final int DEFAULT_THEME = ThemeMode.SYSTEM_DEFAULT.value;
    public static final boolean DEFAULT_AUTO_START_READ_MODE = false;
    public static final boolean DEFAULT_SAME_INTENSITY_BRIGHTNESS_FOR_ALL = false;
    public static final String DEFAULT_CUSTOM_COLOR = "#7F7F7F"; // medium gray
    public static final String DEFAULT_COLOR_SETTINGS = "{}";
}
