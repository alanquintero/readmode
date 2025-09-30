/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui.controller;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.ContextCompat;

import com.contilabs.readmode.R;
import com.contilabs.readmode.command.ReadModeCommand;
import com.contilabs.readmode.model.ColorSettings;
import com.contilabs.readmode.model.ReadModeSettings;
import com.contilabs.readmode.observer.customcolor.CustomColorObserver;
import com.contilabs.readmode.observer.dropdown.ColorDropdownObserver;
import com.contilabs.readmode.observer.settings.SettingsObserver;
import com.contilabs.readmode.util.ColorUtils;
import com.contilabs.readmode.util.Constants;
import com.contilabs.readmode.util.PrefsHelper;
import com.contilabs.readmode.util.Utils;

/**
 * SeekBarController is responsible for applying consistent visual styles to all seeks bars.
 *
 * @author Alan Quintero
 */
public class SeekBarController implements ColorDropdownObserver, CustomColorObserver, SettingsObserver {

    private static final String TAG = SeekBarController.class.getSimpleName();

    private final @NonNull Context context;
    private final @NonNull PrefsHelper prefsHelper;
    private final @NonNull ReadModeCommand readModeCommand;
    private final @NonNull ReadModeSettings readModeSettings;
    private final @NonNull TextView labelColorSettings;
    private final @NonNull View lineColorSettings;
    private final @NonNull SeekBar seekColorIntensityBar;
    private final @NonNull TextView colorLevelText;
    private final @NonNull TextView colorLevelPercentageText;
    private final @NonNull TextView brightnessLevelText;
    private final @NonNull SeekBar seekBrightnessBar;
    private final @NonNull TextView brightnessLevelPercentageText;
    private final @NonNull LinearLayout containerLayout;

    public SeekBarController(final @NonNull Context context, final @NonNull View rootView, final @NonNull ReadModeCommand readModeCommand, final @NonNull ReadModeSettings readModeSettings) {
        this.context = context;
        this.prefsHelper = PrefsHelper.init(context);
        this.readModeCommand = readModeCommand;
        this.readModeSettings = readModeSettings;
        this.labelColorSettings = rootView.findViewById(R.id.labelColorSettings);
        this.lineColorSettings = rootView.findViewById(R.id.lineColorSettings);
        this.seekColorIntensityBar = rootView.findViewById(R.id.colorLevelBar);
        this.colorLevelText = rootView.findViewById(R.id.colorLevelText);
        this.colorLevelPercentageText = rootView.findViewById(R.id.colorLevelPercentageText);
        this.brightnessLevelText = rootView.findViewById(R.id.brightnessLevelText);
        this.seekBrightnessBar = rootView.findViewById(R.id.brightnessLevelBar);
        this.brightnessLevelPercentageText = rootView.findViewById(R.id.brightnessLevelPercentageText);
        this.containerLayout = rootView.findViewById(R.id.colorSettingsContainer);
    }

    /**
     * Restores the saved values for the seek bars, setup listeners.
     */
    public void setupSeekBars() {
        // restore saved value
        restoreSavedSelection();
        // setup seek bars
        setupSeekColorIntensityBar();
        setupSeekBrightnessBar();

    }

    /**
     * Restores the saved values for the seek bars
     */
    private void restoreSavedSelection() {
        // Color Intensity
        seekColorIntensityBar.setProgress(readModeSettings.getColorIntensity());
        colorLevelPercentageText.setText(context.getString(R.string.color_intensity, readModeSettings.getColorIntensity()));
        // Brightness
        seekBrightnessBar.setProgress(readModeSettings.getBrightness());
        brightnessLevelPercentageText.setText(context.getString(R.string.brightness_level, readModeSettings.getBrightness()));
    }

    /**
     * Configures the SeekBar that controls the brightness level of the screen filter.
     * <p>
     * This method sets the initial progress based on the current brightness value,
     * listens for user interactions, updates the corresponding label dynamically,
     * and applies the brightness change to the filter when the value is modified.
     * </p>
     */
    private void setupSeekBrightnessBar() {
        seekBrightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                if (fromUser) {
                    readModeSettings.setBrightness(progress);
                    prefsHelper.saveProperty(Constants.PREF_BRIGHTNESS, progress);
                    prefsHelper.tryToSaveColorSettingsProperty(readModeSettings);
                    brightnessLevelPercentageText.setText(context.getString(R.string.brightness_level, readModeSettings.getBrightness()));
                    if (readModeSettings.isAutoStartReadMode()) {
                        readModeCommand.updateReadMode();
                    } else {
                        setContainerColors();
                        if (readModeSettings.isReadModeOn()) {
                            readModeCommand.updateReadMode();
                        }
                    }
                }
            }

            public void onStopTrackingTouch(SeekBar bar) {
            }

            public void onStartTrackingTouch(SeekBar bar) {
            }
        });
    }

    /**
     * Configures the SeekBar that controls the color intensity of the screen filter.
     * <p>
     * This method sets the initial progress based on the current intensity value,
     * listens for user changes, updates the corresponding label dynamically,
     * and triggers the filter update when the value changes.
     * </p>
     */
    private void setupSeekColorIntensityBar() {
        seekColorIntensityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                if (fromUser) {
                    readModeSettings.setColorIntensity(progress);
                    prefsHelper.saveProperty(Constants.PREF_COLOR_INTENSITY, progress);
                    prefsHelper.tryToSaveColorSettingsProperty(readModeSettings);
                    colorLevelPercentageText.setText(context.getString(R.string.color_intensity, readModeSettings.getColorIntensity()));
                    if (readModeSettings.isAutoStartReadMode()) {
                        readModeCommand.updateReadMode();
                    } else {
                        setContainerColors();
                        if (readModeSettings.isReadModeOn()) {
                            readModeCommand.updateReadMode();
                        }
                    }
                }
            }

            public void onStopTrackingTouch(SeekBar bar) {
            }

            public void onStartTrackingTouch(SeekBar bar) {
            }
        });
    }

    /**
     * Sets the seek bars for the selected color from the dropdown.
     */
    public void updateSeekBarsForSelectedColor() {
        Log.d(TAG, "updateSeekBarsForSelectedColor");

        if (readModeSettings.shouldUseSameIntensityBrightnessForAll()) {
            Log.d(TAG, "Using same Intensity and Brightness for All");
            Log.d(TAG, "Selected position: " + readModeSettings.getColorDropdownPosition() + "; colorIntensity: " + readModeSettings.getColorIntensity() + "; brightness: " + readModeSettings.getBrightness());
            // Color Intensity
            seekColorIntensityBar.setProgress(prefsHelper.getColorIntensity());
            colorLevelPercentageText.setText(context.getString(R.string.color_intensity, prefsHelper.getColorIntensity()));
            // Brightness
            seekBrightnessBar.setProgress(prefsHelper.getBrightness());
            brightnessLevelPercentageText.setText(context.getString(R.string.brightness_level, prefsHelper.getBrightness()));
        } else {
            Log.d(TAG, "Using Intensity and Brightness for selected color");
            Log.d(TAG, "Selected position: " + readModeSettings.getColorDropdownPosition());
            // change the brightness and color intensity based on selected color
            final ColorSettings colorSettings = prefsHelper.getColorSettings(readModeSettings.getColorDropdownPosition());
            if (colorSettings != null) {
                Log.d(TAG, "Updating seek bars with saved values: colorIntensity: " + colorSettings.getColorIntensity() + "; brightness: " + colorSettings.getBrightness());
                // Color Intensity
                readModeSettings.setColorIntensity(colorSettings.getColorIntensity());
                seekColorIntensityBar.setProgress(readModeSettings.getColorIntensity());
                colorLevelPercentageText.setText(context.getString(R.string.color_intensity, readModeSettings.getColorIntensity()));
                // Brightness
                readModeSettings.setBrightness(colorSettings.getBrightness());
                seekBrightnessBar.setProgress(readModeSettings.getBrightness());
                brightnessLevelPercentageText.setText(context.getString(R.string.brightness_level, readModeSettings.getBrightness()));
            } else {
                setSeekBarsWithDefaultValues();
            }
        }
        if (!readModeSettings.isAutoStartReadMode()) {
            setContainerColors();
        }
    }

    /**
     * Sets seeks bars using the default values.
     */
    private void setSeekBarsWithDefaultValues() {
        Log.d(TAG, "Updating seek bars with default values");
        // Color Intensity
        seekColorIntensityBar.setProgress(Constants.DEFAULT_COLOR_INTENSITY);
        colorLevelPercentageText.setText(context.getString(R.string.color_intensity, Constants.DEFAULT_COLOR_INTENSITY));
        // Brightness
        seekBrightnessBar.setProgress(Constants.DEFAULT_BRIGHTNESS);
        brightnessLevelPercentageText.setText(context.getString(R.string.brightness_level, Constants.DEFAULT_BRIGHTNESS));
    }

    /**
     * Apply colors to the Container Layout.
     */
    @VisibleForTesting
    void setContainerColors() {
        // Background color
        final int position = readModeSettings.getColorDropdownPosition();
        final int colorIntensity = seekColorIntensityBar.getProgress();
        final int brightness = seekBrightnessBar.getProgress();

        final GradientDrawable drawable = (GradientDrawable) containerLayout.getBackground();

        int backgroundColor;
        if (position == Constants.CUSTOM_COLOR_DROPDOWN_POSITION) {
            backgroundColor = ColorUtils.adjustColor(Color.parseColor(readModeSettings.getCustomColor()), colorIntensity, brightness);
        } else {
            backgroundColor = ColorUtils.adjustColor(Constants.BACKGROUND_COLOR_FOR_DROPDOWN_ITEMS[position], colorIntensity, brightness);
        }
        drawable.setColor(backgroundColor);

        // Text color
        if (position == Constants.CUSTOM_COLOR_DROPDOWN_POSITION) {
            if (ColorUtils.isColorDark(backgroundColor)) {
                setTextColorForAllElementsInColorSettingsContainer(Color.WHITE);
            } else {
                setTextColorForAllElementsInColorSettingsContainer(Color.BLACK);
            }
        } else {
            if (Utils.isDarkMode(context)) {
                setTextColorForAllElementsInColorSettingsContainer(Color.WHITE);
            } else {
                setTextColorForAllElementsInColorSettingsContainer(Color.BLACK);
            }
        }
    }

    private void setTextColorForAllElementsInColorSettingsContainer(final int color) {
        labelColorSettings.setTextColor(color);
        lineColorSettings.setBackgroundColor(color);
        colorLevelText.setTextColor(color);
        colorLevelPercentageText.setTextColor(color);
        brightnessLevelText.setTextColor(color);
        brightnessLevelPercentageText.setTextColor(color);
    }

    /**
     * Apply or removes the background color for the Container Layout.
     */
    @VisibleForTesting
    void handleContainerBackgroundColor() {
        Log.d(TAG, "handleContainerBackgroundColor");
        if (readModeSettings.isAutoStartReadMode()) {
            // reset to initial value
            final GradientDrawable drawable = (GradientDrawable) containerLayout.getBackground();
            drawable.setColor(ContextCompat.getColorStateList(context, R.color.surface_color_settings));
            setTextColorForAllElementsInColorSettingsContainer(Color.BLACK);
        } else {
            setContainerColors();
            if (readModeSettings.isReadModeOn()) {
                readModeCommand.updateReadMode();
            }
        }
    }

    @Override
    public void onColorDropdownPositionChange(final int currentColorDropdownPosition) {
        updateSeekBarsForSelectedColor();
    }

    @Override
    public void onSettingsChanged(final @NonNull Constants.SETTING_OPTIONS setting) {
        switch (setting) {
            case AUTO_READ_MODE:
                handleContainerBackgroundColor();
                break;
            case SAME_SETTINGS_FOR_ALL:
                updateSeekBarsForSelectedColor();
                break;
            default:
                Log.w(TAG, "Invalid setting option");

        }
    }

    @Override
    public void onCustomColorChange(final @NonNull String customColor) {
        handleContainerBackgroundColor();
    }
}
