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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.contilabs.readmode.R;
import com.contilabs.readmode.command.ReadModeCommand;
import com.contilabs.readmode.model.ReadModeSettings;
import com.contilabs.readmode.observer.dropdown.ColorDropdownObserver;
import com.contilabs.readmode.observer.readmode.ReadModeObserver;
import com.contilabs.readmode.ui.CustomColorDialog;
import com.contilabs.readmode.util.Constants;

/**
 * ButtonController is responsible for applying consistent visual styles to buttons.
 *
 * @author Alan Quintero
 */
public class ButtonController implements ReadModeObserver, ColorDropdownObserver {

    private static final String TAG = ButtonController.class.getSimpleName();

    private final @NonNull Context context;
    private final @NonNull FragmentActivity activity;
    private final @NonNull ReadModeCommand readModeCommand;
    private final @NonNull ReadModeSettings readModeSettings;
    private final @NonNull CustomColorDialog customColorDialog;
    private final @NonNull Button customColorButton;
    private final @NonNull Button startStopButton;

    public ButtonController(final @NonNull Context context, final @NonNull FragmentActivity activity, final @NonNull View rootView, final @NonNull ReadModeCommand readModeCommand, final @NonNull ReadModeSettings readModeSettings, final @NonNull CustomColorDialog customColorDialog) {
        this.context = context;
        this.activity = activity;
        this.readModeCommand = readModeCommand;
        this.customColorDialog = customColorDialog;
        this.readModeSettings = readModeSettings;
        this.customColorButton = rootView.findViewById(R.id.customColorButton);
        this.startStopButton = rootView.findViewById(R.id.startStopButton);
    }

    public void setupButtons() {
        // Custom color button
        if (readModeSettings.getColorDropdownPosition() == Constants.CUSTOM_COLOR_DROPDOWN_POSITION) {
            applyCustomColorButtonStyle(readModeSettings.getCustomColor());
            customColorButton.setVisibility(View.VISIBLE);
        }
        // Custom color button listener
        customColorButton.setOnClickListener(v ->
                customColorDialog.show(activity.getSupportFragmentManager(), "CustomColorDialogOpenedFromButton"));
        // Start/stop button
        setupStartStopButton();
    }

    private void setupStartStopButton() {
        applyStartStopButtonStyle(readModeSettings.isReadModeOn());
        // Start/Stop button listener
        startStopButton.setOnClickListener(v -> {
            if (readModeSettings.getColorDropdownPosition() == Constants.NO_COLOR_DROPDOWN_POSITION) {
                Toast.makeText(context, R.string.select_a_color_first, Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Start clicked but no color selected.");
            } else {
                if (readModeSettings.isReadModeOn()) {
                    readModeCommand.stopReadMode();
                } else {
                    readModeCommand.startReadMode();
                }
            }
        });
    }

    /**
     * Updates the visual style and text of the Start/Stop button
     * based on whether Read Mode is currently active.
     */
    public void applyStartStopButtonStyle(final boolean isReadModeOn) {
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
    }

    /**
     * Updates the custom color button's background and text color
     * based on the current custom color. Ensures text is readable
     * by calculating the perceived brightness of the color.
     */

    private void applyCustomColorButtonStyle(final @NonNull String customColor) {
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
    }

    @Override
    public void onReadModeChanged(boolean isReadModeOn) {
        applyStartStopButtonStyle(isReadModeOn);
    }

    @Override
    public void onColorDropdownPositionChange(final int currentColorDropdownPosition) {
        final String selectedColor = Constants.COLOR_HEX_ARRAY[currentColorDropdownPosition];
        switch (selectedColor) {
            case Constants.COLOR_NONE:
                customColorButton.setVisibility(View.GONE);
                startStopButton.setBackgroundColor(context.getResources().getColor(R.color.gray_disabled));
                break;
            case Constants.CUSTOM_COLOR:
                customColorButton.setVisibility(View.VISIBLE);
                applyStartStopButtonStyle(readModeSettings.isReadModeOn());
                break;
            default:
                customColorButton.setVisibility(View.GONE);
                applyStartStopButtonStyle(readModeSettings.isReadModeOn());
                break;
        }
    }
}
