/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;

public class DrawOverAppsService extends Service {

    private boolean isReadModeEnabled = Constants.DEFAULT_IS_READ_MODE_ENABLED;
    private int colorIntensity = Constants.DEFAULT_COLOR_INTENSITY;

    private int brightness = Constants.DEFAULT_BRIGHTNESS;
    private String color;
    private View mView;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private SharedPreferences sharedpreferences;

    @Override
    public void onCreate() {
        // Stop service if no overlay permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            stopSelf();
            return;
        }

        // Initialize WindowManager
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // Determine correct window type
        int windowType = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

        mParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, windowType, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT);

        sharedpreferences = getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
        isReadModeEnabled = sharedpreferences.getBoolean(Constants.PREF_IS_READ_MODE_ON, Constants.DEFAULT_IS_READ_MODE_ENABLED);
        colorIntensity = sharedpreferences.getInt(Constants.PREF_COLOR_INTENSITY, Constants.DEFAULT_COLOR_INTENSITY);
        brightness = sharedpreferences.getInt(Constants.PREF_BRIGHTNESS, Constants.DEFAULT_BRIGHTNESS);

        if (!isReadModeEnabled) {
            mView = new MyLoadView(this);
            mWindowManager.addView(mView, mParams);
            startNotification();
        }

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedpreferences = getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
        isReadModeEnabled = sharedpreferences.getBoolean(Constants.PREF_IS_READ_MODE_ON, Constants.DEFAULT_IS_READ_MODE_ENABLED);
        colorIntensity = sharedpreferences.getInt(Constants.PREF_COLOR_INTENSITY, Constants.DEFAULT_COLOR_INTENSITY);
        color = sharedpreferences.getString(Constants.PREF_COLOR, Constants.COLOR_WHITE);
        brightness = sharedpreferences.getInt(Constants.PREF_BRIGHTNESS, Constants.DEFAULT_BRIGHTNESS);

        if (mWindowManager == null) {
            // Safety: ensure WindowManager is initialized
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }

        if (isReadModeEnabled) {
            onUpdate();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mView != null) {
            mWindowManager.removeView(mView);
            mView = null;
        }
        stopNotification();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onUpdate() {
        if (mView != null) {
            mWindowManager.removeView(mView);
            mView = null;
        }
        mView = new MyLoadView(this);
        mWindowManager.addView(mView, mParams);
    }

    public void startNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new Notification.Builder(this).setContentTitle(Constants.TEXT_READ_MODE).setContentText(Constants.TEXT_READ_MODE_MSG).setSmallIcon(R.drawable.moon).setContentIntent(pendingIntent).setTicker(Constants.TEXT_READ_MODE_MSG).build();

        startForeground(Constants.NOTIFICATION_ID, notification);
    }

    private void stopNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(Constants.NOTIFICATION_ID);
    }

    public class MyLoadView extends View {

        public MyLoadView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (color != null) {
                canvas.drawARGB(150 - brightness, 0, 0, 0);
                switch (color) {
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
                        final String customColor = sharedpreferences.getString(Constants.PREF_CUSTOM_COLOR, "");
                        if (!customColor.isEmpty()) {
                            int color = Color.parseColor(customColor);
                            int alpha = 120;
                            int red = Color.red(color);
                            int green = Color.green(color);
                            int blue = Color.blue(color);
                            canvas.drawARGB(alpha, red, green, blue - colorIntensity);
                        }
                        break;
                    case Constants.COLOR_NONE:
                    default:
                        break;
                }
            }
        }
    }

}
