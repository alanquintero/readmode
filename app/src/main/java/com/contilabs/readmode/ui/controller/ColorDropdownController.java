/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui.controller;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.contilabs.readmode.R;
import com.contilabs.readmode.command.ReadModeCommand;
import com.contilabs.readmode.model.ReadModeSettings;
import com.contilabs.readmode.observer.dropdown.ColorDropdownSubject;
import com.contilabs.readmode.ui.dialog.CustomColorDialog;
import com.contilabs.readmode.util.Constants;
import com.contilabs.readmode.util.PrefsHelper;

/**
 * ColorDropdownController is responsible for applying consistent visual styles the Color dropdown.
 *
 * @author Alan Quintero
 */
public class ColorDropdownController {

    private static final String TAG = ColorDropdownController.class.getSimpleName();

    private final @NonNull Context context;
    private final @NonNull FragmentActivity activity;
    private final @NonNull PrefsHelper prefsHelper;
    private final @NonNull ReadModeCommand readModeCommand;
    private final @NonNull ReadModeSettings readModeSettings;
    private final @NonNull CustomColorDialog customColorDialog;
    private final @NonNull String[] colorNames;
    private final @NonNull ColorDropdownSubject colorDropdownSubject;
    private final @NonNull Spinner colorSpinner;

    public ColorDropdownController(final @NonNull Context context, final @NonNull FragmentActivity activity, final @NonNull View rootView, final @NonNull CustomColorDialog customColorDialog, final @NonNull ColorDropdownSubject colorDropdownSubject, final @NonNull ReadModeCommand readModeCommand, final @NonNull ReadModeSettings readModeSettings, final @NonNull String[] colorNames) {
        this.context = context;
        this.activity = activity;
        this.prefsHelper = PrefsHelper.init(context);
        this.customColorDialog = customColorDialog;
        this.colorDropdownSubject = colorDropdownSubject;
        this.readModeCommand = readModeCommand;
        this.readModeSettings = readModeSettings;
        this.colorNames = colorNames;
        colorSpinner = rootView.findViewById(R.id.colorSpinner);
    }


    /**
     * Initializes and configures the color selection dropdown (Spinner) along with
     * its related UI elements such as intensity and brightness controls.
     * <p>
     * This method sets up a custom adapter for the Spinner, manages item selection
     * (including handling of the "Custom color" option), and ensures that the
     * corresponding SeekBars and labels are updated when a color is selected.
     * </p>
     */
    public void setupColorDropdown() {
        // Flag to ignore initial selection
        final boolean[] isColorDropdownInitializing = {true};
        // Adapter for dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, colorNames) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setBackgroundColor(Constants.BACKGROUND_COLOR_FOR_DROPDOWN_ITEMS[position]); // Color for selected item
                view.setTextColor(Color.BLACK); // Text color
                return view;
            }

            @Override
            public View getDropDownView(final int position, final View convertView, final @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setBackgroundColor(Constants.BACKGROUND_COLOR_FOR_DROPDOWN_ITEMS[position]); // Color for dropdown item
                if (position == Constants.CUSTOM_COLOR_DROPDOWN_POSITION) {
                    int color = Color.parseColor(readModeSettings.getCustomColor());
                    // Calculate brightness
                    int r = Color.red(color);
                    int g = Color.green(color);
                    int b = Color.blue(color);
                    double brightness = (0.299 * r + 0.587 * g + 0.114 * b); // Perceived brightness
                    if (brightness > 200) { // very light color
                        view.setTextColor(Color.BLACK); // fallback
                    } else {
                        view.setTextColor(color);
                    }
                } else {
                    view.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        colorSpinner.setAdapter(adapter);
        colorSpinner.setSelection(readModeSettings.getColorDropdownPosition());

        // Listen for selection
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Position selected: " + position);
                colorDropdownSubject.setCurrentColorDropdownPosition(position);
                readModeSettings.setColorDropdownPosition(position);

                if (isColorDropdownInitializing[0]) {
                    // Ignore the initial selection triggered by setSelection
                    isColorDropdownInitializing[0] = false;
                    Log.d(TAG, "Ignoring the initial selection triggered by setSelection");
                    return;
                }

                prefsHelper.saveProperty(Constants.PREF_COLOR_DROPDOWN, position);

                final String selectedColor = Constants.COLOR_HEX_ARRAY[position];
                if (selectedColor.equals(Constants.COLOR_NONE)) {
                    readModeCommand.stopReadMode();
                } else if (selectedColor.equals(Constants.CUSTOM_COLOR)) {
                    customColorDialog.show(activity.getSupportFragmentManager(), "CustomColorDialogOpenedFromDropdown");
                    adapter.notifyDataSetChanged();
                } else {
                    prefsHelper.saveProperty(Constants.PREF_COLOR, selectedColor);
                    readModeCommand.startReadMode();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
