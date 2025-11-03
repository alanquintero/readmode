/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode.ui.controller;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import autonightmode.mx.com.alanquintero.autonightmode.R;
import autonightmode.mx.com.alanquintero.autonightmode.model.ReadModeSettings;
import autonightmode.mx.com.alanquintero.autonightmode.util.Constants;
import autonightmode.mx.com.alanquintero.autonightmode.util.PrefsHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class TextViewControllerTest {

    private AutoCloseable mocks;
    private Context context;
    private PrefsHelper mockPrefsHelper;
    private ReadModeSettings mockReadModeSettings;
    private TextView mockColorSettingsText;
    private View mockRootView;
    private String[] colorNames;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        // Setup real context
        context = ApplicationProvider.getApplicationContext();

        // Setup mocks
        mockPrefsHelper = mock(PrefsHelper.class);
        mockReadModeSettings = mock(ReadModeSettings.class);
        mockColorSettingsText = mock(TextView.class);
        mockRootView = mock(View.class);

        // Setup color names
        colorNames = new String[]{"Red", "Green", "Blue", "Custom"};

        // Mock the findViewById call
        when(mockRootView.findViewById(R.id.labelColorSettings)).thenReturn(mockColorSettingsText);
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void constructor_initializesCorrectlyAndFindsViews() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(any())).thenReturn(mockPrefsHelper);

            // When
            new TextViewController(context, mockRootView, mockReadModeSettings, colorNames);

            // Then
            verify(mockRootView).findViewById(R.id.labelColorSettings);
            prefsHelperStatic.verify(() -> PrefsHelper.init(context));
        }
    }

    @Test
    public void setupTextViews_callsSetColorSettingsText() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(any())).thenReturn(mockPrefsHelper);
            final TextViewController controller = new TextViewController(context, mockRootView, mockReadModeSettings, colorNames);

            // Mock the behavior for setColorSettingsText
            when(mockPrefsHelper.shouldUseSameIntensityBrightnessForAll()).thenReturn(true);

            // When
            controller.setupTextViews();

            // Then - setColorSettingsText should be called which will set the text
            // We verify through the mock that the text was set
            verify(mockColorSettingsText).setText(anyString());
        }
    }

    @Test
    public void setColorSettingsText_whenSameIntensityTrue_setsPlaceholderText() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(any())).thenReturn(mockPrefsHelper);
            final TextViewController controller = new TextViewController(context, mockRootView, mockReadModeSettings, colorNames);

            when(mockPrefsHelper.shouldUseSameIntensityBrightnessForAll()).thenReturn(true);
            final String placeholder = context.getString(R.string.color_settings_placeholder);

            // When
            controller.setColorSettingsText();

            // Then
            verify(mockColorSettingsText).setText(placeholder);
            // Verify that color dropdown position was NOT accessed
            verify(mockReadModeSettings, never()).getColorDropdownPosition();
        }
    }

    @Test
    public void setColorSettingsText_whenSameIntensityFalse_setsFormattedTextWithColor() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(any())).thenReturn(mockPrefsHelper);
            final TextViewController controller = new TextViewController(context, mockRootView, mockReadModeSettings, colorNames);

            when(mockPrefsHelper.shouldUseSameIntensityBrightnessForAll()).thenReturn(false);
            when(mockReadModeSettings.getColorDropdownPosition()).thenReturn(1); // "Green"
            final String placeholder = context.getString(R.string.color_settings_placeholder);
            final String expectedText = context.getString(R.string.color_settings_format, placeholder, "Green");

            // When
            controller.setColorSettingsText();

            // Then
            verify(mockColorSettingsText).setText(expectedText);
            verify(mockReadModeSettings).getColorDropdownPosition();
        }
    }

    @Test
    public void setColorSettingsText_whenColorPositionZero_setsFormattedTextWithFirstColor() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(any())).thenReturn(mockPrefsHelper);
            final TextViewController controller = new TextViewController(context, mockRootView, mockReadModeSettings, colorNames);

            when(mockPrefsHelper.shouldUseSameIntensityBrightnessForAll()).thenReturn(false);
            when(mockReadModeSettings.getColorDropdownPosition()).thenReturn(0); // "Red"
            final String placeholder = context.getString(R.string.color_settings_placeholder);
            final String expectedText = context.getString(R.string.color_settings_format, placeholder, "Red");

            // When
            controller.setColorSettingsText();

            // Then
            verify(mockColorSettingsText).setText(expectedText);
        }
    }

    @Test
    public void setColorSettingsText_whenColorPositionLast_setsFormattedTextWithLastColor() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(any())).thenReturn(mockPrefsHelper);
            final TextViewController controller = new TextViewController(context, mockRootView, mockReadModeSettings, colorNames);

            when(mockPrefsHelper.shouldUseSameIntensityBrightnessForAll()).thenReturn(false);
            when(mockReadModeSettings.getColorDropdownPosition()).thenReturn(3); // "Custom"
            final String placeholder = context.getString(R.string.color_settings_placeholder);
            final String expectedText = context.getString(R.string.color_settings_format, placeholder, "Custom");

            // When
            controller.setColorSettingsText();

            // Then
            verify(mockColorSettingsText).setText(expectedText);
        }
    }

    @Test
    public void onColorDropdownPositionChange_callsSetColorSettingsText() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(any())).thenReturn(mockPrefsHelper);
            final TextViewController controller = new TextViewController(context, mockRootView, mockReadModeSettings, colorNames);

            // Mock the behavior for setColorSettingsText
            when(mockPrefsHelper.shouldUseSameIntensityBrightnessForAll()).thenReturn(true);

            // When
            controller.onColorDropdownPositionChange(2); // Any position

            // Then
            verify(mockColorSettingsText).setText(anyString());
        }
    }

    @Test
    public void onSettingsChanged_whenSameSettingsForAll_callsSetColorSettingsText() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(any())).thenReturn(mockPrefsHelper);
            final TextViewController controller = new TextViewController(context, mockRootView, mockReadModeSettings, colorNames);

            // Mock the behavior for setColorSettingsText
            when(mockPrefsHelper.shouldUseSameIntensityBrightnessForAll()).thenReturn(true);

            // When
            controller.onSettingsChanged(Constants.SETTING_OPTIONS.SAME_SETTINGS_FOR_ALL);

            // Then
            verify(mockColorSettingsText).setText(anyString());
        }
    }

    @Test
    public void onSettingsChanged_whenAutoReadMode_doesNothing() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(any())).thenReturn(mockPrefsHelper);
            final TextViewController controller = new TextViewController(context, mockRootView, mockReadModeSettings, colorNames);

            // When
            controller.onSettingsChanged(Constants.SETTING_OPTIONS.AUTO_READ_MODE);

            // Then - No text should be set
            verify(mockColorSettingsText, never()).setText(anyString());
        }
    }

    @Test
    public void onSettingsChanged_whenResetAppData_doesNothing() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(any())).thenReturn(mockPrefsHelper);
            final TextViewController controller = new TextViewController(context, mockRootView, mockReadModeSettings, colorNames);

            // When
            controller.onSettingsChanged(Constants.SETTING_OPTIONS.RESET_APP_DATA);

            // Then - No text should be set (default case in switch)
            verify(mockColorSettingsText, never()).setText(anyString());
        }
    }

    @Test
    public void setColorSettingsText_whenSameIntensityChanges_dynamicBehavior() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(any())).thenReturn(mockPrefsHelper);
            final TextViewController controller = new TextViewController(context, mockRootView, mockReadModeSettings, colorNames);

            // First call - same intensity is true
            when(mockPrefsHelper.shouldUseSameIntensityBrightnessForAll()).thenReturn(true);
            final String placeholder = context.getString(R.string.color_settings_placeholder);

            // When - First call
            controller.setColorSettingsText();

            // Then - First call should set placeholder
            verify(mockColorSettingsText).setText(placeholder);

            // Reset mock for second call
            when(mockPrefsHelper.shouldUseSameIntensityBrightnessForAll()).thenReturn(false);
            when(mockReadModeSettings.getColorDropdownPosition()).thenReturn(2); // "Blue"
            final String expectedText = context.getString(R.string.color_settings_format, placeholder, "Blue");

            // When - Second call with different same intensity setting
            controller.setColorSettingsText();

            // Then - Second call should set formatted text
            verify(mockColorSettingsText).setText(expectedText);
        }
    }

    @Test
    public void setColorSettingsText_multipleColorChanges_updatesTextCorrectly() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(any())).thenReturn(mockPrefsHelper);
            final TextViewController controller = new TextViewController(context, mockRootView, mockReadModeSettings, colorNames);

            when(mockPrefsHelper.shouldUseSameIntensityBrightnessForAll()).thenReturn(false);
            final String placeholder = context.getString(R.string.color_settings_placeholder);

            // Test multiple color positions
            when(mockReadModeSettings.getColorDropdownPosition()).thenReturn(0); // "Red"
            final String expectedText1 = context.getString(R.string.color_settings_format, placeholder, "Red");
            controller.setColorSettingsText();
            verify(mockColorSettingsText).setText(expectedText1);

            when(mockReadModeSettings.getColorDropdownPosition()).thenReturn(1); // "Green"
            final String expectedText2 = context.getString(R.string.color_settings_format, placeholder, "Green");
            controller.setColorSettingsText();
            verify(mockColorSettingsText).setText(expectedText2);

            when(mockReadModeSettings.getColorDropdownPosition()).thenReturn(2); // "Blue"
            String expectedText3 = context.getString(R.string.color_settings_format, placeholder, "Blue");
            controller.setColorSettingsText();
            verify(mockColorSettingsText).setText(expectedText3);
        }
    }
}
