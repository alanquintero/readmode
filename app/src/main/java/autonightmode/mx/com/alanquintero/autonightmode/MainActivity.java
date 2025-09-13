/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private boolean isLightOn = false;
    private Intent intentLight;
    private int colorLevel = 0;
    private int lightLevel = 0;
    private SharedPreferences sharedpreferences;

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


        initUI();
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

        // COMPONENTS
        final SeekBar seekColorBar = findViewById(R.id.colorLevelBar);
        final TextView colorLevelText = findViewById(R.id.colorLevelPercentageText);
        final SeekBar seekBrightnessBar = findViewById(R.id.brightnessLevelBar);
        final TextView brightnessLevelText = findViewById(R.id.brightnessLevelPercentageText);
        final Button startNowButton = findViewById(R.id.startNowButton);
        final Spinner colorSpinner = findViewById(R.id.colorSpinner);

        // Color names and corresponding hex codes
        String[] colors = {"None", "Soft Beige", "Light Gray", "Pale Yellow", "Warm Sepia", "Soft Blue", "Custom"};
        final String[] colorHex = {"NONE", "#F5F5DC", "#E6E6E6", "#FFFFD2", "#F4ECD3", "#DCEBFF", "CUSTOM"};
        int[] colorValues = {
                Color.WHITE,            // NONE
                Color.parseColor("#F5F5DC"),  // Beige
                Color.parseColor("#E6E6E6"),  // Light Gray
                Color.parseColor("#FFFFD2"),  // Light Yellow
                Color.parseColor("#F4ECD3"),  // Light Cream
                Color.parseColor("#DCEBFF"),  // Pale Blue
                Color.WHITE             // CUSTOM
        };

        // Adapter for dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, colors) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setBackgroundColor(colorValues[position]); // Color for selected item
                view.setTextColor(Color.BLACK); // Text color
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setBackgroundColor(colorValues[position]); // Color for dropdown item
                view.setTextColor(Color.BLACK);
                return view;
            }
        };

        colorSpinner.setAdapter(adapter);

        // Listen for selection
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedColor = colorHex[position];
                if (selectedColor.equals("NONE")) {
                    saveProperty(Constants.COLOR, selectedColor);
                    stopLightService();
                    changeStartNowButtonText(startNowButton);
                } else if (selectedColor.equals("CUSTOM")) {
                    // TODO: Open a color picker dialog
                } else {
                    // Apply selected color
                    saveProperty(Constants.COLOR, selectedColor);
                    startLightService();
                    changeStartNowButtonText(startNowButton);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sharedpreferences = getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
        String levelSettings = sharedpreferences.getString(Constants.COLOR_LEVEL, Constants.VALUE_EMPTY);
        String colorSettings = sharedpreferences.getString(Constants.COLOR, Constants.COLOR_WHITE);
        String lightSettings = sharedpreferences.getString(Constants.LIGHT_LEVEL, Constants.VALUE_EMPTY);

        isLightOn = Boolean.parseBoolean(sharedpreferences.getString(Constants.IS_LIGHT_ON, Constants.VALUE_FALSE));

        if (levelSettings != null && !levelSettings.equals(Constants.VALUE_EMPTY)) {
            seekColorBar.setProgress(Integer.parseInt(levelSettings));
            colorLevelText.setText(Constants.TEXT_PARENTHESES_OPEN + levelSettings + Constants.TEXT_PARENTHESES_CLOSE);
            colorLevel = Integer.parseInt(levelSettings);
        }

        if (lightSettings != null && !lightSettings.equals("")) {
            seekBrightnessBar.setProgress(Integer.parseInt(lightSettings));
            brightnessLevelText.setText(Constants.TEXT_PARENTHESES_OPEN + lightSettings + Constants.TEXT_PARENTHESES_CLOSE);
            lightLevel = Integer.parseInt(lightSettings);
        }


        // *** START NOW ***
        startNowButton.setOnClickListener(v -> {
            if (!isLightOn) {
                colorLevel = seekColorBar.getProgress();
                lightLevel = seekBrightnessBar.getProgress();
                startLightService();
                changeStartNowButtonText(startNowButton);
            } else {
                stopLightService();
                changeStartNowButtonText(startNowButton);
            }
        });
        changeStartNowButtonText(startNowButton);

        // SeekBars
        seekColorBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar bar) {
            }

            public void onStartTrackingTouch(SeekBar bar) {
            }

            public void onProgressChanged(SeekBar bar, int paramInt, boolean paramBoolean) {
                colorLevelText.setText(Constants.TEXT_PARENTHESES_OPEN + paramInt + Constants.TEXT_PARENTHESES_CLOSE);
                colorLevel = paramInt;
                startLightService();
                changeStartNowButtonText(startNowButton);
            }
        });

        seekBrightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar bar) {
            }

            public void onStartTrackingTouch(SeekBar bar) {
            }

            public void onProgressChanged(SeekBar bar, int paramInt, boolean paramBoolean) {
                brightnessLevelText.setText(Constants.TEXT_PARENTHESES_OPEN + paramInt + Constants.TEXT_PARENTHESES_CLOSE);
                lightLevel = paramInt;
                startLightService();
                changeStartNowButtonText(startNowButton);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (!isLightOn) {
            stopLightService();
        }
        super.onDestroy();
    }

    private void startLightService() {
        // Only start service if overlay permission granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            return;
        }

        isLightOn = true;
        saveProperty(Constants.IS_LIGHT_ON, Constants.VALUE_TRUE);
        saveProperty(Constants.LIGHT_LEVEL, String.valueOf(lightLevel));
        saveProperty(Constants.COLOR_LEVEL, String.valueOf(colorLevel));
        intentLight = new Intent(this, DrawOverAppsService.class);

        if (!isMyServiceRunning(intentLight.getClass())) {
            startService(intentLight);
        }
    }

    private void stopLightService() {
        isLightOn = false;
        saveProperty(Constants.IS_LIGHT_ON, Constants.VALUE_FALSE);
        if (intentLight != null) {
            stopService(intentLight);
        } else {
            stopService(new Intent(MainActivity.this, DrawOverAppsService.class));
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void changeStartNowButtonText(Button startNowButton) {
        if (isLightOn) {
            startNowButton.setText(Constants.TEXT_STOP);
        } else {
            startNowButton.setText(Constants.TEXT_START);
        }
    }

    private void saveProperty(final String property, String value) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(property, value);
        editor.apply();
    }

    private void setupStatusBarColor() {
        int lightModeColor = Color.parseColor(Constants.STATUS_BAR_LIGHT_BACKGROUND_COLOR); // light background
        int darkModeColor = Color.parseColor(Constants.STATUS_BAR_DARK_BACKGROUND_COLOR);  // dark gray, not pure black

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
