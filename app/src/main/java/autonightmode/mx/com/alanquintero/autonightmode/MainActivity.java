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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.provider.Settings;
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

public class MainActivity extends AppCompatActivity {

    private boolean isReadModeOn = false;
    private Intent readModeIntent;

    private int dropDownPosition = Constants.DEFAULT_COLOR_DROPDOWN_POSITION;

    private int currentColorIntensity = Constants.DEFAULT_COLOR_INTENSITY;
    private int currentBrightness = Constants.DEFAULT_BRIGHTNESS;

    private int prefColorDropdownPosition = Constants.DEFAULT_COLOR_DROPDOWN_POSITION;
    private String prefCustomColor = Constants.DEFAULT_COLOR_WHITE;

    private final Gson gson = new Gson();

    private final String[] dropDownOptions = new String[]{Constants.COLOR_NONE, Constants.SOFT_BEIGE, Constants.LIGHT_GRAY,
            Constants.PALE_YELLOW, Constants.WARM_SEPIA, Constants.SOFT_BLUE, Constants.CUSTOM_COLOR};
    private Map<String, ColorSettings> colorSettingsMap = new HashMap<>();
    private SharedPreferences sharedPreferences;

    private static final int REQUEST_OVERLAY_PERMISSION = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check overlay permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())
            );
            startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
            return;
        }

        setupStatusBarColor();
        initSharedPreferences();

        initUI();
    }

    private void initSharedPreferences() {
        sharedPreferences = getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
        prefColorDropdownPosition = sharedPreferences.getInt(Constants.PREF_COLOR_DROPDOWN, Constants.DEFAULT_COLOR_DROPDOWN_POSITION);
        prefCustomColor = sharedPreferences.getString(Constants.PREF_CUSTOM_COLOR, Constants.DEFAULT_COLOR_WHITE);
        currentColorIntensity = sharedPreferences.getInt(Constants.PREF_COLOR_INTENSITY, Constants.DEFAULT_COLOR_INTENSITY);
        currentBrightness = sharedPreferences.getInt(Constants.PREF_BRIGHTNESS, Constants.DEFAULT_BRIGHTNESS);

        initColorSettings();
    }

    private void initColorSettings() {
        final String json = sharedPreferences.getString(Constants.PREF_COLOR_SETTINGS, "{}");
        final Type type = new TypeToken<Map<String, ColorSettings>>() {
        }.getType();
        colorSettingsMap = gson.fromJson(json, type);
        if (colorSettingsMap.isEmpty()) {
            // There is no pref saved, create the map with default settings
            colorSettingsMap.put(Constants.COLOR_NONE, new ColorSettings(Constants.COLOR_NONE, Constants.COLOR_NONE, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            colorSettingsMap.put(Constants.SOFT_BEIGE, new ColorSettings(Constants.SOFT_BEIGE, Constants.COLOR_SOFT_BEIGE, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            colorSettingsMap.put(Constants.LIGHT_GRAY, new ColorSettings(Constants.LIGHT_GRAY, Constants.COLOR_LIGHT_GRAY, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            colorSettingsMap.put(Constants.PALE_YELLOW, new ColorSettings(Constants.PALE_YELLOW, Constants.COLOR_PALE_YELLOW, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            colorSettingsMap.put(Constants.WARM_SEPIA, new ColorSettings(Constants.WARM_SEPIA, Constants.COLOR_WARM_SEPIA, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            colorSettingsMap.put(Constants.SOFT_BLUE, new ColorSettings(Constants.SOFT_BLUE, Constants.COLOR_SOFT_BLUE, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
            colorSettingsMap.put(Constants.CUSTOM_COLOR, new ColorSettings(Constants.CUSTOM_COLOR, Constants.CUSTOM_COLOR, Constants.DEFAULT_COLOR_INTENSITY, Constants.DEFAULT_BRIGHTNESS));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                // Permission not granted, just finish activity
                finish();
            } else {
                initUI(); // Permission granted, initialize UI
            }
        }
    }

    private void initUI() {
        setContentView(R.layout.activity_main);

        // Components
        final SeekBar seekColorBar = findViewById(R.id.colorLevelBar);
        final TextView colorLevelText = findViewById(R.id.colorLevelPercentageText);
        final SeekBar seekBrightnessBar = findViewById(R.id.brightnessLevelBar);
        final TextView brightnessLevelText = findViewById(R.id.brightnessLevelPercentageText);
        final Button btnStartStop = findViewById(R.id.btnStartStop);
        final Spinner colorSpinner = findViewById(R.id.colorSpinner);
        final Button customColorButton = findViewById(R.id.customColorButton);

        // Disabling the bars
        seekColorBar.setEnabled(false);
        seekBrightnessBar.setEnabled(false);

        // Color names and corresponding hex codes
        final String colorNone = getString(R.string.color_none);
        final String colorSoftBeige = getString(R.string.color_soft_beige);
        final String colorLightGray = getString(R.string.color_light_gray);
        final String colorPaleYellow = getString(R.string.color_pale_yellow);
        final String colorWarmSepia = getString(R.string.color_warm_sepia);
        final String colorSoftBlue = getString(R.string.color_soft_blue);
        final String colorCustom = getString(R.string.color_custom);

        final String[] colors = {colorNone, colorSoftBeige, colorLightGray, colorPaleYellow, colorWarmSepia, colorSoftBlue, colorCustom};
        final String[] colorHex = {Constants.COLOR_NONE, Constants.COLOR_SOFT_BEIGE, Constants.COLOR_LIGHT_GRAY, Constants.COLOR_PALE_YELLOW, Constants.COLOR_WARM_SEPIA, Constants.COLOR_SOFT_BLUE, Constants.CUSTOM_COLOR};
        final int[] colorsForDropdownItems = {
                Color.TRANSPARENT,            // NONE
                Color.parseColor(Constants.COLOR_SOFT_BEIGE),  // Soft Beige
                Color.parseColor(Constants.COLOR_LIGHT_GRAY),  // Light Gray
                Color.parseColor(Constants.COLOR_PALE_YELLOW),  // Pale Yellow
                Color.parseColor(Constants.COLOR_WARM_SEPIA),  // Warm Sepia
                Color.parseColor(Constants.COLOR_SOFT_BLUE),  // Soft Blue
                Color.WHITE             // CUSTOM
        };
        final int customColorPosition = colorsForDropdownItems.length - 1;

        // Flag to ignore initial selection
        final boolean[] isDropDownInitializing = {true};
        // Adapter for dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, colors) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setBackgroundColor(colorsForDropdownItems[position]); // Color for selected item
                view.setTextColor(Color.BLACK); // Text color
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setBackgroundColor(colorsForDropdownItems[position]); // Color for dropdown item
                if (position == colorsForDropdownItems.length - 1) {
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
                    seekColorBar.setEnabled(false);
                    seekBrightnessBar.setEnabled(false);
                } else {
                    seekColorBar.setEnabled(true);
                    seekBrightnessBar.setEnabled(true);
                }
                if (isDropDownInitializing[0]) {
                    // Ignore the initial selection triggered by setSelection
                    isDropDownInitializing[0] = false;
                    return;
                }
                dropDownPosition = position;
                String selectedColor = colorHex[position];

                // change the brightness and color intensity based on selected color
                final ColorSettings colorSettings = colorSettingsMap.get(dropDownOptions[dropDownPosition]);
                if (colorSettings != null) {
                    currentColorIntensity = colorSettings.getIntensity();
                    seekColorBar.setProgress(currentColorIntensity);
                    colorLevelText.setText(getString(R.string.color_intensity, currentColorIntensity));

                    currentBrightness = colorSettings.getBrightness();
                    seekBrightnessBar.setProgress(currentBrightness);
                    brightnessLevelText.setText(getString(R.string.brightness_level, currentBrightness));
                }

                if (selectedColor.equals(Constants.COLOR_NONE)) {
                    customColorButton.setVisibility(View.GONE);
                    stopReadMode(btnStartStop);
                    btnStartStop.setBackgroundColor(getResources().getColor(R.color.gray_disabled));
                } else if (selectedColor.equals(Constants.CUSTOM_COLOR)) {
                    customColorButton.setVisibility(View.VISIBLE);
                    openCustomColorDialog(btnStartStop, customColorButton);
                    adapter.notifyDataSetChanged();
                } else {
                    // Apply selected color
                    saveProperty(Constants.PREF_COLOR, selectedColor);
                    customColorButton.setVisibility(View.GONE);
                    startRadMode(btnStartStop);
                }

                saveProperty(Constants.PREF_COLOR_DROPDOWN, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        customColorButton.setOnClickListener(v -> openCustomColorDialog(btnStartStop, customColorButton));

        // Set selection if found
        if (prefColorDropdownPosition >= 0) {
            dropDownPosition = prefColorDropdownPosition;
            colorSpinner.setSelection(prefColorDropdownPosition);
            if (prefColorDropdownPosition == customColorPosition) {
                customizeCustomColorButton(customColorButton);
                customColorButton.setVisibility(View.VISIBLE);
            }
        }

        isReadModeOn = sharedPreferences.getBoolean(Constants.PREF_IS_READ_MODE_ON, Constants.DEFAULT_IS_READ_MODE_ENABLED);

        if (currentColorIntensity >= 0) {
            seekColorBar.setProgress(currentColorIntensity);
            colorLevelText.setText(getString(R.string.color_intensity, currentColorIntensity));
        }

        if (currentBrightness >= 0) {
            seekBrightnessBar.setProgress(currentBrightness);
            brightnessLevelText.setText(getString(R.string.brightness_level, currentBrightness));
        }

        // *** START NOW ***
        btnStartStop.setOnClickListener(v -> {
            if (dropDownPosition == 0) {
                Toast.makeText(this, R.string.select_a_color_first, Toast.LENGTH_SHORT).show();
            } else {
                if (!isReadModeOn) {
                    currentColorIntensity = seekColorBar.getProgress();
                    currentBrightness = seekBrightnessBar.getProgress();
                    startRadMode(btnStartStop);
                } else {
                    stopReadMode(btnStartStop);
                }
            }
        });
        applyStartStopButtonStyle(btnStartStop);

        // SeekBars
        seekColorBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentColorIntensity = progress;
                    colorLevelText.setText(getString(R.string.color_intensity, currentColorIntensity));
                    startRadMode(btnStartStop);
                }
            }

            public void onStopTrackingTouch(SeekBar bar) {
            }

            public void onStartTrackingTouch(SeekBar bar) {
            }
        });

        seekBrightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentBrightness = progress;
                    brightnessLevelText.setText(getString(R.string.brightness_level, currentBrightness));
                    startRadMode(btnStartStop);
                }
            }

            public void onStopTrackingTouch(SeekBar bar) {
            }

            public void onStartTrackingTouch(SeekBar bar) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (!isReadModeOn) {
            stopReadMode(null);
        }
        super.onDestroy();
    }

    private void openCustomColorDialog(final Button btnStartStop, final Button customColorButton) {
        stopReadMode(btnStartStop);

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
                    startRadMode(btnStartStop);
                    // update preferences for custom color
                    prefCustomColor = chosenColorHex;
                    customizeCustomColorButton(customColorButton);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    startRadMode(btnStartStop);
                }).show();
    }

    private void startRadMode(final @NonNull Button btnStartStop) {
        // Only start service if overlay permission granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            return;
        }

        isReadModeOn = true;

        // save properties
        saveProperty(Constants.PREF_IS_READ_MODE_ON, true);
        saveProperty(Constants.PREF_BRIGHTNESS, currentBrightness);
        saveProperty(Constants.PREF_COLOR_INTENSITY, currentColorIntensity);
        saveColorSettingsProperty();

        // change button style
        applyStartStopButtonStyle(btnStartStop);

        readModeIntent = new Intent(this, DrawOverAppsService.class);
        if (!isMyServiceRunning(readModeIntent.getClass())) {
            startService(readModeIntent);
        }
    }

    private void stopReadMode(final @Nullable Button btnStartStop) {
        isReadModeOn = false;

        // save properties
        saveProperty(Constants.PREF_IS_READ_MODE_ON, false);
        saveProperty(Constants.PREF_BRIGHTNESS, currentBrightness);
        saveProperty(Constants.PREF_COLOR_INTENSITY, currentColorIntensity);
        saveColorSettingsProperty();

        // change button style
        if (btnStartStop != null) {
            applyStartStopButtonStyle(btnStartStop);
        }

        if (readModeIntent != null) {
            stopService(readModeIntent);
        } else {
            stopService(new Intent(MainActivity.this, DrawOverAppsService.class));
        }
    }

    private boolean isMyServiceRunning(final @NonNull Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void applyStartStopButtonStyle(final @NonNull Button btnStartStop) {
        if (isReadModeOn) {
            btnStartStop.setText(R.string.stop);
            btnStartStop.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.stop_color));
        } else {
            btnStartStop.setText(R.string.start);
            btnStartStop.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.start_color));
        }
    }

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

    private void saveProperty(final @NonNull String property, final @NonNull String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(property, value);
        editor.apply();
    }

    private void saveProperty(final @NonNull String property, final @NonNull boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(property, value);
        editor.apply();
    }

    private void saveProperty(final @NonNull String property, final int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(property, value);
        editor.apply();
    }

    private void saveColorSettingsProperty() {
        // update the brightness and color intensity values for the selected color
        final String selectedColor = dropDownOptions[dropDownPosition];
        final ColorSettings colorSettings = colorSettingsMap.get(selectedColor);
        if (colorSettings != null) {
            colorSettings.setBrightness(currentBrightness);
            colorSettings.setIntensity(currentColorIntensity);
            colorSettingsMap.put(selectedColor, colorSettings);
        }
        for (ColorSettings value : colorSettingsMap.values()) {
            System.out.println(value);
        }

        Gson gson = new Gson();
        String json = gson.toJson(colorSettingsMap);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREF_COLOR_SETTINGS, json);
        editor.apply();
    }

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
}
