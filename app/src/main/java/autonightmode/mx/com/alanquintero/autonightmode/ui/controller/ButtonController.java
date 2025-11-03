/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode.ui.controller;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import autonightmode.mx.com.alanquintero.autonightmode.R;
import autonightmode.mx.com.alanquintero.autonightmode.command.ReadModeCommand;
import autonightmode.mx.com.alanquintero.autonightmode.model.ReadModeSettings;
import autonightmode.mx.com.alanquintero.autonightmode.observer.customcolor.CustomColorObserver;
import autonightmode.mx.com.alanquintero.autonightmode.observer.dropdown.ColorDropdownObserver;
import autonightmode.mx.com.alanquintero.autonightmode.observer.readmode.ReadModeObserver;
import autonightmode.mx.com.alanquintero.autonightmode.ui.dialog.CustomColorDialog;
import autonightmode.mx.com.alanquintero.autonightmode.util.ColorUtils;
import autonightmode.mx.com.alanquintero.autonightmode.util.Constants;

/**
 * ButtonController is responsible for applying consistent visual styles to buttons.
 *
 * @author Alan Quintero
 */
public class ButtonController implements ReadModeObserver, ColorDropdownObserver, CustomColorObserver {

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
            if (readModeSettings.isReadModeOn()) {
                readModeCommand.stopReadMode();
            } else {
                readModeCommand.startReadMode();
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
                    ContextCompat.getColorStateList(context, R.color.button_stop));
        } else {
            Log.d(TAG, "Applying style start button");
            startStopButton.setText(R.string.start);
            startStopButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(context, R.color.button_start));
        }
    }

    /**
     * Updates the custom color button's background and text color
     * based on the current custom color. Ensures text is readable
     * by calculating the perceived brightness of the color.
     */
    @VisibleForTesting
    void applyCustomColorButtonStyle(final @NonNull String customColor) {
        Log.d(TAG, "Applying style to custom color button, customColor: " + customColor);
        // Set background color
        customColorButton.setBackgroundTintList(ColorStateList.valueOf(ColorUtils.toButtonCompatibleColor(customColor)));

        // Set text color
        int color = Color.parseColor(customColor);
        if (ColorUtils.isVeryLightColor(color)) { // very light color
            customColorButton.setTextColor(Color.BLACK); // fallback
        } else {
            customColorButton.setTextColor(Color.WHITE);
        }
    }

    @Override
    public void onReadModeChanged(final boolean isReadModeOn) {
        applyStartStopButtonStyle(isReadModeOn);
    }

    @Override
    public void onColorDropdownPositionChange(final int currentColorDropdownPosition) {
        final String selectedColor = Constants.COLOR_HEX_ARRAY[currentColorDropdownPosition];
        Log.e(TAG, "Selected color: " + selectedColor);
        if (selectedColor.equals(Constants.CUSTOM_COLOR)) {
            customColorButton.setVisibility(View.VISIBLE);
            applyStartStopButtonStyle(readModeSettings.isReadModeOn());
        } else {
            customColorButton.setVisibility(View.GONE);
            applyStartStopButtonStyle(readModeSettings.isReadModeOn());
        }
    }

    @Override
    public void onCustomColorChange(final @NonNull String customColor) {
        applyCustomColorButtonStyle(customColor);
    }
}
