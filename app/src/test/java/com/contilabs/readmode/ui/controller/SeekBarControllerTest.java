/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui.controller;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.contilabs.readmode.R;
import com.contilabs.readmode.command.ReadModeCommand;
import com.contilabs.readmode.model.ColorSettings;
import com.contilabs.readmode.model.ReadModeSettings;
import com.contilabs.readmode.util.ColorUtils;
import com.contilabs.readmode.util.Constants;
import com.contilabs.readmode.util.PrefsHelper;
import com.contilabs.readmode.util.Utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.M})
public class SeekBarControllerTest {

    private AutoCloseable mocks;
    private Context mockContext;
    private View mockRootView;
    private ReadModeCommand mockReadModeCommand;
    private ReadModeSettings mockReadModeSettings;
    private PrefsHelper mockPrefsHelper;
    private TextView mockLabelColorSettings;
    private View mockLineColorSettings;
    private SeekBar mockSeekColorIntensityBar;
    private TextView mockColorLevelText;
    private TextView mockColorLevelPercentageText;
    private TextView mockBrightnessLevelText;
    private SeekBar mockSeekBrightnessBar;
    private TextView mockBrightnessLevelPercentageText;
    private LinearLayout mockContainerLayout;
    private GradientDrawable mockGradientDrawable;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        // Setup mocks
        mockContext = mock(Context.class);
        mockRootView = mock(View.class);
        mockReadModeCommand = mock(ReadModeCommand.class);
        mockReadModeSettings = mock(ReadModeSettings.class);
        mockPrefsHelper = mock(PrefsHelper.class);
        mockLabelColorSettings = mock(TextView.class);
        mockLineColorSettings = mock(View.class);
        mockSeekColorIntensityBar = mock(SeekBar.class);
        mockColorLevelText = mock(TextView.class);
        mockColorLevelPercentageText = mock(TextView.class);
        mockBrightnessLevelText = mock(TextView.class);
        mockSeekBrightnessBar = mock(SeekBar.class);
        mockBrightnessLevelPercentageText = mock(TextView.class);
        mockContainerLayout = mock(LinearLayout.class);
        mockGradientDrawable = mock(GradientDrawable.class);
        final Resources mockResources = mock(Resources.class);

        // Setup common mock behavior
        when(mockRootView.findViewById(R.id.labelColorSettings)).thenReturn(mockLabelColorSettings);
        when(mockRootView.findViewById(R.id.lineColorSettings)).thenReturn(mockLineColorSettings);
        when(mockRootView.findViewById(R.id.colorLevelBar)).thenReturn(mockSeekColorIntensityBar);
        when(mockRootView.findViewById(R.id.colorLevelText)).thenReturn(mockColorLevelText);
        when(mockRootView.findViewById(R.id.colorLevelPercentageText)).thenReturn(mockColorLevelPercentageText);
        when(mockRootView.findViewById(R.id.brightnessLevelText)).thenReturn(mockBrightnessLevelText);
        when(mockRootView.findViewById(R.id.brightnessLevelBar)).thenReturn(mockSeekBrightnessBar);
        when(mockRootView.findViewById(R.id.brightnessLevelPercentageText)).thenReturn(mockBrightnessLevelPercentageText);
        when(mockRootView.findViewById(R.id.colorSettingsContainer)).thenReturn(mockContainerLayout);

        when(mockContainerLayout.getBackground()).thenReturn(mockGradientDrawable);
        when(mockContext.getResources()).thenReturn(mockResources);
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void setupSeekBars_restoresSavedSelectionAndSetsUpListeners() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(mockContext)).thenReturn(mockPrefsHelper);
            when(mockReadModeSettings.getColorIntensity()).thenReturn(50);
            when(mockReadModeSettings.getBrightness()).thenReturn(75);
            when(mockContext.getString(R.string.color_intensity, 50)).thenReturn("50%");
            when(mockContext.getString(R.string.brightness_level, 75)).thenReturn("75%");

            final SeekBarController controller = new SeekBarController(mockContext, mockRootView, mockReadModeCommand, mockReadModeSettings);

            // When
            controller.setupSeekBars();

            // Then
            verify(mockSeekColorIntensityBar).setProgress(50);
            verify(mockColorLevelPercentageText).setText("50%");
            verify(mockSeekBrightnessBar).setProgress(75);
            verify(mockBrightnessLevelPercentageText).setText("75%");
            verify(mockSeekColorIntensityBar).setOnSeekBarChangeListener(org.mockito.ArgumentMatchers.any(SeekBar.OnSeekBarChangeListener.class));
            verify(mockSeekBrightnessBar).setOnSeekBarChangeListener(org.mockito.ArgumentMatchers.any(SeekBar.OnSeekBarChangeListener.class));
        }
    }

    @Test
    public void setupSeekBrightnessBar_whenProgressChangedAndFromUser_updatesSettingsAndCommands() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(mockContext)).thenReturn(mockPrefsHelper);
            when(mockReadModeSettings.isAutoStartReadMode()).thenReturn(true);
            when(mockContext.getString(R.string.brightness_level, 60)).thenReturn("60%");

            final SeekBarController controller = new SeekBarController(mockContext, mockRootView, mockReadModeCommand, mockReadModeSettings);
            controller.setupSeekBars();

            // Capture the listener
            ArgumentCaptor<SeekBar.OnSeekBarChangeListener> listenerCaptor = ArgumentCaptor.forClass(SeekBar.OnSeekBarChangeListener.class);
            verify(mockSeekBrightnessBar).setOnSeekBarChangeListener(listenerCaptor.capture());
            SeekBar.OnSeekBarChangeListener listener = listenerCaptor.getValue();

            // When
            listener.onProgressChanged(mockSeekBrightnessBar, 60, true);

            // Then
            verify(mockReadModeSettings).setBrightness(60);
            verify(mockPrefsHelper).saveProperty(Constants.PREF_BRIGHTNESS, 60);
            verify(mockPrefsHelper).tryToSaveColorSettingsProperty(mockReadModeSettings);
            verify(mockReadModeCommand).updateReadMode();
        }
    }

    @Test
    public void setupSeekBrightnessBar_whenProgressChangedAndNotFromUser_doesNothing() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(mockContext)).thenReturn(mockPrefsHelper);

            final SeekBarController controller = new SeekBarController(mockContext, mockRootView, mockReadModeCommand, mockReadModeSettings);
            controller.setupSeekBars();

            // Capture the listener
            ArgumentCaptor<SeekBar.OnSeekBarChangeListener> listenerCaptor = ArgumentCaptor.forClass(SeekBar.OnSeekBarChangeListener.class);
            verify(mockSeekBrightnessBar).setOnSeekBarChangeListener(listenerCaptor.capture());
            SeekBar.OnSeekBarChangeListener listener = listenerCaptor.getValue();

            // When
            listener.onProgressChanged(mockSeekBrightnessBar, 60, false);

            // Then
            verify(mockReadModeSettings, never()).setBrightness(60);
            verify(mockPrefsHelper, never()).saveProperty(Constants.PREF_BRIGHTNESS, 60);
            verify(mockReadModeCommand, never()).updateReadMode();
        }
    }

    @Test
    public void setupSeekColorIntensityBar_whenProgressChangedAndFromUser_updatesSettingsAndCommands() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(mockContext)).thenReturn(mockPrefsHelper);
            when(mockReadModeSettings.isAutoStartReadMode()).thenReturn(true);
            when(mockContext.getString(R.string.color_intensity, 40)).thenReturn("40%");

            final SeekBarController controller = new SeekBarController(mockContext, mockRootView, mockReadModeCommand, mockReadModeSettings);
            controller.setupSeekBars();

            // Capture the listener
            ArgumentCaptor<SeekBar.OnSeekBarChangeListener> listenerCaptor = ArgumentCaptor.forClass(SeekBar.OnSeekBarChangeListener.class);
            verify(mockSeekColorIntensityBar).setOnSeekBarChangeListener(listenerCaptor.capture());
            SeekBar.OnSeekBarChangeListener listener = listenerCaptor.getValue();

            // When
            listener.onProgressChanged(mockSeekColorIntensityBar, 40, true);

            // Then
            verify(mockReadModeSettings).setColorIntensity(40);
            verify(mockPrefsHelper).saveProperty(Constants.PREF_COLOR_INTENSITY, 40);
            verify(mockPrefsHelper).tryToSaveColorSettingsProperty(mockReadModeSettings);
            verify(mockReadModeCommand).updateReadMode();
        }
    }

    @Test
    public void updateSeekBarsForSelectedColor_whenSameIntensityBrightnessForAll_usesGlobalSettings() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class);
             MockedStatic<Utils> utilsStatic = mockStatic(Utils.class)) {

            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(mockContext)).thenReturn(mockPrefsHelper);
            when(mockReadModeSettings.shouldUseSameIntensityBrightnessForAll()).thenReturn(true);
            when(mockPrefsHelper.getColorIntensity()).thenReturn(30);
            when(mockPrefsHelper.getBrightness()).thenReturn(80);
            when(mockContext.getString(R.string.color_intensity, 30)).thenReturn("30%");
            when(mockContext.getString(R.string.brightness_level, 80)).thenReturn("80%");
            when(mockReadModeSettings.isAutoStartReadMode()).thenReturn(false);
            utilsStatic.when(() -> Utils.isDarkMode(mockContext)).thenReturn(false);

            final SeekBarController controller = new SeekBarController(mockContext, mockRootView, mockReadModeCommand, mockReadModeSettings);

            // When
            controller.updateSeekBarsForSelectedColor();

            // Then
            verify(mockSeekColorIntensityBar).setProgress(30);
            verify(mockColorLevelPercentageText).setText("30%");
            verify(mockSeekBrightnessBar).setProgress(80);
            verify(mockBrightnessLevelPercentageText).setText("80%");
            verify(mockContainerLayout).getBackground(); // setContainerColors called
        }
    }

    @Test
    public void updateSeekBarsForSelectedColor_whenDifferentIntensityBrightnessForAll_usesColorSpecificSettings() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class);
             MockedStatic<Utils> utilsStatic = mockStatic(Utils.class)) {

            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(mockContext)).thenReturn(mockPrefsHelper);
            when(mockReadModeSettings.shouldUseSameIntensityBrightnessForAll()).thenReturn(false);
            when(mockReadModeSettings.getColorDropdownPosition()).thenReturn(2);

            ColorSettings mockColorSettings = mock(ColorSettings.class);
            when(mockColorSettings.getColorIntensity()).thenReturn(25);
            when(mockColorSettings.getBrightness()).thenReturn(65);
            when(mockPrefsHelper.getColorSettings(2)).thenReturn(mockColorSettings);

            when(mockContext.getString(R.string.color_intensity, 25)).thenReturn("25%");
            when(mockContext.getString(R.string.brightness_level, 65)).thenReturn("65%");
            when(mockReadModeSettings.isAutoStartReadMode()).thenReturn(false);
            utilsStatic.when(() -> Utils.isDarkMode(mockContext)).thenReturn(false);

            final SeekBarController controller = new SeekBarController(mockContext, mockRootView, mockReadModeCommand, mockReadModeSettings);

            // When
            controller.updateSeekBarsForSelectedColor();

            // Then
            verify(mockReadModeSettings).setColorIntensity(25);
            verify(mockReadModeSettings).setBrightness(65);
            verify(mockContainerLayout).getBackground(); // setContainerColors called
        }
    }

    @Test
    public void updateSeekBarsForSelectedColor_whenColorSettingsNull_usesDefaultValues() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class);
             MockedStatic<Utils> utilsStatic = mockStatic(Utils.class)) {

            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(mockContext)).thenReturn(mockPrefsHelper);
            when(mockReadModeSettings.shouldUseSameIntensityBrightnessForAll()).thenReturn(false);
            when(mockReadModeSettings.getColorDropdownPosition()).thenReturn(3);
            when(mockPrefsHelper.getColorSettings(3)).thenReturn(null);

            when(mockContext.getString(R.string.color_intensity, Constants.DEFAULT_COLOR_INTENSITY)).thenReturn("50%");
            when(mockContext.getString(R.string.brightness_level, Constants.DEFAULT_BRIGHTNESS)).thenReturn("50%");
            when(mockReadModeSettings.isAutoStartReadMode()).thenReturn(false);
            utilsStatic.when(() -> Utils.isDarkMode(mockContext)).thenReturn(false);

            final SeekBarController controller = new SeekBarController(mockContext, mockRootView, mockReadModeCommand, mockReadModeSettings);

            // When
            controller.updateSeekBarsForSelectedColor();

            // Then
            verify(mockSeekColorIntensityBar).setProgress(Constants.DEFAULT_COLOR_INTENSITY);
            verify(mockColorLevelPercentageText).setText("50%");
            verify(mockSeekBrightnessBar).setProgress(Constants.DEFAULT_BRIGHTNESS);
            verify(mockBrightnessLevelPercentageText).setText("50%");
            verify(mockContainerLayout).getBackground(); // setContainerColors called
        }
    }

    @Test
    public void setContainerColors_whenCustomColorPosition_setsColorsBasedOnAdjustedColor() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class);
             MockedStatic<ColorUtils> colorUtilsStatic = mockStatic(ColorUtils.class);
             MockedStatic<Utils> utilsStatic = mockStatic(Utils.class)) {

            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(mockContext)).thenReturn(mockPrefsHelper);
            when(mockReadModeSettings.getColorDropdownPosition()).thenReturn(Constants.CUSTOM_COLOR_DROPDOWN_POSITION);
            when(mockReadModeSettings.getCustomColor()).thenReturn("#FF0000");
            when(mockSeekColorIntensityBar.getProgress()).thenReturn(40);
            when(mockSeekBrightnessBar.getProgress()).thenReturn(70);

            colorUtilsStatic.when(() -> ColorUtils.adjustColor(Color.parseColor("#FF0000"), 40, 70)).thenReturn(0xFFFF8080);
            colorUtilsStatic.when(() -> ColorUtils.isColorDark(0xFFFF8080)).thenReturn(false);

            final SeekBarController controller = new SeekBarController(mockContext, mockRootView, mockReadModeCommand, mockReadModeSettings);

            // When - We need to call this indirectly through a public method
            // Since setContainerColors is private, we'll test it through updateSeekBarsForSelectedColor
            when(mockReadModeSettings.isAutoStartReadMode()).thenReturn(false);
            utilsStatic.when(() -> Utils.isDarkMode(mockContext)).thenReturn(false);
            controller.updateSeekBarsForSelectedColor();

            // Then - Verify the background color was set
            verify(mockGradientDrawable).setColor(0xFFFF8080);
        }
    }

    @Test
    public void setContainerColors_whenNonCustomColorPositionAndDarkMode_setsWhiteTextColors() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class);
             MockedStatic<ColorUtils> colorUtilsStatic = mockStatic(ColorUtils.class);
             MockedStatic<Utils> utilsStatic = mockStatic(Utils.class)) {

            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(mockContext)).thenReturn(mockPrefsHelper);
            when(mockReadModeSettings.getColorDropdownPosition()).thenReturn(1); // Not custom color
            when(mockSeekColorIntensityBar.getProgress()).thenReturn(40);
            when(mockSeekBrightnessBar.getProgress()).thenReturn(70);

            colorUtilsStatic.when(() -> ColorUtils.adjustColor(Constants.BACKGROUND_COLOR_FOR_DROPDOWN_ITEMS[1], 40, 70)).thenReturn(0xFF303030);
            utilsStatic.when(() -> Utils.isDarkMode(mockContext)).thenReturn(true);
            when(mockReadModeSettings.isAutoStartReadMode()).thenReturn(false);

            final SeekBarController controller = new SeekBarController(mockContext, mockRootView, mockReadModeCommand, mockReadModeSettings);

            // When
            controller.updateSeekBarsForSelectedColor();

            // Then
            verify(mockGradientDrawable).setColor(0xFF303030);
            verify(mockLabelColorSettings).setTextColor(Color.WHITE);
            verify(mockLineColorSettings).setBackgroundColor(Color.WHITE);
            verify(mockColorLevelText).setTextColor(Color.WHITE);
            verify(mockColorLevelPercentageText).setTextColor(Color.WHITE);
            verify(mockBrightnessLevelText).setTextColor(Color.WHITE);
            verify(mockBrightnessLevelPercentageText).setTextColor(Color.WHITE);
        }
    }

    @Test
    public void handleContainerBackgroundColor_whenAutoStartReadMode_resetsToInitialColors() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class);
             MockedStatic<ContextCompat> contextCompatStatic = mockStatic(ContextCompat.class)) {

            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(mockContext)).thenReturn(mockPrefsHelper);
            when(mockReadModeSettings.isAutoStartReadMode()).thenReturn(true);

            // Mock the color state list
            android.content.res.ColorStateList mockColorStateList = mock(android.content.res.ColorStateList.class);
            contextCompatStatic.when(() -> ContextCompat.getColorStateList(mockContext, R.color.surface_color_settings))
                    .thenReturn(mockColorStateList);
            when(mockColorStateList.getDefaultColor()).thenReturn(Color.WHITE);

            final SeekBarController controller = new SeekBarController(mockContext, mockRootView, mockReadModeCommand, mockReadModeSettings);

            // When - Since handleContainerBackgroundColor is private, test through onSettingsChanged
            controller.onSettingsChanged(Constants.SETTING_OPTIONS.AUTO_READ_MODE);

            // Then
            verify(mockLabelColorSettings).setTextColor(Color.BLACK);
            verify(mockReadModeCommand, never()).updateReadMode();
        }
    }

    @Test
    public void onColorDropdownPositionChange_callsUpdateSeekBarsForSelectedColor() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class);
             MockedStatic<Utils> utilsStatic = mockStatic(Utils.class)) {

            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(mockContext)).thenReturn(mockPrefsHelper);
            when(mockReadModeSettings.shouldUseSameIntensityBrightnessForAll()).thenReturn(true);
            when(mockPrefsHelper.getColorIntensity()).thenReturn(30);
            when(mockPrefsHelper.getBrightness()).thenReturn(70);
            when(mockContext.getString(R.string.color_intensity, 30)).thenReturn("30%");
            when(mockContext.getString(R.string.brightness_level, 70)).thenReturn("70%");
            when(mockReadModeSettings.isAutoStartReadMode()).thenReturn(false);
            utilsStatic.when(() -> Utils.isDarkMode(mockContext)).thenReturn(false);

            final SeekBarController controller = new SeekBarController(mockContext, mockRootView, mockReadModeCommand, mockReadModeSettings);

            // When
            controller.onColorDropdownPositionChange(2);

            // Then
            verify(mockSeekColorIntensityBar).setProgress(30);
            verify(mockSeekBrightnessBar).setProgress(70);
        }
    }

    @Test
    public void onSettingsChanged_whenSameSettingsForAll_callsUpdateSeekBarsForSelectedColor() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class);
             MockedStatic<Utils> utilsStatic = mockStatic(Utils.class)) {

            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(mockContext)).thenReturn(mockPrefsHelper);
            when(mockReadModeSettings.shouldUseSameIntensityBrightnessForAll()).thenReturn(true);
            when(mockPrefsHelper.getColorIntensity()).thenReturn(40);
            when(mockPrefsHelper.getBrightness()).thenReturn(60);
            when(mockContext.getString(R.string.color_intensity, 40)).thenReturn("40%");
            when(mockContext.getString(R.string.brightness_level, 60)).thenReturn("60%");
            when(mockReadModeSettings.isAutoStartReadMode()).thenReturn(false);
            utilsStatic.when(() -> Utils.isDarkMode(mockContext)).thenReturn(false);

            final SeekBarController controller = new SeekBarController(mockContext, mockRootView, mockReadModeCommand, mockReadModeSettings);

            // When
            controller.onSettingsChanged(Constants.SETTING_OPTIONS.SAME_SETTINGS_FOR_ALL);

            // Then
            verify(mockSeekColorIntensityBar).setProgress(40);
            verify(mockSeekBrightnessBar).setProgress(60);
        }
    }
}