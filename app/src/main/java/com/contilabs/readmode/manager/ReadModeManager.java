/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.manager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.contilabs.readmode.model.ReadModeSettings;
import com.contilabs.readmode.observer.readmode.ReadModeSubject;
import com.contilabs.readmode.service.DrawOverAppsService;
import com.contilabs.readmode.util.Constants;
import com.contilabs.readmode.util.PrefsHelper;

/**
 * ReadModeManager is a centralized class responsible for managing
 * the lifecycle of "Read Mode" in the application. It delegates
 * start and stop operations to implementations of ReadModeCommand,
 * providing a single point of control for triggering Read Mode actions.
 *
 * @author Alan Quintero
 */
public class ReadModeManager {

    private static final String TAG = ReadModeManager.class.getSimpleName();

    private final @NonNull Context context;
    private final @NonNull PrefsHelper prefsHelper;

    private final @NonNull ReadModeSubject readModeSubject;

    public ReadModeManager(final @NonNull Context context, final @NonNull ReadModeSubject readModeSubject) {
        this.context = context;
        this.prefsHelper = PrefsHelper.init(context);
        this.readModeSubject = readModeSubject;
    }

    /**
     * Starts the Read Mode by enabling the overlay service and updating
     * the UI and shared preferences with the current color, brightness,
     * and intensity settings.
     */
    public void startReadMode(final @NonNull ReadModeSettings readModeSettings) {
        // Only start service if overlay permission granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            Log.w(TAG, "No overlay permission granted, finishing the app");
            return;
        }

        readModeSettings.setIsReadModeOn(true);

        // save properties
        prefsHelper.saveProperty(Constants.PREF_IS_READ_MODE_ON, readModeSettings.isReadModeOn());
        prefsHelper.saveProperty(Constants.PREF_COLOR_INTENSITY, readModeSettings.getColorIntensity());
        prefsHelper.saveProperty(Constants.PREF_BRIGHTNESS, readModeSettings.getBrightness());
        if (!readModeSettings.shouldUseSameIntensityBrightnessForAll()) {
            // Only save the properties for each color when setting is disabled
            prefsHelper.saveColorSettingsProperty(readModeSettings);
        }

        final Intent readModeIntent = new Intent(context, DrawOverAppsService.class);
        if (!isMyServiceRunning(readModeIntent.getClass())) {
            Log.i(TAG, "Starting Read Mode...");
            context.startService(readModeIntent);
            readModeSettings.setReadModeIntent(readModeIntent);
        }

        readModeSubject.setReadModeOn(true);
    }

    /**
     * Stops the Read Mode by disabling the overlay service and updating
     * the UI and shared preferences with the current color, brightness,
     * and intensity settings.
     */
    public void stopReadMode(final @NonNull ReadModeSettings readModeSettings) {
        readModeSubject.setReadModeOn(false);
        readModeSettings.setIsReadModeOn(false);

        // save properties
        prefsHelper.saveProperty(Constants.PREF_IS_READ_MODE_ON, readModeSettings.isReadModeOn());

        Log.i(TAG, "Stopping Read Mode...");
        if (readModeSettings.getReadModeIntent() != null) {
            context.stopService(readModeSettings.getReadModeIntent());
        } else {
            context.stopService(new Intent(context, DrawOverAppsService.class));
        }
        readModeSettings.setReadModeIntent(null);
    }

    /**
     * Checks if a specific service is currently running on the device.
     */
    private boolean isMyServiceRunning(final @NonNull Class<?> serviceClass) {
        final ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.d(TAG, "Service is running");
                return true;
            }
        }
        Log.d(TAG, "Service is NOT running");
        return false;
    }


}
