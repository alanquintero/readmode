/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.contilabs.readmode.model.ReadModeSettings;
import com.contilabs.readmode.observer.settings.SettingsSubject;
import com.contilabs.readmode.util.Constants;
import com.contilabs.readmode.R;
import com.contilabs.readmode.util.PrefsHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * SettingsDialog is a custom dialog used to display and manage
 * the user-configurable settings for "Read Mode" in the application.
 *
 * @author Alan Quintero
 */
public class SettingsDialog extends DialogFragment {

    private static final String TAG = SettingsDialog.class.getSimpleName();

    private final @NonNull SettingsSubject settingsSubject;
    private final @NonNull ReadModeSettings readModeSettings;

    public SettingsDialog(final @NonNull SettingsSubject settingsSubject, final @NonNull ReadModeSettings readModeSettings) {
        this.settingsSubject = settingsSubject;
        this.readModeSettings = readModeSettings;
    }

    @Override
    public @NonNull Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "Opening setting dialog");
        final PrefsHelper prefsHelper = PrefsHelper.init(requireContext());

        final LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_settings, null);

        final SwitchMaterial switchSameIntensityBrightness = view.findViewById(R.id.switch_same_intensity_brightness);
        final SwitchMaterial switchAutoStartReadMode = view.findViewById(R.id.switch_auto_start_read_mode);

        final ImageButton infoSameIntensity = view.findViewById(R.id.info_same_intensity_brightness);
        final ImageButton infoAutoStart = view.findViewById(R.id.info_auto_start_read_mode);

        infoAutoStart.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.title_auto_start_read_mode))
                .setMessage(getString(R.string.info_auto_start_read_mode))
                .setPositiveButton(getString(R.string.ok), null)
                .show());

        infoSameIntensity.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.title_same_intensity_brightness))
                .setMessage(getString(R.string.info_same_intensity_brightness))
                .setPositiveButton(getString(R.string.ok), null)
                .show());

        // Load saved values
        switchAutoStartReadMode.setChecked(prefsHelper.getAutoStartReadMode());
        switchSameIntensityBrightness.setChecked(prefsHelper.shouldUseSameIntensityBrightnessForAll());

        // Save changes when toggled
        switchAutoStartReadMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsHelper.saveProperty(Constants.PREF_AUTO_START_READ_MODE, isChecked);
            readModeSettings.setAutoStartReadMode(isChecked);
            settingsSubject.onSettingsChanged(Constants.SETTING_OPTIONS.AUTO_READ_MODE);
        });

        switchSameIntensityBrightness.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsHelper.saveProperty(Constants.PREF_SAME_INTENSITY_BRIGHTNESS_FOR_ALL, isChecked);
            readModeSettings.setShouldUseSameIntensityBrightnessForAll(isChecked);
            settingsSubject.onSettingsChanged(Constants.SETTING_OPTIONS.SAME_SETTINGS_FOR_ALL);
        });

        final AlertDialog settingsDialog = new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.menu_settings))
                .setView(view)
                .setPositiveButton(getString(R.string.done), (dialog, which) -> dialog.dismiss())
                .create();

        // RESET DATA
        final MaterialButton resetButton = view.findViewById(R.id.button_reset_app_data);
        resetButton.setOnClickListener(v -> {
            new AlertDialog.Builder(view.getContext())
                    .setTitle(getString(R.string.title_reset_app_data))
                    .setMessage(getString(R.string.setting_reset_warning_info))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        // Show second confirmation
                        new AlertDialog.Builder(view.getContext())
                                .setTitle(getString(R.string.title_confirm_reset))
                                .setMessage(getString(R.string.setting_reset_warning_confirm))
                                .setPositiveButton(getString(R.string.confirm), (d, w) -> {
                                    prefsHelper.resetAppData();
                                    settingsSubject.onSettingsChanged(Constants.SETTING_OPTIONS.RESET_APP_DATA);
                                    // Dismiss setting dialog
                                    settingsDialog.dismiss();
                                    Toast.makeText(view.getContext(), getString(R.string.setting_reset_confirmation), Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton(getString(R.string.cancel), null)
                                .show();
                    })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
        });


        return settingsDialog;
    }
}
