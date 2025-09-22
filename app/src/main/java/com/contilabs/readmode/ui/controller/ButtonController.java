/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui.controller;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.contilabs.readmode.R;
import com.contilabs.readmode.command.ReadModeCommand;
import com.contilabs.readmode.model.ReadModeSettings;
import com.contilabs.readmode.ui.CustomColorDialog;
import com.contilabs.readmode.util.Constants;

/**
 * ButtonController is responsible for applying consistent visual styles to buttons.
 *
 * @author Alan Quintero
 */
public class ButtonController {

    private static final String TAG = ButtonController.class.getSimpleName();

    private final @NonNull Context context;

    private final @NonNull CustomColorDialog customColorDialog;

    private final @Nullable Button customColorButton;

    private final @Nullable Button startStopButton;

    public ButtonController(final @NonNull Context context, final @NonNull View rootView, final @NonNull CustomColorDialog customColorDialog) {
        this.context = context;
        this.customColorDialog = customColorDialog;
        this.customColorButton = rootView.findViewById(R.id.customColorButton);
        this.startStopButton = rootView.findViewById(R.id.startStopButton);
    }

    public void setupButtons(final @NonNull ReadModeCommand readModeCommand, final @NonNull ReadModeSettings readModeSettings) {
        // Custom color button
        if (customColorButton != null) {
            if (readModeSettings.getColorDropdownPosition() == Constants.CUSTOM_COLOR_DROPDOWN_POSITION) {
                applyCustomColorButtonStyle(readModeSettings.getCustomColor());
                customColorButton.setVisibility(View.VISIBLE);
            }
            // Custom color button listener
            customColorButton.setOnClickListener(v -> customColorDialog.openCustomColorDialog());
        }
        // Start/stop button
        setupStartStopButton(readModeCommand, readModeSettings);
    }

    private void setupStartStopButton(final @NonNull ReadModeCommand readModeCommand, final @NonNull ReadModeSettings readModeSettings) {
        if (startStopButton != null) {
            applyStartStopButtonStyle(readModeSettings.isReadModeOn());
            // Start/Stop button listener
            startStopButton.setOnClickListener(v -> {
                if (readModeSettings.getColorDropdownPosition() == Constants.NO_COLOR_DROPDOWN_POSITION) {
                    Toast.makeText(context, R.string.select_a_color_first, Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Start clicked but no color selected.");
                } else {
                    if (readModeSettings.isReadModeOn()) {
                        readModeCommand.stopReadMode(readModeSettings);
                    } else {
                        readModeCommand.startReadMode(readModeSettings);
                    }
                }
            });
        }
    }

    /**
     * Updates the visual style and text of the Start/Stop button
     * based on whether Read Mode is currently active.
     */
    public void applyStartStopButtonStyle(final boolean isReadModeOn) {
        if (startStopButton != null) {
            if (isReadModeOn) {
                Log.d(TAG, "Applying style to stop button");
                startStopButton.setText(R.string.stop);
                startStopButton.setBackgroundTintList(
                        ContextCompat.getColorStateList(context, R.color.stop_color));
            } else {
                Log.d(TAG, "Applying style start button");
                startStopButton.setText(R.string.start);
                startStopButton.setBackgroundTintList(
                        ContextCompat.getColorStateList(context, R.color.start_color));
            }
        } else {
            Log.d(TAG, "Start/Stop button not found!");
        }
    }

    /**
     * Updates the custom color button's background and text color
     * based on the current custom color. Ensures text is readable
     * by calculating the perceived brightness of the color.
     */

    private void applyCustomColorButtonStyle(final @NonNull String customColor) {
        if (customColorButton != null) {
            Log.d(TAG, "Applying style to custom color button");
            // Set background color
            customColorButton.setBackgroundColor(Color.parseColor(customColor));
            // Set text color
            int color = Color.parseColor(customColor);
            // Calculate brightness
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            double brightness = (0.299 * r + 0.587 * g + 0.114 * b); // Perceived brightness
            if (brightness > 200) { // very light color
                customColorButton.setTextColor(Color.BLACK); // fallback
            } else {
                customColorButton.setTextColor(Color.WHITE);
            }
        } else {
            Log.d(TAG, "Custom color button not found!");
        }
    }

}
