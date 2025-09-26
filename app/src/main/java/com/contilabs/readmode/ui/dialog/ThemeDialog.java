/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.contilabs.readmode.R;
import com.contilabs.readmode.util.Constants;
import com.contilabs.readmode.util.PrefsHelper;
import com.contilabs.readmode.util.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * ThemeDialog is a custom dialog used to display and manage
 * the user-configurable settings for Dark/Light mode in the application.
 *
 * @author Alan Quintero
 */
public class ThemeDialog extends DialogFragment {

    private static final String TAG = ThemeDialog.class.getSimpleName();

    @Override
    public @NonNull Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "Opening theme dialog");
        final PrefsHelper prefsHelper = PrefsHelper.init(requireContext());
        final Constants.ThemeMode savedTheme = prefsHelper.getTheme();

        final LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_theme, null);

        final RadioButton systemDefaultRadioButton = view.findViewById(R.id.radio_system_default);
        final RadioButton lightRadioButton = view.findViewById(R.id.radio_light);
        final RadioButton darkRadioButton = view.findViewById(R.id.radio_dark);

        // Checked the corresponding option based the saved theme
        if (Constants.ThemeMode.SYSTEM_DEFAULT.equals(savedTheme)) {
            systemDefaultRadioButton.setChecked(true);
        } else if (Constants.ThemeMode.LIGHT.equals(savedTheme)) {

            lightRadioButton.setChecked(true);
        } else if (Constants.ThemeMode.DARK.equals(savedTheme)) {
            darkRadioButton.setChecked(true);
        } else {
            Log.w(TAG, "Invalid theme!");
        }

        // Listener for selected theme
        final RadioGroup themeGroup = view.findViewById(R.id.theme_radio_group);
        // Add listener for selection changes
        themeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_system_default) {
                Log.d(TAG, "System default selected");
                Utils.setAppTheme(Constants.ThemeMode.SYSTEM_DEFAULT);
                prefsHelper.saveProperty(Constants.PREF_THEME, Constants.ThemeMode.SYSTEM_DEFAULT.getValue());

            } else if (checkedId == R.id.radio_light) {
                Log.d(TAG, "Light mode selected");
                Utils.setAppTheme(Constants.ThemeMode.LIGHT);
                prefsHelper.saveProperty(Constants.PREF_THEME, Constants.ThemeMode.LIGHT.getValue());
            } else if (checkedId == R.id.radio_dark) {
                Log.d(TAG, "Dark mode selected");
                Utils.setAppTheme(Constants.ThemeMode.DARK);
                prefsHelper.saveProperty(Constants.PREF_THEME, Constants.ThemeMode.DARK.getValue());
            } else {
                Log.w(TAG, "Invalid theme selected");
            }
        });

        return new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogCustom)
                .setCustomTitle(Utils.createDialogTitle(requireContext(), R.string.title_theme))
                .setView(view)
                .setPositiveButton(getString(R.string.done), (dialog, which) -> dialog.dismiss())
                .create();
    }
}
