/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui.controller;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.FragmentActivity;

import com.contilabs.readmode.R;
import com.contilabs.readmode.command.ReadModeCommand;
import com.contilabs.readmode.model.ReadModeSettings;
import com.contilabs.readmode.observer.dropdown.ColorDropdownSubject;
import com.contilabs.readmode.ui.dialog.CustomColorDialog;
import com.contilabs.readmode.ui.spinner.ColorItem;
import com.contilabs.readmode.ui.spinner.ColorSpinnerAdapter;
import com.contilabs.readmode.util.Constants;
import com.contilabs.readmode.util.PrefsHelper;

import java.util.ArrayList;
import java.util.List;

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
        final List<ColorItem> colorItems = createColorItems();
        final ColorSpinnerAdapter adapter = new ColorSpinnerAdapter(context, colorItems, readModeSettings);
        colorSpinner.setAdapter(adapter);
        customColorDialog.setColorItems(colorItems);
        customColorDialog.setColorSpinnerAdapter(adapter);

        colorSpinner.setSelection(readModeSettings.getColorDropdownPosition());

        setupSelectionListener();
    }

    /**
     * Creates the list of color items for the spinner
     */
    private List<ColorItem> createColorItems() {
        final List<ColorItem> colorItems = new ArrayList<>();
        for (int position = 0; position < colorNames.length; position++) {
            if (position == Constants.CUSTOM_COLOR_DROPDOWN_POSITION) {
                colorItems.add(new ColorItem(colorNames[position], Color.parseColor(readModeSettings.getCustomColor())));
            } else {
                colorItems.add(new ColorItem(colorNames[position], Constants.BACKGROUND_COLOR_FOR_DROPDOWN_ITEMS[position]));
            }
        }
        return colorItems;
    }

    /**
     * Sets up the selection listener for the color spinner
     */
    private void setupSelectionListener() {
        // Flag to ignore initial selection
        final boolean[] isColorDropdownInitializing = {true};

        // Listen for selection
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Position selected: " + position);

                if (isColorDropdownInitializing[0]) {
                    // Ignore the initial selection triggered by setSelection
                    isColorDropdownInitializing[0] = false;
                    Log.d(TAG, "Ignoring the initial selection triggered by setSelection");
                    return;
                }

                handleColorSelection(position, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }

    /**
     * Handles color selection logic - extracted for testability
     *
     * @param position        The selected position
     * @param isUserSelection Whether this was a user selection (vs initial selection)
     */
    public void handleColorSelection(final int position, final boolean isUserSelection) {
        if (!isUserSelection) {
            return;
        }

        readModeSettings.setColorDropdownPosition(position);
        colorDropdownSubject.setCurrentColorDropdownPosition(position);
        prefsHelper.saveProperty(Constants.PREF_COLOR_DROPDOWN, position);

        final String selectedColor = Constants.COLOR_HEX_ARRAY[position];
        if (selectedColor.equals(Constants.CUSTOM_COLOR)) {
            customColorDialog.show(activity.getSupportFragmentManager(), "CustomColorDialogOpenedFromDropdown");
        } else {
            prefsHelper.saveProperty(Constants.PREF_COLOR, selectedColor);
            if (readModeSettings.isReadModeOn()) {
                readModeCommand.updateReadMode();
            }
        }

        if (readModeSettings.isAutoStartReadMode()) {
            readModeCommand.updateReadMode();
        }
    }

    /**
     * Getter for the color spinner - useful for testing
     */
    @VisibleForTesting
    @NonNull
    Spinner getColorSpinner() {
        return colorSpinner;
    }
}
