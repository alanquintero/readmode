/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui.controller;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.contilabs.readmode.R;
import com.contilabs.readmode.command.ReadModeCommand;
import com.contilabs.readmode.model.ColorSettings;
import com.contilabs.readmode.model.ReadModeSettings;
import com.contilabs.readmode.observer.dropdown.ColorDropdownObserver;
import com.contilabs.readmode.observer.settings.SettingsObserver;
import com.contilabs.readmode.util.Constants;
import com.contilabs.readmode.util.PrefsHelper;

/**
 * SeekBarController is responsible for applying consistent visual styles to all seeks bars.
 *
 * @author Alan Quintero
 */
public class SeekBarController implements ColorDropdownObserver, SettingsObserver {

    private static final String TAG = SeekBarController.class.getSimpleName();

    private final @NonNull Context context;
    private final @NonNull PrefsHelper prefsHelper;
    private final @NonNull ReadModeCommand readModeCommand;
    private final @NonNull ReadModeSettings readModeSettings;
    private final @NonNull SeekBar seekColorIntensityBar;
    private final @NonNull TextView colorLevelText;
    private final @NonNull SeekBar seekBrightnessBar;
    private final @NonNull TextView brightnessLevelText;

    public SeekBarController(final @NonNull Context context, final @NonNull View rootView, final @NonNull ReadModeCommand readModeCommand, final @NonNull ReadModeSettings readModeSettings) {
        this.context = context;
        this.prefsHelper = PrefsHelper.init(context);
        this.readModeCommand = readModeCommand;
        this.readModeSettings = readModeSettings;
        this.seekColorIntensityBar = rootView.findViewById(R.id.colorLevelBar);
        this.colorLevelText = rootView.findViewById(R.id.colorLevelPercentageText);
        this.seekBrightnessBar = rootView.findViewById(R.id.brightnessLevelBar);
        this.brightnessLevelText = rootView.findViewById(R.id.brightnessLevelPercentageText);
    }

    public void enableSeekBars() {
        Log.d(TAG, "Enabling seek bars");
        seekColorIntensityBar.setEnabled(true);
        seekBrightnessBar.setEnabled(true);
    }

    public void disableSeekBars() {
        Log.d(TAG, "Disabling seek bars");
        seekColorIntensityBar.setEnabled(false);
        seekBrightnessBar.setEnabled(false);
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
        if (readModeSettings.getColorDropdownPosition() == Constants.DEFAULT_COLOR_DROPDOWN_POSITION) {
            setSeekBarsWithDefaultValues();
            disableSeekBars();
        } else {
            enableSeekBars();
            // Color Intensity
            seekColorIntensityBar.setProgress(readModeSettings.getColorIntensity());
            colorLevelText.setText(context.getString(R.string.color_intensity, readModeSettings.getColorIntensity()));
            // Brightness
            seekBrightnessBar.setProgress(readModeSettings.getBrightness());
            brightnessLevelText.setText(context.getString(R.string.brightness_level, readModeSettings.getBrightness()));
        }
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
                    brightnessLevelText.setText(context.getString(R.string.brightness_level, readModeSettings.getBrightness()));
                    readModeCommand.startReadMode();
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
                    colorLevelText.setText(context.getString(R.string.color_intensity, readModeSettings.getColorIntensity()));
                    readModeCommand.startReadMode();
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

        if (readModeSettings.getColorDropdownPosition() == Constants.DEFAULT_COLOR_DROPDOWN_POSITION) {
            Log.d(TAG, "Selected color: NONE");
            setSeekBarsWithDefaultValues();
        } else if (readModeSettings.shouldUseSameIntensityBrightnessForAll()) {
            Log.d(TAG, "Using same Intensity and Brightness for All");
            Log.d(TAG, "Selected position: " + readModeSettings.getColorDropdownPosition() + "; colorIntensity: " + readModeSettings.getColorIntensity() + "; brightness: " + readModeSettings.getBrightness());
            // Color Intensity
            seekColorIntensityBar.setProgress(prefsHelper.getColorIntensity());
            colorLevelText.setText(context.getString(R.string.color_intensity, prefsHelper.getColorIntensity()));
            // Brightness
            seekBrightnessBar.setProgress(prefsHelper.getBrightness());
            brightnessLevelText.setText(context.getString(R.string.brightness_level, prefsHelper.getBrightness()));
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
                colorLevelText.setText(context.getString(R.string.color_intensity, readModeSettings.getColorIntensity()));
                // Brightness
                readModeSettings.setBrightness(colorSettings.getBrightness());
                seekBrightnessBar.setProgress(readModeSettings.getBrightness());
                brightnessLevelText.setText(context.getString(R.string.brightness_level, readModeSettings.getBrightness()));
            } else {
                setSeekBarsWithDefaultValues();
            }
        }
    }

    /**
     * Sets seeks bars using the default values.
     */
    private void setSeekBarsWithDefaultValues() {
        Log.d(TAG, "Updating seek bars with default values");
        // Color Intensity
        seekColorIntensityBar.setProgress(Constants.DEFAULT_COLOR_INTENSITY);
        colorLevelText.setText(context.getString(R.string.color_intensity, Constants.DEFAULT_COLOR_INTENSITY));
        // Brightness
        seekBrightnessBar.setProgress(Constants.DEFAULT_BRIGHTNESS);
        brightnessLevelText.setText(context.getString(R.string.brightness_level, Constants.DEFAULT_BRIGHTNESS));
    }

    @Override
    public void onColorDropdownPositionChange(final int currentColorDropdownPosition) {
        final String selectedColor = Constants.COLOR_HEX_ARRAY[currentColorDropdownPosition];
        if (selectedColor.equals(Constants.COLOR_NONE)) {
            disableSeekBars();
        } else {
            enableSeekBars();
        }
        updateSeekBarsForSelectedColor();
    }

    @Override
    public void shouldUseSameIntensityBrightnessForAllChanged() {
        updateSeekBarsForSelectedColor();
    }
}
