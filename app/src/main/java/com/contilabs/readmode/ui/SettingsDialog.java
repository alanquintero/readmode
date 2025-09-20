/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.contilabs.readmode.util.Constants;
import com.contilabs.readmode.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsDialog extends DialogFragment {

    @Override
    public @NonNull android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);

        final LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_settings, null);

        final SwitchMaterial switchSameIntensityBrightness = view.findViewById(R.id.switch_same_intensity_brightness);
        final SwitchMaterial switchAutoStartReadMode = view.findViewById(R.id.switch_auto_start_read_mode);

        final ImageButton infoSameIntensity = view.findViewById(R.id.info_same_intensity_brightness);
        final ImageButton infoAutoStart = view.findViewById(R.id.info_auto_start_read_mode);

        infoSameIntensity.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.same_intensity_brightness_title))
                    .setMessage(getString(R.string.same_intensity_brightness_info))
                    .setPositiveButton(getString(R.string.ok), null)
                    .show();
        });

        infoAutoStart.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.auto_start_read_mode_title))
                    .setMessage(getString(R.string.auto_start_read_mode_info))
                    .setPositiveButton(getString(R.string.ok), null)
                    .show();
        });

        // Load saved values
        switchSameIntensityBrightness.setChecked(sharedPreferences.getBoolean(Constants.PREF_SAME_INTENSITY_BRIGHTNESS_FOR_ALL, Constants.DEFAULT_SAME_INTENSITY_BRIGHTNESS_FOR_ALL));
        switchAutoStartReadMode.setChecked(sharedPreferences.getBoolean(Constants.PREF_AUTO_START_READ_MODE, Constants.DEFAULT_AUTO_START_READ_MODE));

        // Save changes when toggled
        switchSameIntensityBrightness.setOnCheckedChangeListener((buttonView, isChecked) ->
                sharedPreferences.edit().putBoolean(Constants.PREF_SAME_INTENSITY_BRIGHTNESS_FOR_ALL, isChecked).apply()
        );

        switchAutoStartReadMode.setOnCheckedChangeListener((buttonView, isChecked) ->
                sharedPreferences.edit().putBoolean(Constants.PREF_AUTO_START_READ_MODE, isChecked).apply()
        );

        return new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.settings_menu))
                .setView(view)
                .setPositiveButton(getString(R.string.done), (dialog, which) -> dialog.dismiss())
                .create();
    }
}
