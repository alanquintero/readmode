/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;

import androidx.test.core.app.ApplicationProvider;

import autonightmode.mx.com.alanquintero.autonightmode.util.Constants;
import autonightmode.mx.com.alanquintero.autonightmode.util.PrefsHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O})
public class DrawOverAppsServiceTest {

    private AutoCloseable mocks;
    private Context context;
    private DrawOverAppsService service;
    private PrefsHelper mockPrefsHelper;
    private WindowManager mockWindowManager;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        context = ApplicationProvider.getApplicationContext();

        // Create the service using Robolectric
        service = Robolectric.setupService(DrawOverAppsService.class);

        mockPrefsHelper = mock(PrefsHelper.class);
        mockWindowManager = mock(WindowManager.class);

        // Reset the static instance
        DrawOverAppsService.instanceRef = null;
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
        if (service != null) {
            service.onDestroy();
        }
        DrawOverAppsService.instanceRef = null;
    }

    @Test
    public void onCreate_whenNoOverlayPermission_stopsSelf() {
        try (MockedStatic<Settings> settingsStatic = mockStatic(Settings.class);
             MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {

            // Given - No overlay permission
            settingsStatic.when(() -> Settings.canDrawOverlays(service)).thenReturn(false);
            prefsHelperStatic.when(() -> PrefsHelper.init(service)).thenReturn(mockPrefsHelper);

            // When
            service.onCreate();

            // Then
            assertTrue(Shadows.shadowOf(service).isStoppedBySelf());
        }
    }

    @Test
    public void onCreate_whenHasOverlayPermissionAndReadModeOff_initializesService() {
        try (MockedStatic<Settings> settingsStatic = mockStatic(Settings.class);
             MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {

            // Given - Has overlay permission and read mode is off
            settingsStatic.when(() -> Settings.canDrawOverlays(service)).thenReturn(true);
            prefsHelperStatic.when(() -> PrefsHelper.init(service)).thenReturn(mockPrefsHelper);
            when(mockPrefsHelper.isReadModeOn()).thenReturn(false);
            when(mockPrefsHelper.getColorIntensity()).thenReturn(Constants.DEFAULT_COLOR_INTENSITY);
            when(mockPrefsHelper.getBrightness()).thenReturn(Constants.DEFAULT_BRIGHTNESS);

            // Mock system services
            final ShadowApplication shadowApplication = Shadows.shadowOf(service.getApplication());
            shadowApplication.setSystemService(Context.WINDOW_SERVICE, mockWindowManager);

            // When
            service.onCreate();

            // Then
            assertNotNull(DrawOverAppsService.instanceRef);
            assertNotNull(DrawOverAppsService.getInstance());
            verify(mockWindowManager).addView(any(View.class), any(WindowManager.LayoutParams.class));
        }
    }

    @Test
    public void onCreate_whenHasOverlayPermissionAndReadModeOn_doesNotAddView() {
        try (MockedStatic<Settings> settingsStatic = mockStatic(Settings.class);
             MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {

            // Given - Has overlay permission and read mode is on
            settingsStatic.when(() -> Settings.canDrawOverlays(service)).thenReturn(true);
            prefsHelperStatic.when(() -> PrefsHelper.init(service)).thenReturn(mockPrefsHelper);
            when(mockPrefsHelper.isReadModeOn()).thenReturn(true);
            when(mockPrefsHelper.getColorIntensity()).thenReturn(Constants.DEFAULT_COLOR_INTENSITY);
            when(mockPrefsHelper.getBrightness()).thenReturn(Constants.DEFAULT_BRIGHTNESS);

            // Mock system services
            final ShadowApplication shadowApplication = Shadows.shadowOf(service.getApplication());
            shadowApplication.setSystemService(Context.WINDOW_SERVICE, mockWindowManager);

            // When
            service.onCreate();

            // Then
            assertNotNull(DrawOverAppsService.instanceRef);
            verify(mockWindowManager, never()).addView(any(View.class), any(WindowManager.LayoutParams.class));
        }
    }

    @Test
    public void onStartCommand_whenReadModeOn_callsOnUpdate() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(service)).thenReturn(mockPrefsHelper);
            when(mockPrefsHelper.isReadModeOn()).thenReturn(true);
            when(mockPrefsHelper.getColor()).thenReturn(Constants.DEFAULT_COLOR_WHITE);
            when(mockPrefsHelper.getColorIntensity()).thenReturn(Constants.DEFAULT_COLOR_INTENSITY);
            when(mockPrefsHelper.getBrightness()).thenReturn(Constants.DEFAULT_BRIGHTNESS);

            // Mock system services
            final ShadowApplication shadowApplication = Shadows.shadowOf(service.getApplication());
            shadowApplication.setSystemService(Context.WINDOW_SERVICE, mockWindowManager);

            // Initialize service first
            service.mWindowManager = mockWindowManager;

            // When
            int result = service.onStartCommand(new Intent(), 0, 0);

            // Then
            assertEquals(Service.START_STICKY, result);
            // Verify that preferences were read
            verify(mockPrefsHelper, atLeast(1)).isReadModeOn();
            verify(mockPrefsHelper, atLeast(1)).getColor();
            verify(mockPrefsHelper, atLeast(1)).getColorIntensity();
            verify(mockPrefsHelper, atLeast(1)).getBrightness();
        }
    }

    @Test
    public void onStartCommand_whenWindowManagerNull_initializesWindowManager() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(service)).thenReturn(mockPrefsHelper);
            when(mockPrefsHelper.isReadModeOn()).thenReturn(false);

            // Mock system services
            final ShadowApplication shadowApplication = Shadows.shadowOf(service.getApplication());
            shadowApplication.setSystemService(Context.WINDOW_SERVICE, mockWindowManager);

            // Ensure WindowManager is null initially
            service.mWindowManager = null;

            // When
            service.onStartCommand(new Intent(), 0, 0);

            // Then
            assertNotNull(service.mWindowManager);
        }
    }

    @Test
    public void onDestroy_removesViewAndCleansUp() {
        try (MockedStatic<Settings> settingsStatic = mockStatic(Settings.class);
             MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {

            // Given - Service is set up with a view
            settingsStatic.when(() -> Settings.canDrawOverlays(service)).thenReturn(true);
            prefsHelperStatic.when(() -> PrefsHelper.init(service)).thenReturn(mockPrefsHelper);
            when(mockPrefsHelper.isReadModeOn()).thenReturn(false);

            final ShadowApplication shadowApplication = Shadows.shadowOf(service.getApplication());
            shadowApplication.setSystemService(Context.WINDOW_SERVICE, mockWindowManager);

            service.onCreate();

            // Ensure view exists
            service.mView = mock(View.class);

            // When
            service.onDestroy();

            // Then
            verify(mockWindowManager).removeView(any());
            assertNull(service.mView);
            assertNull(DrawOverAppsService.instanceRef);
        }
    }

    @Test
    public void getInstance_whenServiceNotCreated_returnsNull() {
        // Given - No service instance
        DrawOverAppsService.instanceRef = null;

        // When
        final DrawOverAppsService instance = DrawOverAppsService.getInstance();

        // Then
        assertNull(instance);
    }

    @Test
    public void getInstance_whenServiceCreated_returnsInstance() {
        try (MockedStatic<Settings> settingsStatic = mockStatic(Settings.class);
             MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {

            // Given - Service is created
            settingsStatic.when(() -> Settings.canDrawOverlays(service)).thenReturn(true);
            prefsHelperStatic.when(() -> PrefsHelper.init(service)).thenReturn(mockPrefsHelper);
            when(mockPrefsHelper.isReadModeOn()).thenReturn(false);

            final ShadowApplication shadowApplication = Shadows.shadowOf(service.getApplication());
            shadowApplication.setSystemService(Context.WINDOW_SERVICE, mockWindowManager);

            service.onCreate();

            // When
            final DrawOverAppsService instance = DrawOverAppsService.getInstance();

            // Then
            assertNotNull(instance);
            assertEquals(service, instance);
        }
    }

    @Test
    public void myLoadView_onDraw_withSoftBeigeColor_drawsCorrectColor() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(service)).thenReturn(mockPrefsHelper);

            service.screenColor = Constants.COLOR_YELLOW;
            service.colorIntensity = 50;
            service.brightness = 30;
            service.prefsHelper = mockPrefsHelper;

            final DrawOverAppsService.MyLoadView myLoadView = service.new MyLoadView(context);
            final Canvas mockCanvas = mock(Canvas.class);

            // When
            myLoadView.onDraw(mockCanvas);

            // Then
            verify(mockCanvas).drawARGB(150 - 30, 0, 0, 0); // Brightness adjustment
            verify(mockCanvas).drawARGB(120, 245, 245, 220 - 50); // Soft beige with intensity
        }
    }

    @Test
    public void myLoadView_onDraw_withCustomColor_drawsParsedColor() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(service)).thenReturn(mockPrefsHelper);
            when(mockPrefsHelper.getCustomColor()).thenReturn("#FF5733"); // Some custom color

            service.screenColor = Constants.CUSTOM_COLOR;
            service.colorIntensity = 40;
            service.brightness = 20;
            service.prefsHelper = mockPrefsHelper;

            final DrawOverAppsService.MyLoadView myLoadView = service.new MyLoadView(context);
            final Canvas mockCanvas = mock(Canvas.class);

            // When
            myLoadView.onDraw(mockCanvas);

            // Then
            verify(mockCanvas).drawARGB(150 - 20, 0, 0, 0); // Brightness adjustment
            verify(mockCanvas).drawARGB(120, 255, 87, 51 - 40); // Custom color with intensity
        }
    }

    @Test
    public void myLoadView_onDraw_withUnknownColor_doesNotDrawAdditionalColor() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(service)).thenReturn(mockPrefsHelper);

            service.screenColor = "UNKNOWN_COLOR";
            service.colorIntensity = 50;
            service.brightness = 30;
            service.prefsHelper = mockPrefsHelper;

            final DrawOverAppsService.MyLoadView myLoadView = service.new MyLoadView(context);
            final Canvas mockCanvas = mock(Canvas.class);

            // When
            myLoadView.onDraw(mockCanvas);

            // Then
            // Only the brightness adjustment should be drawn
            verify(mockCanvas).drawARGB(150 - 30, 0, 0, 0);
            // No additional drawARGB calls for unknown color
        }
    }

    @Test
    public void myLoadView_onDraw_withDifferentColors_callsCorrectDrawOperations() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(service)).thenReturn(mockPrefsHelper);

            final String[] colors = {
                    Constants.COLOR_YELLOW,
                    Constants.COLOR_PINK,
                    Constants.COLOR_GREEN,
                    Constants.COLOR_GRAY,
                    Constants.COLOR_WHITE
            };

            final int[][] expectedRGB = {
                    {255, 241, 118}, // YELLOW
                    {255, 209, 220}, // PINK
                    {168, 230, 207}, // GREEN
                    {176, 190, 197}, // GRAY
                    {255, 255, 255}  // WHITE
            };

            for (int i = 0; i < colors.length; i++) {
                service.screenColor = colors[i];
                service.colorIntensity = 25;
                service.brightness = 15;
                service.prefsHelper = mockPrefsHelper;

                final DrawOverAppsService.MyLoadView myLoadView = service.new MyLoadView(context);
                final Canvas mockCanvas = mock(Canvas.class);

                // When
                myLoadView.onDraw(mockCanvas);

                // Then
                verify(mockCanvas).drawARGB(150 - 15, 0, 0, 0); // Brightness adjustment
                verify(mockCanvas).drawARGB(120, expectedRGB[i][0], expectedRGB[i][1], expectedRGB[i][2] - 25);
            }
        }
    }

    @Test
    public void onBind_returnsNull() {
        // When
        final IBinder result = service.onBind(new Intent());

        // Then
        assertNull(result);
    }
}
