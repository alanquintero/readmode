/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
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
        final RadioButton yellow = findViewById(R.id.yellow);
        final RadioButton red = findViewById(R.id.red);
        final RadioButton green = findViewById(R.id.green);
        final RadioButton blue = findViewById(R.id.blue);
        final RadioButton gray = findViewById(R.id.gray);
        final RadioButton pink = findViewById(R.id.pink);
        final RadioButton white = findViewById(R.id.white);

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

        switch (colorSettings) {
            case Constants.COLOR_YELLOW:
                yellow.setChecked(true);
                break;
            case Constants.COLOR_RED:
                red.setChecked(true);
                break;
            case Constants.COLOR_GREEN:
                green.setChecked(true);
                break;
            case Constants.COLOR_BLUE:
                blue.setChecked(true);
                break;
            case Constants.COLOR_GRAY:
                gray.setChecked(true);
                break;
            case Constants.COLOR_PINK:
                pink.setChecked(true);
                break;
            case Constants.COLOR_WHITE:
                white.setChecked(true);
                break;
            default:
                white.setChecked(true);
                break;
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

        // RadioButtons
        yellow.setOnClickListener(v -> {
            saveProperty(Constants.COLOR, Constants.COLOR_YELLOW);
            startLightService();
            changeStartNowButtonText(startNowButton);
        });
        red.setOnClickListener(v -> {
            saveProperty(Constants.COLOR, Constants.COLOR_RED);
            startLightService();
            changeStartNowButtonText(startNowButton);
        });
        green.setOnClickListener(v -> {
            saveProperty(Constants.COLOR, Constants.COLOR_GREEN);
            startLightService();
            changeStartNowButtonText(startNowButton);
        });
        blue.setOnClickListener(v -> {
            saveProperty(Constants.COLOR, Constants.COLOR_BLUE);
            startLightService();
            changeStartNowButtonText(startNowButton);
        });
        gray.setOnClickListener(v -> {
            saveProperty(Constants.COLOR, Constants.COLOR_GRAY);
            startLightService();
            changeStartNowButtonText(startNowButton);
        });
        pink.setOnClickListener(v -> {
            saveProperty(Constants.COLOR, Constants.COLOR_PINK);
            startLightService();
            changeStartNowButtonText(startNowButton);
        });
        white.setOnClickListener(v -> {
            saveProperty(Constants.COLOR, Constants.COLOR_WHITE);
            startLightService();
            changeStartNowButtonText(startNowButton);
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
        editor.commit();
    }
}
