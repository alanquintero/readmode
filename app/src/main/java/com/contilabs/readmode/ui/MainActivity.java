/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui;

import android.app.AlertDialog;
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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    private GeneralReadModeCommand generalReadModeCommand;
    private SettingsReadModeCommand settingsReadModeCommand;

    // Current state variables
    @VisibleForTesting
    int currentColorDropdownPosition = Constants.DEFAULT_COLOR_DROPDOWN_POSITION;

    // ---------------- Colors ----------------
    // Color hex corresponding to each dropdown item
    private final String[] colorHex = {Constants.COLOR_NONE, Constants.COLOR_SOFT_BEIGE, Constants.COLOR_LIGHT_GRAY, Constants.COLOR_PALE_YELLOW, Constants.COLOR_WARM_SEPIA, Constants.COLOR_SOFT_BLUE, Constants.CUSTOM_COLOR};
    // Background color to each dropdown item for better visual distinction
    private final int[] backgroundColorForDropdownItems = {
            Color.TRANSPARENT,            // NONE
            Color.parseColor(Constants.COLOR_SOFT_BEIGE),  // Soft Beige
            Color.parseColor(Constants.COLOR_LIGHT_GRAY),  // Light Gray
            Color.parseColor(Constants.COLOR_PALE_YELLOW),  // Pale Yellow
            Color.parseColor(Constants.COLOR_WARM_SEPIA),  // Warm Sepia
            Color.parseColor(Constants.COLOR_SOFT_BLUE),  // Soft Blue
            Color.WHITE             // CUSTOM
    };
    // Color name corresponding to each dropdown item
    String[] colors = {};
    private final int CUSTOM_DROPDOWN_POSITION = backgroundColorForDropdownItems.length - 1;

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
        buttonStyler = new ButtonStyler(this, rootView);
        seekBarsStyler = new SeekBarsStyler(this, rootView);
        textViewStyler = new TextViewStyler(this, rootView);
        generalReadModeCommand = new GeneralReadModeCommand(this, rootView);
        settingsReadModeCommand = new SettingsReadModeCommand(this, rootView);

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
            textViewStyler.setColorSettingsText(readModeSettings, colors);
            if (readModeSettings.getColorDropdownPosition() == CUSTOM_DROPDOWN_POSITION) {
                buttonStyler.customizeCustomColorButton(readModeSettings.getCustomColor());
                customColorButton.setVisibility(View.VISIBLE);
            }
        }

        // Restore SeekBars values
        seekBarsStyler.restoreSeekBarValues(readModeSettings);

        // Apply style to buttons
        buttonStyler.applyStartStopButtonStyle(readModeSettings.isReadModeOn());
        buttonStyler.customizeCustomColorButton(readModeSettings.getCustomColor());

        // ---------------------- Listeners ------------------------

        // Menu listener
        setupMenuListener(menu);

        // SeekBar listeners
        seekBarsStyler.setupSeekColorIntensityBar(settingsReadModeCommand, readModeSettings);
        seekBarsStyler.setupSeekBrightnessBar(settingsReadModeCommand, readModeSettings);

        // Custom color button listener
        customColorButton.setOnClickListener(v -> openCustomColorDialog());

        // Start/Stop button listener
        startStopButton.setOnClickListener(v -> {
            if (currentColorDropdownPosition == Constants.NO_COLOR_DROPDOWN_POSITION) {
                Toast.makeText(this, R.string.select_a_color_first, Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Start clicked but no color selected.");
            } else {
                if (readModeSettings.isReadModeOn()) {
                    generalReadModeCommand.stopReadMode(readModeSettings);
                } else {
                    readModeSettings.setColorIntensity(seekBarsStyler.getCurrentColorIntensity());
                    readModeSettings.setBrightness(seekBarsStyler.getCurrentBrightness());
                    generalReadModeCommand.startReadMode(readModeSettings);
                }
            }
        });

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
                view.setBackgroundColor(backgroundColorForDropdownItems[position]); // Color for selected item
                view.setTextColor(Color.BLACK); // Text color
                return view;
            }

            @Override
            public View getDropDownView(final int position, final View convertView, final @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setBackgroundColor(backgroundColorForDropdownItems[position]); // Color for dropdown item
                if (position == CUSTOM_DROPDOWN_POSITION) {
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

                final String selectedColor = colorHex[currentColorDropdownPosition];
                textViewStyler.setColorSettingsText(readModeSettings, colors);
                seekBarsStyler.updateSeekBarsForSelectedColor(readModeSettings);

                if (selectedColor.equals(Constants.COLOR_NONE)) {
                    customColorButton.setVisibility(View.GONE);
                    generalReadModeCommand.stopReadMode(readModeSettings);
                    startStopButton.setBackgroundColor(getResources().getColor(R.color.gray_disabled));
                } else if (selectedColor.equals(Constants.CUSTOM_COLOR)) {
                    customColorButton.setVisibility(View.VISIBLE);
                    openCustomColorDialog();
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


    // ---------------------- Custom Color Handling ------------------------

    /**
     * Opens the custom color picker dialog.
     */
    private void openCustomColorDialog() {
        generalReadModeCommand.pauseReadMode(readModeSettings);

        // Inflate the dialog layout
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_color_picker, null);
        final View colorPreview = dialogView.findViewById(R.id.colorPreview);
        final SeekBar seekRed = dialogView.findViewById(R.id.seekRed);
        final SeekBar seekGreen = dialogView.findViewById(R.id.seekGreen);
        final SeekBar seekBlue = dialogView.findViewById(R.id.seekBlue);

        // Initial color
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
        new AlertDialog.Builder(this)
                .setTitle(R.string.choose_custom_color)
                .setView(dialogView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    final int currentRed = seekRed.getProgress();
                    final int currentGreen = seekGreen.getProgress();
                    final int currentBlue = seekBlue.getProgress();
                    final String chosenColorHex = String.format(Constants.COLOR_HEX_FORMAT, currentRed, currentGreen, currentBlue);
                    prefsHelper.saveProperty(Constants.PREF_COLOR, Constants.CUSTOM_COLOR);
                    prefsHelper.saveProperty(Constants.PREF_CUSTOM_COLOR, chosenColorHex);
                    // update preferences for custom color
                    readModeSettings.setCustomColor(chosenColorHex);
                    buttonStyler.customizeCustomColorButton(readModeSettings.getCustomColor());

                    // resume read mode
                    generalReadModeCommand.resumeReadMode(readModeSettings);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> generalReadModeCommand.resumeReadMode(readModeSettings)).show();
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
