/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui.controller;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.contilabs.readmode.R;
import com.contilabs.readmode.command.ReadModeCommand;
import com.contilabs.readmode.model.ReadModeSettings;
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

    private final @NonNull PrefsHelper prefsHelper;
    private final @NonNull Spinner colorSpinner;

    public ColorDropdownController(final @NonNull Context context, final @NonNull View rootView) {
        this.context = context;
        this.prefsHelper = PrefsHelper.init(context);
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
    public void setupColorDropdown(final @NonNull ReadModeCommand readModeCommand, final @NonNull ReadModeSettings readModeSettings, final @NonNull String[] colors) {
        // Flag to ignore initial selection
        final boolean[] isColorDropdownInitializing = {true};
        // Adapter for dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, colors) {
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

                readModeSettings.setColorDropdownPosition(position);
                if (position == Constants.NO_COLOR_DROPDOWN_POSITION) {
                    // No color option
                    // Disabling the seek bars
                    // TODO listeners
                    // seekBarsStyler.disableSeekBars();
                } else {
                    // Enabling the seek bars
                    // TODO listeners
                    // seekBarsStyler.enableSeekBars();
                }

                if (isColorDropdownInitializing[0]) {
                    // Ignore the initial selection triggered by setSelection
                    isColorDropdownInitializing[0] = false;
                    return;
                }

                prefsHelper.saveProperty(Constants.PREF_COLOR_DROPDOWN, position);

                final String selectedColor = Constants.COLOR_HEX_ARRAY[position];
                // TODO listener
                // textViewStyler.setColorSettingsText(readModeSettings, colors);
                // seekBarsStyler.updateSeekBarsForSelectedColor(readModeSettings);

                if (selectedColor.equals(Constants.COLOR_NONE)) {
                    // TODO listener
                    // customColorButton.setVisibility(View.GONE);
                    // startStopButton.setBackgroundColor(getResources().getColor(R.color.gray_disabled));
                    readModeCommand.stopReadMode(readModeSettings);
                } else if (selectedColor.equals(Constants.CUSTOM_COLOR)) {
                    // customColorButton.setVisibility(View.VISIBLE);
                    // customColorDialog.openCustomColorDialog();
                    adapter.notifyDataSetChanged();
                } else {
                    // Apply selected color
                    // TODO listener
                    prefsHelper.saveProperty(Constants.PREF_COLOR, selectedColor);
                    // customColorButton.setVisibility(View.GONE);
                    readModeCommand.startReadMode(readModeSettings);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
