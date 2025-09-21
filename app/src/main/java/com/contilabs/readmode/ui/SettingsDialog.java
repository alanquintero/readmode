/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.contilabs.readmode.util.Constants;
import com.contilabs.readmode.R;
import com.contilabs.readmode.util.PrefsHelper;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * SettingsDialog is a custom dialog used to display and manage
 * the user-configurable settings for "Read Mode" in the application.
 *
 * @author Alan Quintero
 */
public class SettingsDialog extends DialogFragment {

    private static final String TAG = SettingsDialog.class.getSimpleName();

    private PrefsHelper prefsHelper;

    @Override
    public @NonNull android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.w(TAG, "Opening setting dialog");

        prefsHelper = PrefsHelper.init(requireContext());

        final LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_settings, null);

        final SwitchMaterial switchSameIntensityBrightness = view.findViewById(R.id.switch_same_intensity_brightness);
        final SwitchMaterial switchAutoStartReadMode = view.findViewById(R.id.switch_auto_start_read_mode);

        final ImageButton infoSameIntensity = view.findViewById(R.id.info_same_intensity_brightness);
        final ImageButton infoAutoStart = view.findViewById(R.id.info_auto_start_read_mode);

        infoAutoStart.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.auto_start_read_mode_title))
                .setMessage(getString(R.string.auto_start_read_mode_info))
                .setPositiveButton(getString(R.string.ok), null)
                .show());

        infoSameIntensity.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.same_intensity_brightness_title))
                .setMessage(getString(R.string.same_intensity_brightness_info))
                .setPositiveButton(getString(R.string.ok), null)
                .show());

        // Load saved values
        switchSameIntensityBrightness.setChecked(prefsHelper.shouldUseSameIntensityBrightnessForAll());
        switchAutoStartReadMode.setChecked(prefsHelper.getAutoStartReadMode());

        // Save changes when toggled
        switchAutoStartReadMode.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefsHelper.saveProperty(Constants.PREF_AUTO_START_READ_MODE, isChecked)
        );

        switchSameIntensityBrightness.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefsHelper.saveProperty(Constants.PREF_SAME_INTENSITY_BRIGHTNESS_FOR_ALL, isChecked));

        return new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.settings_menu))
                .setView(view)
                .setPositiveButton(getString(R.string.done), (dialog, which) -> dialog.dismiss())
                .create();
    }

    public interface OnSettingsClosedListener {
        void onSettingsClosed();
    }

    private OnSettingsClosedListener listener;

    public void setOnSettingsClosedListener(OnSettingsClosedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) {
            listener.onSettingsClosed();
        }
    }
}
