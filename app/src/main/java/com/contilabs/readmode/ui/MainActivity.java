/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.contilabs.readmode.command.GeneralReadModeCommand;
import com.contilabs.readmode.command.SettingsReadModeCommand;
import com.contilabs.readmode.manager.ReadModeManager;
import com.contilabs.readmode.model.ReadModeSettings;
import com.contilabs.readmode.observer.customcolor.CustomColorSubject;
import com.contilabs.readmode.observer.dropdown.ColorDropdownSubject;
import com.contilabs.readmode.observer.readmode.ReadModeSubject;
import com.contilabs.readmode.observer.settings.SettingsObserver;
import com.contilabs.readmode.observer.settings.SettingsSubject;
import com.contilabs.readmode.ui.controller.ButtonController;
import com.contilabs.readmode.ui.controller.ColorDropdownController;
import com.contilabs.readmode.ui.controller.MenuController;
import com.contilabs.readmode.ui.controller.SeekBarController;
import com.contilabs.readmode.ui.controller.StatusBarController;
import com.contilabs.readmode.ui.controller.TextViewController;
import com.contilabs.readmode.R;
import com.contilabs.readmode.ui.dialog.CustomColorDialog;
import com.contilabs.readmode.util.Constants;
import com.contilabs.readmode.util.PrefsHelper;
import com.contilabs.readmode.util.Utils;

/**
 * MainActivity controls the UI for Read Mode.
 * Users can select colors, brightness, intensity, and start/stop the read mode overlay.
 *
 * @author Alan Quintero
 */
public class MainActivity extends AppCompatActivity implements SettingsObserver {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final ReadModeSettings readModeSettings = ReadModeSettings.init();
    private PrefsHelper prefsHelper;
    private ReadModeManager readModeManager;
    private GeneralReadModeCommand generalReadModeCommand;
    private String[] colorNames = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Check Theme before loading app content
        prefsHelper = PrefsHelper.init(this);
        final Constants.ThemeMode savedTheme = prefsHelper.getTheme();
        Utils.setAppTheme(savedTheme);

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
        initSharedPreferences();
        initColorNames();

        Log.d(TAG, "init classes...");
        final @NonNull View rootView = findViewById(android.R.id.content);
        final ReadModeSubject readModeSubject = new ReadModeSubject();
        final ColorDropdownSubject colorDropdownSubject = new ColorDropdownSubject();
        final CustomColorSubject customColorSubject = new CustomColorSubject();
        final SettingsSubject settingsSubject = new SettingsSubject();
        readModeManager = new ReadModeManager(this, prefsHelper, readModeSubject, readModeSettings);
        generalReadModeCommand = new GeneralReadModeCommand(readModeManager, readModeSettings);
        final SettingsReadModeCommand settingsReadModeCommand = new SettingsReadModeCommand(readModeManager, readModeSettings);

        /*
         * When Read Mode is ON and the app is updated, the app process is killed.
         * As a result, Read Mode is turned OFF, but the property was never saved,
         * leaving the stored state inconsistent with the actual state.
         */
        if (!readModeManager.isReadModeServiceRunning() && readModeSettings.isReadModeOn()) {
            Log.d(TAG, "Read Mode is not running but setting is ON, set it to OFF");
            readModeSettings.setIsReadModeOn(false);
        }

        // UI components
        final CustomColorDialog customColorDialog = new CustomColorDialog(this, generalReadModeCommand, readModeSettings, customColorSubject);
        final ButtonController buttonController = new ButtonController(this, this, rootView, generalReadModeCommand, readModeSettings, customColorDialog);
        final SeekBarController seekBarController = new SeekBarController(this, rootView, generalReadModeCommand, readModeSettings);
        final TextViewController textViewController = new TextViewController(this, rootView, readModeSettings, colorNames);
        final StatusBarController statusBarController = new StatusBarController(this, this);
        final MenuController menuController = new MenuController(this, this, rootView, settingsSubject, readModeSettings);
        final ColorDropdownController colorDropdownController = new ColorDropdownController(this, this, rootView, customColorDialog, colorDropdownSubject, settingsReadModeCommand, readModeSettings, colorNames);

        statusBarController.setupStatusBarColor();
        colorDropdownController.setupColorDropdown();
        textViewController.setupTextViews();
        seekBarController.setupSeekBars();
        buttonController.setupButtons();
        menuController.setupMenu();

        // Register Observers
        readModeSubject.registerObserver(buttonController);
        customColorSubject.registerObserver(buttonController);
        customColorSubject.registerObserver(seekBarController);
        colorDropdownSubject.registerObserver(buttonController);
        colorDropdownSubject.registerObserver(seekBarController);
        colorDropdownSubject.registerObserver(textViewController);
        settingsSubject.registerObserver(textViewController);
        settingsSubject.registerObserver(seekBarController);
        settingsSubject.registerObserver(this);

        Log.i(TAG, "UI initialized successfully.");
    }

    // ---------------------- Init methods ------------------------

    /**
     * Initialize SharedPreferences and load color settings map.
     */
    private void initSharedPreferences() {
        prefsHelper.initPrefColorSettingsMap();

        // Loading saved preferences to the Read Mode Setting obj
        readModeSettings.setIsReadModeOn(prefsHelper.isReadModeOn());
        readModeSettings.setColorDropdownPosition(prefsHelper.getColorDropdownPosition());
        readModeSettings.setCustomColor(prefsHelper.getCustomColor());
        readModeSettings.setColorIntensity(prefsHelper.getColorIntensity());
        readModeSettings.setBrightness(prefsHelper.getBrightness());
        readModeSettings.setAutoStartReadMode(prefsHelper.getAutoStartReadMode());
        readModeSettings.setShouldUseSameIntensityBrightnessForAll(prefsHelper.shouldUseSameIntensityBrightnessForAll());
    }


    /**
     * Initialize the color names for the dropdown.
     */
    private void initColorNames() {
        // Color names for the color dropdown
        final String colorSoftBeige = getString(R.string.color_soft_beige);
        final String colorLightGray = getString(R.string.color_light_gray);
        final String colorPaleYellow = getString(R.string.color_pale_yellow);
        final String colorWarmSepia = getString(R.string.color_warm_sepia);
        final String colorSoftBlue = getString(R.string.color_soft_blue);
        final String colorCustom = getString(R.string.color_custom);
        colorNames = new String[]{colorSoftBeige, colorLightGray, colorPaleYellow, colorWarmSepia, colorSoftBlue, colorCustom};
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
        generalReadModeCommand.stopReadMode();
        super.onDestroy();
        Log.i(TAG, "Activity destroyed.");
    }

    @Override
    public void onSettingsChanged(Constants.SETTING_OPTIONS setting) {
        if (Constants.SETTING_OPTIONS.RESET_APP_DATA.equals(setting)) {
            Log.w(TAG, "App data was reset");
            if (readModeManager != null) {
                readModeManager.stopReadMode();
            }
            final Constants.ThemeMode savedTheme = prefsHelper.getTheme();
            Utils.setAppTheme(savedTheme);
            initUI();
        }
    }
}
