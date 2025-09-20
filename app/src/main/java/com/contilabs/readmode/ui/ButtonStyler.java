/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.contilabs.readmode.R;

/**
 * ButtonStyler is responsible for applying consistent visual styles to buttons.
 *
 * @author Alan Quintero
 */
public class ButtonStyler {

    private static final String TAG = ButtonStyler.class.getSimpleName();

    private final @NonNull Context context;
    private final @NonNull View rootView;

    public ButtonStyler(final @NonNull Context context, final @NonNull View rootView) {
        this.context = context;
        this.rootView = rootView;
    }

    /**
     * Updates the visual style and text of the Start/Stop button
     * based on whether Read Mode is currently active.
     */
    public void applyStartStopButtonStyle(final boolean isReadModeOn) {
        final @Nullable Button startStopButton = rootView.findViewById(R.id.startStopButton);
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

    public void customizeCustomColorButton(final @NonNull String customColor) {
        final @Nullable Button customColorButton = rootView.findViewById(R.id.customColorButton);
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
