/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.contilabs.readmode.R;
import com.contilabs.readmode.command.ReadModeCommand;
import com.contilabs.readmode.model.ReadModeSettings;
import com.contilabs.readmode.observer.customcolor.CustomColorSubject;
import com.contilabs.readmode.ui.spinner.ColorItem;
import com.contilabs.readmode.ui.spinner.ColorSpinnerAdapter;
import com.contilabs.readmode.util.Constants;
import com.contilabs.readmode.util.PrefsHelper;
import com.contilabs.readmode.util.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

/**
 * CustomColorDialog is a custom dialog used to display and manage
 * the user-configurable settings for choosing a custom color for the Read Mode.
 *
 * @author Alan Quintero
 */
public class CustomColorDialog extends DialogFragment {

    private static final String TAG = CustomColorDialog.class.getSimpleName();

    private final @NonNull ReadModeCommand readModeCommand;
    private final @NonNull ReadModeSettings readModeSettings;
    private final @NonNull CustomColorSubject customColorSubject;

    private List<ColorItem> colorItems;
    private ColorSpinnerAdapter colorSpinnerAdapter;

    public CustomColorDialog(final @NonNull ReadModeCommand readModeCommand, final @NonNull ReadModeSettings readModeSettings, final @NonNull CustomColorSubject customColorSubject) {
        this.readModeCommand = readModeCommand;
        this.readModeSettings = readModeSettings;
        this.customColorSubject = customColorSubject;
    }

    public void setColorItems(final List<ColorItem> colorItems) {
        this.colorItems = colorItems;
    }

    public void setColorSpinnerAdapter(final ColorSpinnerAdapter colorSpinnerAdapter) {
        this.colorSpinnerAdapter = colorSpinnerAdapter;
    }

    /**
     * Opens the custom color picker dialog.
     */
    @Override
    public @NonNull android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return openCustomColorDialog();
    }

    private @NonNull android.app.Dialog openCustomColorDialog() {
        Log.i(TAG, "Opening custom color dialog");
        final PrefsHelper prefsHelper = PrefsHelper.init(requireContext());
        readModeCommand.pauseReadMode();

        // Inflate the dialog layout
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_color_picker, null);
        final View colorPreview = dialogView.findViewById(R.id.colorPreview);
        final SeekBar seekRed = dialogView.findViewById(R.id.seekRed);
        final SeekBar seekGreen = dialogView.findViewById(R.id.seekGreen);
        final SeekBar seekBlue = dialogView.findViewById(R.id.seekBlue);

        // Initial color
        Log.d(TAG, "Loading initial custom color: " + readModeSettings.getCustomColor());
        final int initialColor = Color.parseColor(readModeSettings.getCustomColor());
        final int initialRed = Color.red(initialColor);
        final int initialGreen = Color.green(initialColor);
        final int initialBlue = Color.blue(initialColor);
        seekRed.setProgress(initialRed);
        seekGreen.setProgress(initialGreen);
        seekBlue.setProgress(initialBlue);
        colorPreview.setBackgroundColor(initialColor);
        final String initialColorHex = String.format(Constants.COLOR_HEX_FORMAT, initialRed, initialGreen, initialBlue);
        prefsHelper.saveProperty(Constants.PREF_COLOR, Constants.CUSTOM_COLOR);
        prefsHelper.saveProperty(Constants.PREF_CUSTOM_COLOR, initialColorHex);

        // Update preview when SeekBars change
        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final int color = Color.rgb(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress());
                colorPreview.setBackgroundColor(color);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };

        seekRed.setOnSeekBarChangeListener(listener);
        seekGreen.setOnSeekBarChangeListener(listener);
        seekBlue.setOnSeekBarChangeListener(listener);

        // Build the dialog
        return new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogCustom)
                .setCustomTitle(Utils.createDialogTitle(requireContext(), R.string.choose_custom_color))
                .setView(dialogView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    final int currentRed = seekRed.getProgress();
                    final int currentGreen = seekGreen.getProgress();
                    final int currentBlue = seekBlue.getProgress();
                    final String chosenColorHex = String.format(Constants.COLOR_HEX_FORMAT, currentRed, currentGreen, currentBlue);
                    prefsHelper.saveProperty(Constants.PREF_COLOR, Constants.CUSTOM_COLOR);
                    prefsHelper.saveProperty(Constants.PREF_CUSTOM_COLOR, chosenColorHex);

                    // update preferences for custom color
                    Log.d(TAG, "Updating custom color to: " + readModeSettings.getCustomColor());
                    readModeSettings.setCustomColor(chosenColorHex);
                    customColorSubject.setCustomColor(chosenColorHex);
                    if (colorItems != null && colorSpinnerAdapter != null && colorItems.size() > Constants.CUSTOM_COLOR_DROPDOWN_POSITION) {
                        Log.d(TAG, "Updating custom color to in colorItems and notifying DataSet Changed...");
                        colorItems.get(Constants.CUSTOM_COLOR_DROPDOWN_POSITION).setIconColor(Color.parseColor(chosenColorHex));
                        colorSpinnerAdapter.notifyDataSetChanged();
                    }

                    // resume read mode
                    readModeCommand.resumeReadMode();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> readModeCommand.resumeReadMode()).show();
    }
}
