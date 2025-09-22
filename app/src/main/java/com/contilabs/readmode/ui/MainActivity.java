/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.contilabs.readmode.command.GeneralReadModeCommand;
import com.contilabs.readmode.command.SettingsReadModeCommand;
import com.contilabs.readmode.model.ReadModeSettings;
import com.contilabs.readmode.ui.util.ButtonStyler;
import com.contilabs.readmode.ui.util.SeekBarsStyler;
import com.contilabs.readmode.ui.util.TextViewStyler;
import com.contilabs.readmode.util.Constants;
import com.contilabs.readmode.R;
import com.contilabs.readmode.util.PrefsHelper;


/**
 * MainActivity controls the UI for Read Mode.
 * Users can select colors, brightness, intensity, and start/stop the read mode overlay.
 *
 * @author Alan Quintero
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private PrefsHelper prefsHelper;
    private ButtonStyler buttonStyler;
    private SeekBarsStyler seekBarsStyler;
    private TextViewStyler textViewStyler;
    private ReadModeSettings readModeSettings;
    private CustomColorDialog customColorDialog;
    private GeneralReadModeCommand generalReadModeCommand;
    private SettingsReadModeCommand settingsReadModeCommand;

    // Current state variables
    @VisibleForTesting
    int currentColorDropdownPosition = Constants.DEFAULT_COLOR_DROPDOWN_POSITION;

    // ---------------- Colors ----------------
    String[] colors = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register Activity Result launcher for overlay permission
        final ActivityResultLauncher<Intent> overlayPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                        // Permission not granted
                        Log.w(TAG, "No overlay permission granted, finishing the app");
                        finish();
                    } else {
                        // Permission granted
                        initUI();
                    }
                }
        );

        // Check overlay permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Log.i(TAG, "Requesting overlay permission");
            final Intent intent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())
            );
            overlayPermissionLauncher.launch(intent);
        } else {
            // Permission already granted or API < 23
            initUI();
        }
    }

    /**
     * Initializes the user interface components and sets up their behavior.
     * <p>
     * This method handles:
     * <ul>
     *     <li>Finding views by ID (SeekBars, TextViews, Buttons, Spinner).</li>
     *     <li>Setting up color dropdown, SeekBars for color intensity and brightness.</li>
     *     <li>Configuring event listeners for user interactions (dropdown selection, button clicks, SeekBar changes).</li>
     *     <li>Applying saved preferences to restore previous app state.</li>
     *     <li>Enabling or disabling UI elements based on selected color and read mode status.</li>
     * </ul>
     */
    private void initUI() {
        Log.d(TAG, "initUI");
        setContentView(R.layout.activity_main);

        Log.d(TAG, "init methods...");
        setupStatusBarColor();
        initSharedPreferences();
        initColors();

        Log.d(TAG, "init classes...");
        final @NonNull View rootView = findViewById(android.R.id.content);
        generalReadModeCommand = new GeneralReadModeCommand(this);
        settingsReadModeCommand = new SettingsReadModeCommand(this);
        customColorDialog = new CustomColorDialog(this, generalReadModeCommand, readModeSettings);
        buttonStyler = new ButtonStyler(this, rootView, customColorDialog);
        seekBarsStyler = new SeekBarsStyler(this, rootView);
        textViewStyler = new TextViewStyler(this, rootView);

        // UI Components
        final ImageView menu = findViewById(R.id.bannerMenu);
        final Button startStopButton = findViewById(R.id.startStopButton);
        final Spinner colorSpinner = findViewById(R.id.colorSpinner);
        final Button customColorButton = findViewById(R.id.customColorButton);

        // Disable SeekBars initially until a color is selected
        seekBarsStyler.disableSeekBars();

        setupColorDropdown(colorSpinner, customColorButton, startStopButton);

        // Restore saved selection
        if (readModeSettings.getColorDropdownPosition() >= 0) {
            currentColorDropdownPosition = readModeSettings.getColorDropdownPosition();
            colorSpinner.setSelection(readModeSettings.getColorDropdownPosition());

        }
        textViewStyler.setupTextViews(readModeSettings, colors);
        seekBarsStyler.setupSeekBars(settingsReadModeCommand, readModeSettings);
        buttonStyler.setupButtons(generalReadModeCommand, readModeSettings);

        // ---------------------- Listeners ------------------------

        // Menu listener
        setupMenuListener(menu);

        Log.i(TAG, "UI initialized successfully.");
    }


    // ---------------------- Applying styles ------------------------

    /**
     * Sets up the status bar color based on the current system theme (light or dark mode).
     */
    private void setupStatusBarColor() {
        int lightModeColor = getResources().getColor(R.color.status_bar_light); // light background
        int darkModeColor = getResources().getColor(R.color.status_bar_dark);  // dark gray, not pure black

        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;

        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            // Light mode
            getWindow().setStatusBarColor(lightModeColor);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } else {
            // Dark mode
            getWindow().setStatusBarColor(darkModeColor);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Remove light status bar flag to make icons white
                getWindow().getDecorView().setSystemUiVisibility(0);
            }
        }
    }


    // ---------------------- Init methods ------------------------

    /**
     * Initialize SharedPreferences and load color settings map.
     */
    private void initSharedPreferences() {
        prefsHelper = PrefsHelper.init(this);
        readModeSettings = new ReadModeSettings();
        readModeSettings.setIsReadModeOn(prefsHelper.isReadModeOn());
        readModeSettings.setColorDropdownPosition(prefsHelper.getColorDropdownPosition());
        readModeSettings.setCustomColor(prefsHelper.getCustomColor());
        readModeSettings.setColorIntensity(prefsHelper.getColorIntensity());
        readModeSettings.setBrightness(prefsHelper.getBrightness());
        readModeSettings.setAutoStartReadMode(prefsHelper.getAutoStartReadMode());
        readModeSettings.setShouldUseSameIntensityBrightnessForAll(prefsHelper.shouldUseSameIntensityBrightnessForAll());

        prefsHelper.initPrefColorSettingsMap();
    }


    /**
     * Initialize the color names for the dropdown.
     */
    private void initColors() {
        // Color names for the colors dropdown
        final String colorNone = getString(R.string.color_none);
        final String colorSoftBeige = getString(R.string.color_soft_beige);
        final String colorLightGray = getString(R.string.color_light_gray);
        final String colorPaleYellow = getString(R.string.color_pale_yellow);
        final String colorWarmSepia = getString(R.string.color_warm_sepia);
        final String colorSoftBlue = getString(R.string.color_soft_blue);
        final String colorCustom = getString(R.string.color_custom);
        colors = new String[]{colorNone, colorSoftBeige, colorLightGray, colorPaleYellow, colorWarmSepia, colorSoftBlue, colorCustom};
    }


    // ---------------------- UI Components ------------------------

    /**
     * Initializes and configures the color selection dropdown (Spinner) along with
     * its related UI elements such as intensity and brightness controls.
     * <p>
     * This method sets up a custom adapter for the Spinner, manages item selection
     * (including handling of the "Custom color" option), and ensures that the
     * corresponding SeekBars and labels are updated when a color is selected.
     * </p>
     */
    private void setupColorDropdown(final @NonNull Spinner colorSpinner, final @NonNull Button customColorButton, final @NonNull Button startStopButton) {
        // Flag to ignore initial selection
        final boolean[] isColorDropdownInitializing = {true};
        // Adapter for dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, colors) {
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

        // Listen for selection
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentColorDropdownPosition = position;
                if (currentColorDropdownPosition == Constants.NO_COLOR_DROPDOWN_POSITION) {
                    // No color option
                    // Disabling the seek bars
                    seekBarsStyler.disableSeekBars();
                } else {
                    // Enabling the seek bars
                    seekBarsStyler.enableSeekBars();
                }

                if (isColorDropdownInitializing[0]) {
                    // Ignore the initial selection triggered by setSelection
                    isColorDropdownInitializing[0] = false;
                    return;
                }

                prefsHelper.saveProperty(Constants.PREF_COLOR_DROPDOWN, currentColorDropdownPosition);
                readModeSettings.setColorDropdownPosition(currentColorDropdownPosition);

                final String selectedColor = Constants.COLOR_HEX_ARRAY[currentColorDropdownPosition];
                textViewStyler.setColorSettingsText(readModeSettings, colors);
                seekBarsStyler.updateSeekBarsForSelectedColor(readModeSettings);

                if (selectedColor.equals(Constants.COLOR_NONE)) {
                    customColorButton.setVisibility(View.GONE);
                    generalReadModeCommand.stopReadMode(readModeSettings);
                    startStopButton.setBackgroundColor(getResources().getColor(R.color.gray_disabled));
                } else if (selectedColor.equals(Constants.CUSTOM_COLOR)) {
                    customColorButton.setVisibility(View.VISIBLE);
                    customColorDialog.openCustomColorDialog();
                    adapter.notifyDataSetChanged();
                } else {
                    // Apply selected color
                    prefsHelper.saveProperty(Constants.PREF_COLOR, selectedColor);
                    customColorButton.setVisibility(View.GONE);
                    settingsReadModeCommand.startReadMode(readModeSettings);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupMenuListener(final @NonNull ImageView menu) {
        menu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_settings) {
                    final SettingsDialog dialog = new SettingsDialog();
                    // Code to run after dialog is closed
                    dialog.setOnSettingsClosedListener(this::reloadSettings);
                    dialog.show(getSupportFragmentManager(), "settingsDialog");
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    private void reloadSettings() {
        Log.d(TAG, "reloading Settings");
        readModeSettings.setAutoStartReadMode(prefsHelper.getAutoStartReadMode());
        readModeSettings.setShouldUseSameIntensityBrightnessForAll(prefsHelper.shouldUseSameIntensityBrightnessForAll());
        textViewStyler.setColorSettingsText(readModeSettings, colors);
        seekBarsStyler.updateSeekBarsForSelectedColor(readModeSettings);
    }

    // ---------------------- Activity Lifecycle ------------------------

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        /*
        Need to call initUI method when system changes between dark and light mode
         */
        if ((newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            Log.d(TAG, "Dark mode enabled");
            initUI();
        } else {
            Log.d(TAG, "Light mode enabled");
            initUI();
        }
    }

    @Override
    protected void onDestroy() {
        generalReadModeCommand.stopReadMode(readModeSettings);
        super.onDestroy();
        Log.i(TAG, "Activity destroyed.");
    }
}
