/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;

/**
 * MainActivity controls the UI for Read Mode.
 * Users can select colors, brightness, intensity, and start/stop the read mode overlay.
 *
 * @author Alan Quintero
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // SharedPreferences and Gson
    private SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    // User preferences
    private boolean prefIsReadModeOn = Constants.DEFAULT_IS_READ_MODE_ENABLED;
    private String prefCustomColor = Constants.DEFAULT_COLOR_WHITE;
    private int prefColorDropdownPosition = Constants.DEFAULT_COLOR_DROPDOWN_POSITION;
    private Map<String, ColorSettings> prefColorSettingsMap = new HashMap<>();

    // Current state variables
    private int currentColorDropdownPosition = Constants.DEFAULT_COLOR_DROPDOWN_POSITION;
    private int currentColorIntensity = Constants.DEFAULT_COLOR_INTENSITY;
    private int currentBrightness = Constants.DEFAULT_BRIGHTNESS;

    // Overlay service intent
    private Intent readModeIntent;

    // Colors
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
    private String[] colors = {};
    private final int noColorDropdownPosition = 0;
    private final int customColorDropdownPosition = backgroundColorForDropdownItems.length - 1;

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

        // UI Components
        final SeekBar seekColorIntensityBar = findViewById(R.id.colorLevelBar);
        final TextView colorLevelText = findViewById(R.id.colorLevelPercentageText);
        final SeekBar seekBrightnessBar = findViewById(R.id.brightnessLevelBar);
        final TextView brightnessLevelText = findViewById(R.id.brightnessLevelPercentageText);
        final TextView colorSettingsText = findViewById(R.id.labelColorSettings);
        final Button startStopButton = findViewById(R.id.startStopButton);
        final Spinner colorSpinner = findViewById(R.id.colorSpinner);
        final Button customColorButton = findViewById(R.id.customColorButton);

        // Disable SeekBars initially until a color is selected
        seekColorIntensityBar.setEnabled(false);
        seekBrightnessBar.setEnabled(false);

        setupColorDropdown(colorSpinner, seekColorIntensityBar, seekBrightnessBar, colorSettingsText, colorLevelText, brightnessLevelText, customColorButton, startStopButton);

        // Restore saved selection
        if (prefColorDropdownPosition >= 0) {
            currentColorDropdownPosition = prefColorDropdownPosition;
            colorSpinner.setSelection(prefColorDropdownPosition);
            setColorSettingsText(colorSettingsText);
            if (prefColorDropdownPosition == customColorDropdownPosition) {
                customizeCustomColorButton(customColorButton);
                customColorButton.setVisibility(View.VISIBLE);
            }
        }

        // Restore SeekBars values
        seekColorIntensityBar.setProgress(currentColorIntensity);
        colorLevelText.setText(getString(R.string.color_intensity, currentColorIntensity));
        seekBrightnessBar.setProgress(currentBrightness);
        brightnessLevelText.setText(getString(R.string.brightness_level, currentBrightness));

        // Apply style to buttons
        applyStartStopButtonStyle(startStopButton);
        customizeCustomColorButton(customColorButton);

        // Custom color button listener
        customColorButton.setOnClickListener(v -> openCustomColorDialog(startStopButton, customColorButton));

        // Start/Stop button listener
        startStopButton.setOnClickListener(v -> {
            if (currentColorDropdownPosition == noColorDropdownPosition) {
                Toast.makeText(this, R.string.select_a_color_first, Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Start clicked but no color selected.");
            } else {
                if (prefIsReadModeOn) {
                    stopReadMode(startStopButton);
                } else {
                    currentColorIntensity = seekColorIntensityBar.getProgress();
                    currentBrightness = seekBrightnessBar.getProgress();
                    startReadMode(startStopButton);
                }
            }
        });

        // SeekBar listeners
        setupSeekColorIntensityBar(seekColorIntensityBar, colorLevelText, startStopButton);
        setupSeekBrightnessBar(seekBrightnessBar, brightnessLevelText, startStopButton);

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
        sharedPreferences = getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
        prefIsReadModeOn = sharedPreferences.getBoolean(Constants.PREF_IS_READ_MODE_ON, Constants.DEFAULT_IS_READ_MODE_ENABLED);
        prefColorDropdownPosition = sharedPreferences.getInt(Constants.PREF_COLOR_DROPDOWN, Constants.DEFAULT_COLOR_DROPDOWN_POSITION);
        prefCustomColor = sharedPreferences.getString(Constants.PREF_CUSTOM_COLOR, Constants.DEFAULT_COLOR_WHITE);
        currentColorIntensity = sharedPreferences.getInt(Constants.PREF_COLOR_INTENSITY, Constants.DEFAULT_COLOR_INTENSITY);
        currentBrightness = sharedPreferences.getInt(Constants.PREF_BRIGHTNESS, Constants.DEFAULT_BRIGHTNESS);

        initPrefColorSettingsMap();
    }

    /**
     * Loads color settings map.
     */
    private void initPrefColorSettingsMap() {
        final String json = sharedPreferences.getString(Constants.PREF_COLOR_SETTINGS, "{}");
        final Type type = new TypeToken<Map<String, ColorSettings>>() {
        }.getType();
        prefColorSettingsMap = gson.fromJson(json, type);
        if (prefColorSettingsMap.isEmpty()) {
            Log.d(TAG, "prefColorSettingsMap is empty, initializing map...");
            // There is no pref saved, create the map with default settings
            prefColorSettingsMap.put(Constants.COLOR_NONE, new ColorSettings(Constants.COLOR_NONE, Constants.COLOR_NONE, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            prefColorSettingsMap.put(Constants.SOFT_BEIGE, new ColorSettings(Constants.SOFT_BEIGE, Constants.COLOR_SOFT_BEIGE, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            prefColorSettingsMap.put(Constants.LIGHT_GRAY, new ColorSettings(Constants.LIGHT_GRAY, Constants.COLOR_LIGHT_GRAY, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            prefColorSettingsMap.put(Constants.PALE_YELLOW, new ColorSettings(Constants.PALE_YELLOW, Constants.COLOR_PALE_YELLOW, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            prefColorSettingsMap.put(Constants.WARM_SEPIA, new ColorSettings(Constants.WARM_SEPIA, Constants.COLOR_WARM_SEPIA, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            prefColorSettingsMap.put(Constants.SOFT_BLUE, new ColorSettings(Constants.SOFT_BLUE, Constants.COLOR_SOFT_BLUE, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            prefColorSettingsMap.put(Constants.CUSTOM_COLOR, new ColorSettings(Constants.CUSTOM_COLOR, Constants.CUSTOM_COLOR, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
        }
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
    private void setupColorDropdown(final @NonNull Spinner colorSpinner, final @NonNull SeekBar seekColorIntensityBar, final @NonNull SeekBar seekBrightnessBar,
                                    final @NonNull TextView colorSettingsText, final @NonNull TextView colorLevelText, final @NonNull TextView brightnessLevelText,
                                    final @NonNull Button customColorButton, final @NonNull Button startStopButton) {
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
                if (position == customColorDropdownPosition) {
                    int color = Color.parseColor(prefCustomColor);
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
                if (position == 0) {
                    // No color option
                    // Disabling the seek bars
                    seekColorIntensityBar.setEnabled(false);
                    seekBrightnessBar.setEnabled(false);
                } else {
                    // Enabling the seek bars
                    seekColorIntensityBar.setEnabled(true);
                    seekBrightnessBar.setEnabled(true);
                }
                if (isColorDropdownInitializing[0]) {
                    // Ignore the initial selection triggered by setSelection
                    isColorDropdownInitializing[0] = false;
                    return;
                }
                currentColorDropdownPosition = position;
                String selectedColor = colorHex[position];

                // change the brightness and color intensity based on selected color
                setColorSettingsText(colorSettingsText);
                final ColorSettings colorSettings = prefColorSettingsMap.get(Constants.COLOR_DROPDOWN_OPTIONS[currentColorDropdownPosition]);
                if (colorSettings != null) {
                    currentColorIntensity = colorSettings.getIntensity();
                    seekColorIntensityBar.setProgress(currentColorIntensity);
                    colorLevelText.setText(getString(R.string.color_intensity, currentColorIntensity));

                    currentBrightness = colorSettings.getBrightness();
                    seekBrightnessBar.setProgress(currentBrightness);
                    brightnessLevelText.setText(getString(R.string.brightness_level, currentBrightness));
                }

                if (selectedColor.equals(Constants.COLOR_NONE)) {
                    customColorButton.setVisibility(View.GONE);
                    stopReadMode(startStopButton);
                    startStopButton.setBackgroundColor(getResources().getColor(R.color.gray_disabled));
                } else if (selectedColor.equals(Constants.CUSTOM_COLOR)) {
                    customColorButton.setVisibility(View.VISIBLE);
                    openCustomColorDialog(startStopButton, customColorButton);
                    adapter.notifyDataSetChanged();
                } else {
                    // Apply selected color
                    saveProperty(Constants.PREF_COLOR, selectedColor);
                    customColorButton.setVisibility(View.GONE);
                    startReadMode(startStopButton);
                }

                saveProperty(Constants.PREF_COLOR_DROPDOWN, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Configures the SeekBar that controls the color intensity of the screen filter.
     * <p>
     * This method sets the initial progress based on the current intensity value,
     * listens for user changes, updates the corresponding label dynamically,
     * and triggers the filter update when the value changes.
     * </p>
     */
    private void setupSeekColorIntensityBar(final @NonNull SeekBar seekColorIntensityBar, final @NonNull TextView colorLevelText, final @NonNull Button startStopButton) {
        seekColorIntensityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentColorIntensity = progress;
                    colorLevelText.setText(getString(R.string.color_intensity, currentColorIntensity));
                    startReadMode(startStopButton);
                }
            }

            public void onStopTrackingTouch(SeekBar bar) {
            }

            public void onStartTrackingTouch(SeekBar bar) {
            }
        });
    }

    /**
     * Configures the SeekBar that controls the brightness level of the screen filter.
     * <p>
     * This method sets the initial progress based on the current brightness value,
     * listens for user interactions, updates the corresponding label dynamically,
     * and applies the brightness change to the filter when the value is modified.
     * </p>
     */
    private void setupSeekBrightnessBar(final @NonNull SeekBar seekBrightnessBar, final @NonNull TextView brightnessLevelText, final @NonNull Button startStopButton) {
        seekBrightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentBrightness = progress;
                    brightnessLevelText.setText(getString(R.string.brightness_level, currentBrightness));
                    startReadMode(startStopButton);
                }
            }

            public void onStopTrackingTouch(SeekBar bar) {
            }

            public void onStartTrackingTouch(SeekBar bar) {
            }
        });
    }


    // ---------------------- Custom Color Handling ------------------------

    /**
     * Opens the custom color picker dialog.
     */
    private void openCustomColorDialog(final @NonNull Button startStopButton, final @NonNull Button customColorButton) {
        stopReadMode(startStopButton);

        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_color_picker, null);
        final View colorPreview = dialogView.findViewById(R.id.colorPreview);
        final SeekBar seekRed = dialogView.findViewById(R.id.seekRed);
        final SeekBar seekGreen = dialogView.findViewById(R.id.seekGreen);
        final SeekBar seekBlue = dialogView.findViewById(R.id.seekBlue);

        // Initial color
        int initialColor = Color.parseColor(prefCustomColor);
        seekRed.setProgress(Color.red(initialColor));
        seekGreen.setProgress(Color.green(initialColor));
        seekBlue.setProgress(Color.blue(initialColor));
        colorPreview.setBackgroundColor(initialColor);

        // Update preview when SeekBars change
        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int color = Color.rgb(seekRed.getProgress(), seekGreen.getProgress(), seekBlue.getProgress());
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
                    int red = seekRed.getProgress();
                    int green = seekGreen.getProgress();
                    int blue = seekBlue.getProgress();
                    String chosenColorHex = String.format(Constants.COLOR_HEX_FORMAT, red, green, blue);
                    saveProperty(Constants.PREF_COLOR, Constants.CUSTOM_COLOR);
                    saveProperty(Constants.PREF_CUSTOM_COLOR, chosenColorHex);
                    // start read mode
                    startReadMode(startStopButton);
                    // update preferences for custom color
                    prefCustomColor = chosenColorHex;
                    customizeCustomColorButton(customColorButton);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    if (prefIsReadModeOn) {
                        startReadMode(startStopButton);
                    }
                }).show();
    }

    /**
     * Updates the provided {@link TextView} with the current color settings text.
     * <p>
     * If no color is selected in the dropdown (i.e., the current position equals
     * {@code noColorDropdownPosition}), the text is set to a placeholder label.
     * Otherwise, the text is formatted to include the placeholder label along
     * with the currently selected color name.
     * </p>
     */
    private void setColorSettingsText(final @NonNull TextView colorSettingsText) {
        final String label = getString(R.string.color_settings_placeholder);
        if (currentColorDropdownPosition == noColorDropdownPosition) {
            colorSettingsText.setText(label);
        } else {
            String selectedColor = colors[currentColorDropdownPosition];
            colorSettingsText.setText(getString(R.string.color_settings_format, label, selectedColor));
        }
    }


    // ---------------------- Start/Stop Read Mode ------------------------

    /**
     * Starts the Read Mode by enabling the overlay service and updating
     * the UI and shared preferences with the current color, brightness,
     * and intensity settings.
     */
    private void startReadMode(final @NonNull Button startStopButton) {
        // Only start service if overlay permission granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            return;
        }

        prefIsReadModeOn = true;

        // save properties
        saveProperty(Constants.PREF_IS_READ_MODE_ON, true);
        saveProperty(Constants.PREF_BRIGHTNESS, currentBrightness);
        saveProperty(Constants.PREF_COLOR_INTENSITY, currentColorIntensity);
        saveColorSettingsProperty();

        // change button style
        applyStartStopButtonStyle(startStopButton);

        readModeIntent = new Intent(this, DrawOverAppsService.class);
        if (!isMyServiceRunning(readModeIntent.getClass())) {
            startService(readModeIntent);
        }
    }

    /**
     * Stops the Read Mode by disabling the overlay service and updating
     * the UI and shared preferences with the current color, brightness,
     * and intensity settings.
     */
    private void stopReadMode(final @Nullable Button startStopButton) {
        prefIsReadModeOn = false;

        // save properties
        saveProperty(Constants.PREF_IS_READ_MODE_ON, false);
        saveProperty(Constants.PREF_BRIGHTNESS, currentBrightness);
        saveProperty(Constants.PREF_COLOR_INTENSITY, currentColorIntensity);
        saveColorSettingsProperty();

        // change button style
        if (startStopButton != null) {
            applyStartStopButtonStyle(startStopButton);
        }

        if (readModeIntent != null) {
            stopService(readModeIntent);
        } else {
            stopService(new Intent(MainActivity.this, DrawOverAppsService.class));
        }
    }

    /**
     * Checks if a specific service is currently running on the device.
     */
    private boolean isMyServiceRunning(final @NonNull Class<?> serviceClass) {
        final ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    // ---------------------- Buttons style ------------------------

    /**
     * Updates the visual style and text of the Start/Stop button
     * based on whether Read Mode is currently active.
     */
    private void applyStartStopButtonStyle(final @NonNull Button startStopButton) {
        if (prefIsReadModeOn) {
            startStopButton.setText(R.string.stop);
            startStopButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.stop_color));
        } else {
            startStopButton.setText(R.string.start);
            startStopButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.start_color));
        }
    }

    /**
     * Updates the custom color button's background and text color
     * based on the current custom color. Ensures text is readable
     * by calculating the perceived brightness of the color.
     */

    private void customizeCustomColorButton(final @NonNull Button customColorButton) {
        // Set background color
        customColorButton.setBackgroundColor(Color.parseColor(prefCustomColor));
        // Set text color
        int color = Color.parseColor(prefCustomColor);
        // Calculate brightness
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        double brightness = (0.299 * r + 0.587 * g + 0.114 * b); // Perceived brightness
        if (brightness > 200) { // very light color
            customColorButton.setTextColor(Color.BLACK); // fallback
        } else {
            customColorButton.setTextColor(Color.WHITE);
        }
    }


    // ---------------------- Shared Preferences Utils ------------------------

    /**
     * Saves an String value to {@link SharedPreferences} under the specified key.
     */
    private void saveProperty(final @NonNull String property, final @NonNull String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(property, value);
        editor.apply();
    }

    /**
     * Saves an boolean value to {@link SharedPreferences} under the specified key.
     */
    private void saveProperty(final @NonNull String property, final boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(property, value);
        editor.apply();
    }

    /**
     * Saves an integer value to {@link SharedPreferences} under the specified key.
     */
    private void saveProperty(final @NonNull String property, final int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(property, value);
        editor.apply();
    }

    /**
     * Saves the current brightness and color intensity settings for the selected color
     * into the {@link SharedPreferences}. Updates the {@link #prefColorSettingsMap}
     * with the latest values and serializes it as JSON for persistence.
     */
    private void saveColorSettingsProperty() {
        // update the brightness and color intensity values for the selected color
        final String selectedColor = Constants.COLOR_DROPDOWN_OPTIONS[currentColorDropdownPosition];
        final ColorSettings colorSettings = prefColorSettingsMap.get(selectedColor);
        if (colorSettings != null) {
            colorSettings.setBrightness(currentBrightness);
            colorSettings.setIntensity(currentColorIntensity);
            prefColorSettingsMap.put(selectedColor, colorSettings);
        }

        final String json = gson.toJson(prefColorSettingsMap);

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREF_COLOR_SETTINGS, json);
        editor.apply();
    }


    // ---------------------- Activity Lifecycle ------------------------

    @Override
    protected void onDestroy() {
        stopReadMode(null);
        super.onDestroy();
        Log.i(TAG, "Activity destroyed.");
    }
}
