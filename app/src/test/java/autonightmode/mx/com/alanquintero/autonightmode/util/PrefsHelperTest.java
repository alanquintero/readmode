/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import autonightmode.mx.com.alanquintero.autonightmode.BaseTest;
import autonightmode.mx.com.alanquintero.autonightmode.model.ColorSettings;
import autonightmode.mx.com.alanquintero.autonightmode.model.ReadModeSettings;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PrefsHelperTest extends BaseTest {

    @Mock
    private Context context;
    @Mock
    private SharedPreferences sharedPreferences;

    private AutoCloseable mocks;
    private PrefsHelper prefsHelper;

    @BeforeEach
    public void setup() {
        // Initialize mocks
        mocks = MockitoAnnotations.openMocks(this);
        doReturn(sharedPreferences).when(context).getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
        prefsHelper = PrefsHelper.init(context);
    }

    @AfterEach
    public void tearDown() throws Exception {
        mocks.close();
        PrefsHelper.cleanUp();
    }

    @Test
    public void initPrefColorSettingsMap() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            // Map is empty
            for (int i = 0; i < Constants.COLOR_DROPDOWN_OPTIONS.length; i++) {
                assertNull(prefsHelper.getColorSettings(i));
            }
            doReturn("{}").when(sharedPreferences).getString(Constants.PREF_COLOR_SETTINGS, Constants.DEFAULT_COLOR_SETTINGS);

            // When
            prefsHelper.initPrefColorSettingsMap();

            // Then
            for (int i = 0; i < Constants.COLOR_DROPDOWN_OPTIONS.length; i++) {
                assertNotNull(prefsHelper.getColorSettings(i));
            }
        }
    }

    @Test
    public void isReadModeOn_true() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            Mockito.lenient()
                    .when(sharedPreferences.getBoolean(Constants.PREF_IS_READ_MODE_ON, Constants.DEFAULT_IS_READ_MODE_ENABLED))
                    .thenReturn(true);

            // When
            final boolean isReadModeOn = prefsHelper.isReadModeOn();

            // Then
            assertTrue(isReadModeOn);
        }
    }

    @Test
    public void isReadModeOn_false() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            Mockito.lenient()
                    .when(sharedPreferences.getBoolean(Constants.PREF_IS_READ_MODE_ON, Constants.DEFAULT_IS_READ_MODE_ENABLED))
                    .thenReturn(false);

            // When
            final boolean isReadModeOn = prefsHelper.isReadModeOn();

            // Then
            assertFalse(isReadModeOn);
        }
    }

    @Test
    public void isReadModeOn_defaultValue() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            Mockito.lenient()
                    .when(sharedPreferences.getBoolean(Constants.PREF_IS_READ_MODE_ON, Constants.DEFAULT_IS_READ_MODE_ENABLED))
                    .thenReturn(Constants.DEFAULT_IS_READ_MODE_ENABLED);

            // When
            final boolean isReadModeOn = prefsHelper.isReadModeOn();

            // Then
            assertEquals(Constants.DEFAULT_IS_READ_MODE_ENABLED, isReadModeOn);
        }
    }

    @Test
    public void getColorDropdownPosition() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            final int expectedColorDropdownPosition = 3;
            Mockito.lenient()
                    .when(sharedPreferences.getInt(Constants.PREF_COLOR_DROPDOWN, Constants.DEFAULT_COLOR_DROPDOWN_POSITION))
                    .thenReturn(expectedColorDropdownPosition);

            // When
            final int colorDropdownPosition = prefsHelper.getColorDropdownPosition();

            // Then
            assertEquals(expectedColorDropdownPosition, colorDropdownPosition);
        }
    }

    @Test
    public void getColorDropdownPosition_defaultValue() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            Mockito.lenient()
                    .when(sharedPreferences.getInt(Constants.PREF_COLOR_DROPDOWN, Constants.DEFAULT_COLOR_DROPDOWN_POSITION))
                    .thenReturn(Constants.DEFAULT_COLOR_DROPDOWN_POSITION);

            // When
            final int colorDropdownPosition = prefsHelper.getColorDropdownPosition();

            // Then
            assertEquals(Constants.DEFAULT_COLOR_DROPDOWN_POSITION, colorDropdownPosition);
        }
    }

    @Test
    public void getCustomColor() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            final String expectedCustomColor = "#FF0000";
            Mockito.lenient()
                    .when(sharedPreferences.getString(Constants.PREF_CUSTOM_COLOR, Constants.DEFAULT_CUSTOM_COLOR))
                    .thenReturn(expectedCustomColor);

            // When
            final String customColor = prefsHelper.getCustomColor();

            // Then
            assertEquals(expectedCustomColor, customColor);
        }
    }

    @Test
    public void getCustomColor_defaultValue() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            Mockito.lenient()
                    .when(sharedPreferences.getString(Constants.PREF_CUSTOM_COLOR, Constants.DEFAULT_CUSTOM_COLOR))
                    .thenReturn(Constants.DEFAULT_CUSTOM_COLOR);

            // When
            final String customColor = prefsHelper.getCustomColor();

            // Then
            assertEquals(Constants.DEFAULT_CUSTOM_COLOR, customColor);
        }
    }

    @Test
    public void getColorIntensity() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            final int expectedColorIntensity = 10;
            Mockito.lenient()
                    .when(sharedPreferences.getInt(Constants.PREF_COLOR_INTENSITY, Constants.DEFAULT_COLOR_INTENSITY))
                    .thenReturn(expectedColorIntensity);

            // When
            final int colorIntensity = prefsHelper.getColorIntensity();

            // Then
            assertEquals(expectedColorIntensity, colorIntensity);
        }
    }

    @Test
    public void getColorIntensity_defaultValue() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            Mockito.lenient()
                    .when(sharedPreferences.getInt(Constants.PREF_COLOR_INTENSITY, Constants.DEFAULT_COLOR_INTENSITY))
                    .thenReturn(Constants.DEFAULT_COLOR_INTENSITY);

            // When
            final int colorIntensity = prefsHelper.getColorIntensity();

            // Then
            assertEquals(Constants.DEFAULT_COLOR_INTENSITY, colorIntensity);
        }
    }

    @Test
    public void getBrightness() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            final int expectedBrightness = 15;
            Mockito.lenient()
                    .when(sharedPreferences.getInt(Constants.PREF_BRIGHTNESS, Constants.DEFAULT_BRIGHTNESS))
                    .thenReturn(expectedBrightness);

            // When
            final int brightness = prefsHelper.getBrightness();

            // Then
            assertEquals(expectedBrightness, brightness);
        }
    }

    @Test
    public void getBrightness_defaultValue() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            Mockito.lenient()
                    .when(sharedPreferences.getInt(Constants.PREF_BRIGHTNESS, Constants.DEFAULT_BRIGHTNESS))
                    .thenReturn(Constants.DEFAULT_BRIGHTNESS);

            // When
            final int brightness = prefsHelper.getBrightness();

            // Then
            assertEquals(Constants.DEFAULT_BRIGHTNESS, brightness);
        }
    }

    @Test
    public void shouldUseSameIntensityBrightnessForAll_true() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            final boolean expectedShouldUseSameIntensityBrightnessForAll = true;
            Mockito.lenient()
                    .when(sharedPreferences.getBoolean(Constants.PREF_SAME_INTENSITY_BRIGHTNESS_FOR_ALL, Constants.DEFAULT_SAME_INTENSITY_BRIGHTNESS_FOR_ALL))
                    .thenReturn(expectedShouldUseSameIntensityBrightnessForAll);

            // When
            final boolean shouldUseSameIntensityBrightnessForAll = prefsHelper.shouldUseSameIntensityBrightnessForAll();

            // Then
            assertEquals(expectedShouldUseSameIntensityBrightnessForAll, shouldUseSameIntensityBrightnessForAll);
        }
    }

    @Test
    public void shouldUseSameIntensityBrightnessForAll_false() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            final boolean expectedShouldUseSameIntensityBrightnessForAll = false;
            Mockito.lenient()
                    .when(sharedPreferences.getBoolean(Constants.PREF_SAME_INTENSITY_BRIGHTNESS_FOR_ALL, Constants.DEFAULT_SAME_INTENSITY_BRIGHTNESS_FOR_ALL))
                    .thenReturn(expectedShouldUseSameIntensityBrightnessForAll);

            // When
            final boolean shouldUseSameIntensityBrightnessForAll = prefsHelper.shouldUseSameIntensityBrightnessForAll();

            // Then
            assertEquals(expectedShouldUseSameIntensityBrightnessForAll, shouldUseSameIntensityBrightnessForAll);
        }
    }

    @Test
    public void shouldUseSameIntensityBrightnessForAll_defaultValue() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            Mockito.lenient()
                    .when(sharedPreferences.getBoolean(Constants.PREF_SAME_INTENSITY_BRIGHTNESS_FOR_ALL, Constants.DEFAULT_SAME_INTENSITY_BRIGHTNESS_FOR_ALL))
                    .thenReturn(Constants.DEFAULT_SAME_INTENSITY_BRIGHTNESS_FOR_ALL);

            // When
            final boolean shouldUseSameIntensityBrightnessForAll = prefsHelper.shouldUseSameIntensityBrightnessForAll();

            // Then
            assertEquals(Constants.DEFAULT_SAME_INTENSITY_BRIGHTNESS_FOR_ALL, shouldUseSameIntensityBrightnessForAll);
        }
    }

    @Test
    public void getAutoStartReadMode_true() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            final boolean expectedAutoStartReadMode = true;
            Mockito.lenient()
                    .when(sharedPreferences.getBoolean(Constants.PREF_AUTO_START_READ_MODE, Constants.DEFAULT_AUTO_START_READ_MODE))
                    .thenReturn(expectedAutoStartReadMode);

            // When
            final boolean autoStartReadMode = prefsHelper.getAutoStartReadMode();

            // Then
            assertEquals(expectedAutoStartReadMode, autoStartReadMode);
        }
    }

    @Test
    public void getAutoStartReadMode_false() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            final boolean expectedAutoStartReadMode = false;
            Mockito.lenient()
                    .when(sharedPreferences.getBoolean(Constants.PREF_AUTO_START_READ_MODE, Constants.DEFAULT_AUTO_START_READ_MODE))
                    .thenReturn(expectedAutoStartReadMode);

            // When
            final boolean autoStartReadMode = prefsHelper.getAutoStartReadMode();

            // Then
            assertEquals(expectedAutoStartReadMode, autoStartReadMode);
        }
    }

    @Test
    public void getAutoStartReadMode_defaultValue() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            Mockito.lenient()
                    .when(sharedPreferences.getBoolean(Constants.PREF_AUTO_START_READ_MODE, Constants.DEFAULT_AUTO_START_READ_MODE))
                    .thenReturn(Constants.DEFAULT_AUTO_START_READ_MODE);

            // When
            final boolean autoStartReadMode = prefsHelper.getAutoStartReadMode();

            // Then
            assertEquals(Constants.DEFAULT_AUTO_START_READ_MODE, autoStartReadMode);
        }
    }

    @Test
    public void getTheme_SystemDefault() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            final Constants.ThemeMode expectedTheme = Constants.ThemeMode.SYSTEM_DEFAULT;
            Mockito.lenient()
                    .when(sharedPreferences.getInt(Constants.PREF_THEME, Constants.DEFAULT_THEME))
                    .thenReturn(expectedTheme.getValue());

            // When
            final Constants.ThemeMode theme = prefsHelper.getTheme();

            // Then
            assertEquals(expectedTheme, theme);
        }
    }

    @Test
    public void getTheme_Light() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            final Constants.ThemeMode expectedTheme = Constants.ThemeMode.LIGHT;
            Mockito.lenient()
                    .when(sharedPreferences.getInt(Constants.PREF_THEME, Constants.DEFAULT_THEME))
                    .thenReturn(expectedTheme.getValue());

            // When
            final Constants.ThemeMode theme = prefsHelper.getTheme();

            // Then
            assertEquals(expectedTheme, theme);
        }
    }

    @Test
    public void getTheme_Dark() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            final Constants.ThemeMode expectedTheme = Constants.ThemeMode.DARK;
            Mockito.lenient()
                    .when(sharedPreferences.getInt(Constants.PREF_THEME, Constants.DEFAULT_THEME))
                    .thenReturn(expectedTheme.getValue());

            // When
            final Constants.ThemeMode theme = prefsHelper.getTheme();

            // Then
            assertEquals(expectedTheme, theme);
        }
    }

    @Test
    public void getColor() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            final String expectedColor = "#FF0000";
            Mockito.lenient()
                    .when(sharedPreferences.getString(Constants.PREF_COLOR, Constants.DEFAULT_COLOR_WHITE))
                    .thenReturn(expectedColor);

            // When
            final String color = prefsHelper.getColor();

            // Then
            assertEquals(expectedColor, color);
        }
    }

    @Test
    public void getColor_defaultValue() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            Mockito.lenient()
                    .when(sharedPreferences.getString(Constants.PREF_COLOR, Constants.DEFAULT_COLOR_WHITE))
                    .thenReturn(Constants.DEFAULT_COLOR_WHITE);

            // When
            final String color = prefsHelper.getColor();

            // Then
            assertEquals(Constants.DEFAULT_COLOR_WHITE, color);
        }
    }

    @Test
    public void getColorSettings() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            doReturn("{}").when(sharedPreferences).getString(Constants.PREF_COLOR_SETTINGS, Constants.DEFAULT_COLOR_SETTINGS);
            prefsHelper.initPrefColorSettingsMap();
            final int colorDropdownPosition = 0;

            // When
            final ColorSettings colorSettingsBefore = prefsHelper.getColorSettings(colorDropdownPosition);

            // Then
            assertNotNull(colorSettingsBefore);
            assertEquals(Constants.DEFAULT_COLOR_INTENSITY, colorSettingsBefore.getColorIntensity());
            assertEquals(Constants.DEFAULT_BRIGHTNESS, colorSettingsBefore.getBrightness());
        }
    }

    @Test
    public void getColorSettings_colorSettingDoesNotExist() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            final int colorDropdownPosition = 0;

            // When
            final ColorSettings colorSettingsBefore = prefsHelper.getColorSettings(colorDropdownPosition);

            // Then
            assertNull(colorSettingsBefore);
        }
    }

    @Test
    public void saveProperty_String() {
        // Given
        final SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        doReturn(editor).when(sharedPreferences).edit();
        final String property = "property";
        final String value = "value";

        // When
        prefsHelper.saveProperty(property, value);

        // Then
        Mockito.verify(editor).putString(property, value);
        Mockito.verify(editor).apply();
    }

    @Test
    public void saveProperty_Boolean() {
        // Given
        final SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        doReturn(editor).when(sharedPreferences).edit();
        final String property = "property";
        final boolean value = true;

        // When
        prefsHelper.saveProperty(property, value);

        // Then
        Mockito.verify(editor).putBoolean(property, value);
        Mockito.verify(editor).apply();
    }

    @Test
    public void saveProperty_Int() {
        // Given
        final SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        doReturn(editor).when(sharedPreferences).edit();
        final String property = "property";
        final int value = 1;

        // When
        prefsHelper.saveProperty(property, value);

        // Then
        Mockito.verify(editor).putInt(property, value);
        Mockito.verify(editor).apply();
    }

    @Test
    public void tryToSaveColorSettingsProperty() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            doReturn("{}").when(sharedPreferences).getString(Constants.PREF_COLOR_SETTINGS, Constants.DEFAULT_COLOR_SETTINGS);
            final SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
            doReturn(editor).when(sharedPreferences).edit();
            prefsHelper.initPrefColorSettingsMap();
            final ReadModeSettings readModeSettings = ReadModeSettings.init();
            readModeSettings.setShouldUseSameIntensityBrightnessForAll(false);
            final int colorDropdownPosition = 0;
            final int colorIntensity = 25;
            final int brightness = 10;
            readModeSettings.setColorDropdownPosition(colorDropdownPosition);
            readModeSettings.setColorIntensity(colorIntensity);
            readModeSettings.setBrightness(brightness);

            // Check the current values before saving the new ones
            final ColorSettings colorSettingsBefore = prefsHelper.getColorSettings(colorDropdownPosition);
            assertNotNull(colorSettingsBefore);
            assertEquals(Constants.DEFAULT_COLOR_INTENSITY, colorSettingsBefore.getColorIntensity());
            assertEquals(Constants.DEFAULT_BRIGHTNESS, colorSettingsBefore.getBrightness());

            // When
            prefsHelper.tryToSaveColorSettingsProperty(readModeSettings);

            // Then
            // Values were updated
            final ColorSettings colorSettingsAfter = prefsHelper.getColorSettings(colorDropdownPosition);
            assertNotNull(colorSettingsAfter);
            assertEquals(colorIntensity, colorSettingsAfter.getColorIntensity());
            assertEquals(brightness, colorSettingsAfter.getBrightness());
        }
    }

    @Test
    public void tryToSaveColorSettingsProperty_mapIsEmpty() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            final ReadModeSettings readModeSettings = ReadModeSettings.init();
            readModeSettings.setShouldUseSameIntensityBrightnessForAll(false);
            final int colorDropdownPosition = 0;
            final int colorIntensity = 25;
            final int brightness = 10;
            readModeSettings.setColorDropdownPosition(colorDropdownPosition);
            readModeSettings.setColorIntensity(colorIntensity);
            readModeSettings.setBrightness(brightness);

            // When
            prefsHelper.tryToSaveColorSettingsProperty(readModeSettings);

            // Then
            final ColorSettings colorSettings = prefsHelper.getColorSettings(colorDropdownPosition);
            assertNull(colorSettings);
        }
    }

    @Test
    public void tryToSaveColorSettingsProperty_shouldUseSameIntensityBrightnessForAll() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            doReturn("{}").when(sharedPreferences).getString(Constants.PREF_COLOR_SETTINGS, Constants.DEFAULT_COLOR_SETTINGS);
            prefsHelper.initPrefColorSettingsMap();
            final ReadModeSettings readModeSettings = ReadModeSettings.init();
            readModeSettings.setShouldUseSameIntensityBrightnessForAll(true);
            final int colorDropdownPosition = 0;
            final int colorIntensity = 25;
            final int brightness = 10;
            readModeSettings.setColorDropdownPosition(colorDropdownPosition);
            readModeSettings.setColorIntensity(colorIntensity);
            readModeSettings.setBrightness(brightness);

            // Check the current values before saving the new ones
            final ColorSettings colorSettingsBefore = prefsHelper.getColorSettings(colorDropdownPosition);
            assertNotNull(colorSettingsBefore);
            assertEquals(Constants.DEFAULT_COLOR_INTENSITY, colorSettingsBefore.getColorIntensity());
            assertEquals(Constants.DEFAULT_BRIGHTNESS, colorSettingsBefore.getBrightness());

            // When
            prefsHelper.tryToSaveColorSettingsProperty(readModeSettings);

            // Then
            // Values are the same
            final ColorSettings colorSettingsAfter = prefsHelper.getColorSettings(colorDropdownPosition);
            assertNotNull(colorSettingsAfter);
            assertEquals(Constants.DEFAULT_COLOR_INTENSITY, colorSettingsAfter.getColorIntensity());
            assertEquals(Constants.DEFAULT_BRIGHTNESS, colorSettingsAfter.getBrightness());
        }
    }

    @Test
    public void resetAppData() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            colorMock.when(() -> Color.parseColor(anyString())).thenReturn(1);
            final SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
            doReturn(editor).when(sharedPreferences).edit();

            // When
            prefsHelper.resetAppData();

            // Then
            Mockito.verify(editor).putBoolean(Constants.PREF_IS_READ_MODE_ON, Constants.DEFAULT_IS_READ_MODE_ENABLED);
            Mockito.verify(editor).putInt(Constants.PREF_COLOR_DROPDOWN, Constants.DEFAULT_COLOR_DROPDOWN_POSITION);
            Mockito.verify(editor).putString(Constants.PREF_COLOR, Constants.DEFAULT_COLOR_WHITE);
            Mockito.verify(editor).putString(Constants.PREF_CUSTOM_COLOR, Constants.DEFAULT_CUSTOM_COLOR);
            Mockito.verify(editor).putString(Constants.PREF_COLOR_SETTINGS, Constants.DEFAULT_COLOR_SETTINGS);
            Mockito.verify(editor).putInt(Constants.PREF_COLOR_INTENSITY, Constants.DEFAULT_COLOR_INTENSITY);
            Mockito.verify(editor).putInt(Constants.PREF_BRIGHTNESS, Constants.DEFAULT_BRIGHTNESS);
            Mockito.verify(editor).putInt(Constants.PREF_THEME, Constants.DEFAULT_THEME);
            Mockito.verify(editor).putBoolean(Constants.PREF_AUTO_START_READ_MODE, Constants.DEFAULT_AUTO_START_READ_MODE);
            Mockito.verify(editor).putBoolean(Constants.PREF_SAME_INTENSITY_BRIGHTNESS_FOR_ALL, Constants.DEFAULT_SAME_INTENSITY_BRIGHTNESS_FOR_ALL);
        }
    }
}
