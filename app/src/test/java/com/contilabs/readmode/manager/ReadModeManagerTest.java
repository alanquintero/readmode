/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.manager;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.contilabs.readmode.BaseTest;
import com.contilabs.readmode.model.ReadModeSettings;
import com.contilabs.readmode.observer.readmode.ReadModeSubject;
import com.contilabs.readmode.service.DrawOverAppsService;
import com.contilabs.readmode.util.Constants;
import com.contilabs.readmode.util.PrefsHelper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ReadModeManagerTest extends BaseTest {

    @Mock
    private Context context;
    @Mock
    private PrefsHelper prefsHelper;
    @Mock
    private ReadModeSubject readModeSubject;
    @Mock
    private ReadModeSettings readModeSettings;

    private AutoCloseable mocks;
    private ReadModeManager readModeManager;

    @BeforeEach
    void setup() {
        // Initialize mocks
        mocks = MockitoAnnotations.openMocks(this);

        readModeManager = new ReadModeManager(context, prefsHelper, readModeSubject, readModeSettings);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void startReadMode_serviceIsNotRunning() {
        ActivityManager manager = mock(ActivityManager.class);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = new ArrayList<>();
        ActivityManager.RunningServiceInfo service = mock(ActivityManager.RunningServiceInfo.class);
        runningServiceInfos.add(service);
        service.service = mock(ComponentName.class);

        doReturn(manager).when(context).getSystemService(Context.ACTIVITY_SERVICE);
        doReturn(runningServiceInfos).when(manager).getRunningServices(Integer.MAX_VALUE);
        doReturn("").when(service.service).getClassName();

        readModeManager.startReadMode();

        Mockito.verify(readModeSubject).setReadModeOn(eq(true));
        Mockito.verify(readModeSettings).setIsReadModeOn(eq(true));
        Mockito.verify(prefsHelper).saveProperty(eq(Constants.PREF_IS_READ_MODE_ON), anyBoolean());
        Mockito.verify(prefsHelper).saveProperty(eq(Constants.PREF_COLOR_INTENSITY), anyInt());
        Mockito.verify(prefsHelper).saveProperty(eq(Constants.PREF_BRIGHTNESS), anyInt());
        Mockito.verify(prefsHelper).tryToSaveColorSettingsProperty(readModeSettings);
        Mockito.verify(context).startService(any());
        Mockito.verify(readModeSettings).setReadModeIntent(any());
    }

    @Test
    public void startReadMode_serviceIsRunning() {
        ActivityManager manager = mock(ActivityManager.class);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = new ArrayList<>();
        ActivityManager.RunningServiceInfo service = mock(ActivityManager.RunningServiceInfo.class);
        runningServiceInfos.add(service);
        service.service = mock(ComponentName.class);

        doReturn(manager).when(context).getSystemService(Context.ACTIVITY_SERVICE);
        doReturn(runningServiceInfos).when(manager).getRunningServices(Integer.MAX_VALUE);
        doReturn("android.content.Intent").when(service.service).getClassName();

        readModeManager.startReadMode();

        Mockito.verify(readModeSubject).setReadModeOn(eq(true));
        Mockito.verify(readModeSettings).setIsReadModeOn(eq(true));
        Mockito.verify(prefsHelper).saveProperty(eq(Constants.PREF_IS_READ_MODE_ON), anyBoolean());
        Mockito.verify(prefsHelper).saveProperty(eq(Constants.PREF_COLOR_INTENSITY), anyInt());
        Mockito.verify(prefsHelper).saveProperty(eq(Constants.PREF_BRIGHTNESS), anyInt());
        Mockito.verify(prefsHelper).tryToSaveColorSettingsProperty(readModeSettings);
        Mockito.verify(context, never()).startService(any());
        Mockito.verify(readModeSettings, never()).setReadModeIntent(any());
    }

    @Test
    public void readModeServiceIsRunning() {
        ActivityManager manager = mock(ActivityManager.class);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = new ArrayList<>();
        ActivityManager.RunningServiceInfo service = mock(ActivityManager.RunningServiceInfo.class);
        runningServiceInfos.add(service);
        service.service = mock(ComponentName.class);

        doReturn(manager).when(context).getSystemService(Context.ACTIVITY_SERVICE);
        doReturn(runningServiceInfos).when(manager).getRunningServices(Integer.MAX_VALUE);
        doReturn("android.content.Intent").when(service.service).getClassName();

        readModeManager.startReadMode();

        assertTrue(readModeManager.isReadModeServiceRunning());
    }

    @Test
    public void readModeServiceRunningIsNotRunning() {
        assertFalse(readModeManager.isReadModeServiceRunning());
    }

    @Test
    public void readModeServiceIsRunningAfterStopReadMode() {
        ActivityManager manager = mock(ActivityManager.class);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = new ArrayList<>();
        ActivityManager.RunningServiceInfo service = mock(ActivityManager.RunningServiceInfo.class);
        runningServiceInfos.add(service);
        service.service = mock(ComponentName.class);

        doReturn(manager).when(context).getSystemService(Context.ACTIVITY_SERVICE);
        doReturn(runningServiceInfos).when(manager).getRunningServices(Integer.MAX_VALUE);
        doReturn("android.content.Intent").when(service.service).getClassName();

        readModeManager.startReadMode();

        assertTrue(readModeManager.isReadModeServiceRunning());

        readModeManager.stopReadMode();

        assertFalse(readModeManager.isReadModeServiceRunning());
    }

    @Test
    public void stopReadMode() {
        readModeSettings.setReadModeIntent(mock(Intent.class));

        readModeManager.stopReadMode();

        Mockito.verify(readModeSubject).setReadModeOn(eq(false));
        Mockito.verify(readModeSettings).setIsReadModeOn(eq(false));
        Mockito.verify(prefsHelper).saveProperty(eq(Constants.PREF_IS_READ_MODE_ON), anyBoolean());
        Mockito.verify(context).stopService(any());
        Mockito.verify(readModeSettings).setReadModeIntent(null);
    }

    @Test
    void updateOverlay_whenServiceAvailable_callsOnUpdate() {
        DrawOverAppsService serviceMock = mock(DrawOverAppsService.class);

        try (MockedStatic<DrawOverAppsService> mockedStatic =
                     Mockito.mockStatic(DrawOverAppsService.class)) {
            mockedStatic.when(DrawOverAppsService::getInstance).thenReturn(serviceMock);

            ReadModeManager manager = new ReadModeManager(context, prefsHelper, readModeSubject, readModeSettings);
            manager.updateOverlay();

            Mockito.verify(serviceMock).onUpdate();
        }
    }

    @Test
    void updateOverlay_whenServiceNull_callsStartReadMode() {
        ActivityManager activityManager = mock(ActivityManager.class);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = new ArrayList<>();
        ActivityManager.RunningServiceInfo service = mock(ActivityManager.RunningServiceInfo.class);
        runningServiceInfos.add(service);
        service.service = mock(ComponentName.class);

        doReturn(activityManager).when(context).getSystemService(Context.ACTIVITY_SERVICE);
        doReturn(runningServiceInfos).when(activityManager).getRunningServices(Integer.MAX_VALUE);
        doReturn("android.content.Intent").when(service.service).getClassName();

        try (MockedStatic<DrawOverAppsService> mockedStatic =
                     Mockito.mockStatic(DrawOverAppsService.class)) {
            mockedStatic.when(DrawOverAppsService::getInstance).thenReturn(null);

            ReadModeManager manager = Mockito.spy(new ReadModeManager(context, prefsHelper, readModeSubject, readModeSettings));
            manager.updateOverlay();

            Mockito.verify(manager).startReadMode();
        }
    }
}
