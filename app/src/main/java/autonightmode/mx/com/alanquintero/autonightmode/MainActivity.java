/*****************************************************************
 *
 * Copyright (C) 2018 Alan Quintero <http://alanquintero.com.mx/>
 *
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // COMPONENTS
        final SeekBar seekColorBar = (SeekBar) findViewById(R.id.colorLevelBar);
        final TextView colorLevelText = (TextView) findViewById(R.id.colorLevelPercentageText);
        final SeekBar seekBrightnessBar = (SeekBar) findViewById(R.id.brightnessLevelBar);
        final TextView brightnessLevelText = (TextView) findViewById(R.id.brightnessLevelPercentageText);
        final Button startNowButton = (Button) findViewById(R.id.startNowButton);
        final RadioButton yellow = (RadioButton) findViewById(R.id.yellow);
        final RadioButton red = (RadioButton) findViewById(R.id.red);
        final RadioButton green = (RadioButton) findViewById(R.id.green);
        final RadioButton blue = (RadioButton) findViewById(R.id.blue);
        final RadioButton gray = (RadioButton) findViewById(R.id.gray);
        final RadioButton pink = (RadioButton) findViewById(R.id.pink);
        final RadioButton white = (RadioButton) findViewById(R.id.white);

        sharedpreferences = getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
        String levelSettings = sharedpreferences.getString(Constants.COLOR_LEVEL, Constants.VALUE_EMPTY);
        String colorSettings = sharedpreferences.getString(Constants.COLOR, Constants.COLOR_WHITE);
        String lightSettings = sharedpreferences.getString(Constants.LIGHT_LEVEL, Constants.VALUE_EMPTY);

        isLightOn = Boolean.parseBoolean(sharedpreferences.getString(Constants.IS_LIGHT_ON, Constants.VALUE_FALSE));

        if(levelSettings != null && !levelSettings.equals(Constants.VALUE_EMPTY)) {
            seekColorBar.setProgress(Integer.parseInt(levelSettings));
            colorLevelText.setText(Constants.TEXT_PARENTHESES_OPEN + levelSettings + Constants.TEXT_PARENTHESES_CLOSE);
            colorLevel = Integer.parseInt(levelSettings);
        }

        if(lightSettings != null && !lightSettings.equals("")) {
            seekBrightnessBar.setProgress(Integer.parseInt(lightSettings));
            brightnessLevelText.setText(Constants.TEXT_PARENTHESES_OPEN + lightSettings + Constants.TEXT_PARENTHESES_CLOSE);
            colorLevel = Integer.parseInt(levelSettings);
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
        startNowButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isLightOn) {
                    colorLevel = seekColorBar.getProgress();
                    lightLevel = seekBrightnessBar.getProgress();
                    startLightService();
                    changeStartNowButtonText(startNowButton);
                } else {
                    stopLightService();
                    changeStartNowButtonText(startNowButton);
                }
            }
        });
        changeStartNowButtonText(startNowButton);


        // *** COLOR LEVEL ***
        seekColorBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar bar) { }

            public void onStartTrackingTouch(SeekBar bar) { }

            public void onProgressChanged(SeekBar bar, int paramInt, boolean paramBoolean) {
                colorLevelText.setText(Constants.TEXT_PARENTHESES_OPEN + paramInt + Constants.TEXT_PARENTHESES_CLOSE);
                colorLevel = paramInt;
                startLightService();
                changeStartNowButtonText(startNowButton);
            }
        });

        // *** BRIGHTNESS LEVEL ***
        seekBrightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar bar) { }

            public void onStartTrackingTouch(SeekBar bar) { }

            public void onProgressChanged(SeekBar bar, int paramInt, boolean paramBoolean) {
                brightnessLevelText.setText(Constants.TEXT_PARENTHESES_OPEN + paramInt + Constants.TEXT_PARENTHESES_CLOSE);
                lightLevel = paramInt;
                startLightService();
                changeStartNowButtonText(startNowButton);
            }
        });


        // *** RadioButtons ***

        // * YELLOW *
        yellow.setOnClickListener((new View.OnClickListener() {
            public void onClick(View v) {
                saveProperty(Constants.COLOR, Constants.COLOR_YELLOW);
                startLightService();
                changeStartNowButtonText(startNowButton);
            }
        }));

        // * RED *
        red.setOnClickListener((new View.OnClickListener() {
            public void onClick(View v) {
                saveProperty(Constants.COLOR, Constants.COLOR_RED);
                startLightService();
                changeStartNowButtonText(startNowButton);
            }
        }));

        // * GREEN *
        green.setOnClickListener((new View.OnClickListener() {
            public void onClick(View v) {
                saveProperty(Constants.COLOR, Constants.COLOR_GREEN);
                startLightService();
                changeStartNowButtonText(startNowButton);
            }
        }));

        // * BLUE *
        blue.setOnClickListener((new View.OnClickListener() {
            public void onClick(View v) {
                saveProperty(Constants.COLOR, Constants.COLOR_BLUE);
                startLightService();
                changeStartNowButtonText(startNowButton);
            }
        }));

        // * GRAY *
        gray.setOnClickListener((new View.OnClickListener() {
            public void onClick(View v) {
                saveProperty(Constants.COLOR, Constants.COLOR_GRAY);
                startLightService();
                changeStartNowButtonText(startNowButton);
            }
        }));

        // * PINK *
        pink.setOnClickListener((new View.OnClickListener() {
            public void onClick(View v) {
                saveProperty(Constants.COLOR, Constants.COLOR_PINK);
                startLightService();
                changeStartNowButtonText(startNowButton);
            }
        }));

        // * WHITE *
        white.setOnClickListener((new View.OnClickListener() {
            public void onClick(View v) {
                saveProperty(Constants.COLOR, Constants.COLOR_WHITE);
                startLightService();
                changeStartNowButtonText(startNowButton);
            }
        }));

    }

    @Override
    protected void onDestroy() {
        if (!isLightOn) {
            stopLightService();
        }
        super.onDestroy();
    }

    private void startLightService() {
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
        if(intentLight != null) {
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
