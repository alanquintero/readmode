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
    private static final String SERVICE_NAME = "com.contilabs.readmode.service.DrawOverAppsService";

    private final @NonNull Context context;
    private final @NonNull PrefsHelper prefsHelper;
    private final @NonNull ReadModeSubject readModeSubject;
    private final @NonNull ReadModeSettings readModeSettings;

    private boolean isReadModeServiceRunning = false;

    public ReadModeManager(final @NonNull Context context, final @NonNull PrefsHelper prefsHelper, final @NonNull ReadModeSubject readModeSubject, final @NonNull ReadModeSettings readModeSettings) {
        this.context = context;
        this.prefsHelper = prefsHelper;
        this.readModeSubject = readModeSubject;
        this.readModeSettings = readModeSettings;

        // If the MainActivity is destroyed while Read Mode is ON, the overlay will keep running
        // since it is managed by the foreground service. When MainActivity is recreated, a new
        // ReadModeManager instance is also created. At that point we must restore the correct
        // state of `isReadModeServiceRunning` to ensure UI elements (e.g., Start/Stop button)
        // reflect the actual Read Mode status.
        if (readModeSettings.isReadModeOn() && isMyServiceRunning(SERVICE_NAME)) {
            isReadModeServiceRunning = true;
        }
    }

    /**
     * Starts the Read Mode by enabling the overlay service and updating
     * the UI and shared preferences with the current color, brightness,
     * and intensity settings.
     */
    public void startReadMode() {
        // Only start service if overlay permission granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            Log.w(TAG, "No overlay permission granted, finishing the app");
            return;
        }

        readModeSubject.setReadModeOn(true);
        readModeSettings.setIsReadModeOn(true);
        isReadModeServiceRunning = true;

        // save properties
        prefsHelper.saveProperty(Constants.PREF_IS_READ_MODE_ON, readModeSettings.isReadModeOn());
        prefsHelper.saveProperty(Constants.PREF_COLOR_INTENSITY, readModeSettings.getColorIntensity());
        prefsHelper.saveProperty(Constants.PREF_BRIGHTNESS, readModeSettings.getBrightness());
        prefsHelper.tryToSaveColorSettingsProperty(readModeSettings);

        final Intent readModeIntent = new Intent(context, DrawOverAppsService.class);
        if (!isMyServiceRunning(readModeIntent.getClass().getName())) {
            Log.i(TAG, "Starting Read Mode...");
            context.startService(readModeIntent);
            readModeSettings.setReadModeIntent(readModeIntent);
        }
    }

    /**
     * Stops the Read Mode by disabling the overlay service and updating
     * the UI and shared preferences with the current color, brightness,
     * and intensity settings.
     */
    public void stopReadMode() {
        readModeSubject.setReadModeOn(false);
        readModeSettings.setIsReadModeOn(false);
        isReadModeServiceRunning = false;

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
     * Either redraws overlay without stopping the Read Mode or start the Read Mode if not running
     */
    public void updateOverlay() {
        final DrawOverAppsService service = DrawOverAppsService.getInstance();
        if (service != null) {
            Log.d(TAG, "Updating overlay directly via ReadModeManager...");
            service.onUpdate(); // redraw overlay without stopping service
        } else {
            Log.w(TAG, "DrawOverAppsService is not running. Starting it...");
            startReadMode(); // start the service if not running
        }
    }

    public boolean isReadModeServiceRunning() {
        return isReadModeServiceRunning;
    }

    /**
     * Checks if a specific service is currently running on the device.
     */
    private boolean isMyServiceRunning(final @NonNull String serviceClassName) {
        final ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (final ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClassName.equals(service.service.getClassName())) {
                Log.d(TAG, "Service is running");
                return true;
            }
        }
        Log.d(TAG, "Service is NOT running");
        return false;
    }
}
