/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.service;

import android.util.Log;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.contilabs.readmode.util.Constants;
import com.contilabs.readmode.R;
import com.contilabs.readmode.ui.MainActivity;
import com.contilabs.readmode.util.PrefsHelper;

import java.lang.ref.WeakReference;

/**
 * DrawOverAppsService is an Android foreground service responsible for creating
 * a screen overlay that applies a "Read Mode" filter over other apps.
 * The overlay adjusts its color, intensity, and brightness according to
 * user preferences saved in SharedPreferences.
 *
 * <p>The service ensures that overlay permissions are granted before starting.
 * It uses a custom view (MyLoadView) to draw the overlay with the chosen
 * color and transparency. The service can run in the foreground with a notification,
 * ensuring it stays alive when the app is in the background.</p>
 *
 * <p>Features include:
 * <ul>
 *     <li>Adjustable screen color filter (predefined colors or custom color)</li>
 *     <li>Adjustable color intensity and brightness</li>
 *     <li>Foreground notification for persistent service behavior</li>
 *     <li>Automatic handling of overlay permissions on Android M and above</li>
 * </ul>
 * </p>
 *
 * <p>Usage:
 * <ul>
 *     <li>The service can be started from an activity with startService(intent)</li>
 *     <li>Color, intensity, and brightness are read from SharedPreferences</li>
 *     <li>The overlay is updated when preferences change using {@link #onUpdate()}</li>
 * </ul>
 * </p>
 *
 * <p>Permissions:
 * Requires {@code android.permission.SYSTEM_ALERT_WINDOW} to draw overlays.</p>
 *
 * @author Alan Quintero
 * @see android.app.Service
 */

public class DrawOverAppsService extends Service {

    private static final String TAG = DrawOverAppsService.class.getSimpleName();

    private boolean isReadModeEnabled = Constants.DEFAULT_IS_READ_MODE_ENABLED;
    private @NonNull String screenColor = Constants.DEFAULT_COLOR_WHITE;
    private int colorIntensity = Constants.DEFAULT_COLOR_INTENSITY;
    private int brightness = Constants.DEFAULT_BRIGHTNESS;

    private View mView;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private PrefsHelper prefsHelper;

    private static WeakReference<DrawOverAppsService> instanceRef;

    @Override
    public void onCreate() {
        Log.d(TAG, "Service onCreate");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Log.w(TAG, "No overlay permission, stopping service");
            stopSelf();
            return;
        }

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        final int windowType = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                windowType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);

        prefsHelper = PrefsHelper.init(this);
        isReadModeEnabled = prefsHelper.isReadModeOn();
        colorIntensity = prefsHelper.getColorIntensity();
        brightness = prefsHelper.getBrightness();

        if (!isReadModeEnabled) {
            Log.d(TAG, "Read mode is OFF, adding overlay view");
            mView = new MyLoadView(this);
            mWindowManager.addView(mView, mParams);
            startNotification();
        }

        instanceRef = new WeakReference<>(this);

        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d(TAG, "Service onStartCommand");

        prefsHelper = PrefsHelper.init(this);
        isReadModeEnabled = prefsHelper.isReadModeOn();
        screenColor = prefsHelper.getColor();
        colorIntensity = prefsHelper.getColorIntensity();
        brightness = prefsHelper.getBrightness();

        if (mWindowManager == null) {
            Log.d(TAG, "Initializing WindowManager in onStartCommand");
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }

        if (isReadModeEnabled) {
            Log.d(TAG, "Read mode is ON, updating overlay view");
            onUpdate();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service onDestroy");

        if (mView != null) {
            Log.d(TAG, "Removing overlay view");
            mWindowManager.removeView(mView);
            mView = null;
        }

        stopNotification();

        instanceRef = null;

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onUpdate() {
        Log.d(TAG, "Updating overlay view");
        // Reading new values
        isReadModeEnabled = prefsHelper.isReadModeOn();
        screenColor = prefsHelper.getColor();
        colorIntensity = prefsHelper.getColorIntensity();
        brightness = prefsHelper.getBrightness();

        if (mView != null) {
            mWindowManager.removeView(mView);
            mView = null;
        }
        mView = new MyLoadView(this);
        mWindowManager.addView(mView, mParams);
    }

    public void startNotification() {
        Log.d(TAG, "Starting foreground notification");
        final Intent notificationIntent = new Intent(this, MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        final String title = getString(R.string.app_name);
        final String notificationMsg = getString(R.string.notification_msg);
        final Notification notification = new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(notificationMsg)
                .setSmallIcon(R.drawable.moon)
                .setContentIntent(pendingIntent)
                .setTicker(notificationMsg)
                .build();

        startForeground(Constants.NOTIFICATION_ID, notification);
    }

    private void stopNotification() {
        Log.d(TAG, "Stopping notification");
        final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(Constants.NOTIFICATION_ID);
    }

    public static DrawOverAppsService getInstance() {
        return instanceRef != null ? instanceRef.get() : null;
    }

    /**
     * MyLoadView is a custom {@link View} used by {@link DrawOverAppsService}
     * to draw the "Read Mode" overlay on top of all other apps.
     *
     * <p>The overlay color, intensity, and brightness are determined by
     * SharedPreferences values:
     * <ul>
     *     <li>screenColor: the selected overlay color (predefined or custom)</li>
     *     <li>colorIntensity: adjustment for color transparency</li>
     *     <li>brightness: overall screen dimming level</li>
     * </ul>
     * </p>
     *
     * <p>The overlay is drawn using the {@link Canvas#drawARGB(int, int, int, int)}
     * method to apply a semi-transparent color filter over the screen.</p>
     *
     * <p>Supports predefined colors such as soft beige, light gray, pale yellow,
     * warm sepia, and soft blue, as well as a user-selected custom color.</p>
     *
     * <p>Usage:
     * <ul>
     *     <li>Created and added to the WindowManager by {@link DrawOverAppsService}</li>
     *     <li>Redraw occurs when preferences change via {@link DrawOverAppsService#onUpdate()}</li>
     * </ul>
     * </p>
     *
     * @see android.view.View
     * @see android.graphics.Canvas
     */
    public class MyLoadView extends View {

        public MyLoadView(final @NonNull Context context) {
            super(context);
        }

        @Override
        protected void onDraw(final @NonNull Canvas canvas) {
            super.onDraw(canvas);
            Log.d(TAG, "Drawing overlay: color=" + screenColor + " intensity=" + colorIntensity + " brightness=" + brightness);

            canvas.drawARGB(150 - brightness, 0, 0, 0);
            switch (screenColor) {
                case Constants.COLOR_SOFT_BEIGE:
                    canvas.drawARGB(120, 245, 245, 220 - colorIntensity);
                    break;
                case Constants.COLOR_LIGHT_GRAY:
                    canvas.drawARGB(120, 230, 230, 230 - colorIntensity);
                    break;
                case Constants.COLOR_PALE_YELLOW:
                    canvas.drawARGB(120, 255, 255, 210 - colorIntensity);
                    break;
                case Constants.COLOR_WARM_SEPIA:
                    canvas.drawARGB(120, 244, 236, 211 - colorIntensity);
                    break;
                case Constants.COLOR_SOFT_BLUE:
                    canvas.drawARGB(120, 220, 235, 255 - colorIntensity);
                    break;
                case Constants.CUSTOM_COLOR:
                    final String customColor = prefsHelper.getCustomColor();
                    int color = Color.parseColor(customColor);
                    int alpha = 120;
                    int red = Color.red(color);
                    int green = Color.green(color);
                    int blue = Color.blue(color);
                    canvas.drawARGB(alpha, red, green, blue - colorIntensity);
                    break;
                default:
                    break;
            }
        }
    }
}
