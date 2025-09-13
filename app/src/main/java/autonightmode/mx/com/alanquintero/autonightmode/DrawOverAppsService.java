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
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;

public class DrawOverAppsService extends Service {

    private int level = 0;
    private int light = 0;
    private boolean isLightOn = false;
    private String colorSelected;
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
        isLightOn = Boolean.parseBoolean(sharedpreferences.getString(Constants.IS_LIGHT_ON, Constants.VALUE_FALSE));
        level = Integer.parseInt(sharedpreferences.getString(Constants.COLOR_LEVEL, Constants.VALUE_ZERO));
        colorSelected = sharedpreferences.getString(Constants.COLOR, Constants.COLOR_WHITE);
        light = Integer.parseInt(sharedpreferences.getString(Constants.LIGHT_LEVEL, Constants.VALUE_ZERO));

        if (!isLightOn) {
            mView = new MyLoadView(this);
            mWindowManager.addView(mView, mParams);
            startNotification();
        }

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedpreferences = getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
        isLightOn = Boolean.parseBoolean(sharedpreferences.getString(Constants.IS_LIGHT_ON, Constants.VALUE_FALSE));
        level = Integer.parseInt(sharedpreferences.getString(Constants.COLOR_LEVEL, Constants.VALUE_ZERO));
        colorSelected = sharedpreferences.getString(Constants.COLOR, Constants.COLOR_WHITE);
        light = Integer.parseInt(sharedpreferences.getString(Constants.LIGHT_LEVEL, Constants.VALUE_ZERO));

        if (mWindowManager == null) {
            // Safety: ensure WindowManager is initialized
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }

        if (isLightOn) {
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
            if (colorSelected != null) {
                canvas.drawARGB(150 - light, 0, 0, 0);
                switch (colorSelected) {
                    case Constants.COLOR_SOFT_BEIGE:
                        canvas.drawARGB(120, 245, 245, 220 - level);
                        break;
                    case Constants.COLOR_LIGHT_GRAY:
                        canvas.drawARGB(120, 230, 230, 230 - level);
                        break;
                    case Constants.COLOR_PALE_YELLOW:
                        canvas.drawARGB(120, 255, 255, 210 - level);
                        break;
                    case Constants.COLOR_WARM_SEPIA:
                        canvas.drawARGB(120, 244, 236, 211 - level);
                        break;
                    case Constants.COLOR_SOFT_BLUE:
                        canvas.drawARGB(120, 220, 235, 255 - level);
                        break;
                    case Constants.COLOR_CUSTOM:
                    case Constants.COLOR_NONE:
                    default:
                        break;
                }
            }
        }
    }

}
