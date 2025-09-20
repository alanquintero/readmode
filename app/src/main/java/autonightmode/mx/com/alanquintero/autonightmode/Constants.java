/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode;

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
    // Screen filter colors
    public static final String COLOR_NONE = "none";
    public static final String SOFT_BEIGE = "SOFT_BEIGE";
    public static final String COLOR_SOFT_BEIGE = "#F5F5DC";
    public static final String LIGHT_GRAY = "LIGHT_GRAY";
    public static final String COLOR_LIGHT_GRAY = "#E6E6E6";
    public static final String PALE_YELLOW = "PALE_YELLOW";
    public static final String COLOR_PALE_YELLOW = "#FFFFD2";
    public static final String WARM_SEPIA = "WARM_SEPIA";
    public static final String COLOR_WARM_SEPIA = "#F4ECD3";
    public static final String SOFT_BLUE = "SOFT_BLUE";
    public static final String COLOR_SOFT_BLUE = "#DCEBFF";
    public static final String CUSTOM_COLOR = "CUSTOM_COLOR";

    public final static String[] COLOR_DROPDOWN_OPTIONS = new String[]{Constants.COLOR_NONE, Constants.SOFT_BEIGE, Constants.LIGHT_GRAY,
            Constants.PALE_YELLOW, Constants.WARM_SEPIA, Constants.SOFT_BLUE, Constants.CUSTOM_COLOR};

    // Settings
    public static final int NOTIFICATION_ID = 10;

    // Shared Preference
    public static final String SETTINGS = "SETTINGS";
    public static final String PREF_IS_READ_MODE_ON = "IS_READ_MODE_ON";
    public static final String PREF_COLOR_DROPDOWN = "COLOR_DROPDOWN";
    public static final String PREF_COLOR = "COLOR";
    public static final String PREF_CUSTOM_COLOR = "CUSTOM_COLOR";
    public static final String PREF_COLOR_SETTINGS = "COLOR_SETTINGS";
    public static final String PREF_COLOR_INTENSITY = "COLOR_INTENSITY";
    public static final String PREF_BRIGHTNESS = "BRIGHTNESS";
    public static final String PREF_SAME_INTENSITY_BRIGHTNESS_FOR_ALL = "SAME_INTENSITY_BRIGHTNESS_FOR_ALL";
    public static final String PREF_AUTO_START_READ_MODE = "AUTO_START_READ_MODE";


    // Default values
    public static final String DEFAULT_COLOR_WHITE = "WHITE";
    public static final boolean DEFAULT_IS_READ_MODE_ENABLED = false;
    public static final int DEFAULT_COLOR_DROPDOWN_POSITION = 0;
    public static final int DEFAULT_BRIGHTNESS = 50;
    public static final int DEFAULT_COLOR_INTENSITY = 50;
    public static final boolean DEFAULT_SAME_INTENSITY_BRIGHTNESS_FOR_ALL = false;
    public static final boolean DEFAULT_AUTO_START_READ_MODE = true;

    // Others
    public static final String COLOR_HEX_FORMAT = "#%02X%02X%02X";
}
