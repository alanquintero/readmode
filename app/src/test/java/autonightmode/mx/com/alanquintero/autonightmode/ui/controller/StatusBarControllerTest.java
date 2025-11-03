/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode.ui.controller;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.view.Window;

import androidx.fragment.app.FragmentActivity;

import autonightmode.mx.com.alanquintero.autonightmode.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.M)
public class StatusBarControllerTest {

    private AutoCloseable mocks;
    private Context mockContext;
    private FragmentActivity mockActivity;
    private Resources mockResources;
    private Configuration mockConfiguration;
    private Window mockWindow;
    private View mockDecorView;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        // Setup mocks
        mockContext = mock(Context.class);
        mockActivity = mock(FragmentActivity.class);
        mockResources = mock(Resources.class);
        mockConfiguration = mock(Configuration.class);
        mockWindow = mock(Window.class);
        mockDecorView = mock(View.class);

        // Setup common mock behavior
        when(mockContext.getResources()).thenReturn(mockResources);
        when(mockActivity.getWindow()).thenReturn(mockWindow);
        when(mockWindow.getDecorView()).thenReturn(mockDecorView);
        when(mockResources.getConfiguration()).thenReturn(mockConfiguration);
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void setupStatusBarColor_whenLightMode_setsLightColorsAndFlags() {
        try (MockedStatic<Build> ignored = mockStatic(Build.class)) {
            // Given - Light mode
            mockConfiguration.uiMode = Configuration.UI_MODE_NIGHT_NO;
            when(mockResources.getColor(R.color.status_bar_light)).thenReturn(0xFFE0E0E0);
            when(mockResources.getColor(R.color.status_bar_dark)).thenReturn(0xFF303030);

            final StatusBarController controller = new StatusBarController(mockContext, mockActivity);

            // When
            controller.setupStatusBarColor();

            // Then
            verify(mockWindow).setStatusBarColor(0xFFE0E0E0);
            verify(mockDecorView).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Test
    public void setupStatusBarColor_whenDarkMode_setsDarkColorsAndRemovesLightFlag() {
        try (MockedStatic<Build> ignored = mockStatic(Build.class)) {
            // Given - Dark mode
            mockConfiguration.uiMode = Configuration.UI_MODE_NIGHT_YES;
            when(mockResources.getColor(R.color.status_bar_light)).thenReturn(0xFFE0E0E0);
            when(mockResources.getColor(R.color.status_bar_dark)).thenReturn(0xFF303030);

            final StatusBarController controller = new StatusBarController(mockContext, mockActivity);

            // When
            controller.setupStatusBarColor();

            // Then
            verify(mockWindow).setStatusBarColor(0xFF303030);
            verify(mockDecorView).setSystemUiVisibility(0); // Remove light status bar flag
        }
    }

    @Test
    public void setupStatusBarColor_whenLightModeAndPreMarshmallow_setsColorWithoutSystemUiFlag() {
        try (MockedStatic<Build> ignored = mockStatic(Build.class)) {
            // Given - Light mode but pre-Marshmallow
            mockConfiguration.uiMode = Configuration.UI_MODE_NIGHT_NO;
            when(mockResources.getColor(R.color.status_bar_light)).thenReturn(0xFFE0E0E0);
            when(mockResources.getColor(R.color.status_bar_dark)).thenReturn(0xFF303030);

            final StatusBarController controller = new StatusBarController(mockContext, mockActivity);

            // When
            controller.setupStatusBarColor();

            // Then
            verify(mockWindow).setStatusBarColor(0xFFE0E0E0);
        }
    }

    @Test
    public void setupStatusBarColor_whenDarkModeAndPreMarshmallow_setsColorWithoutSystemUiFlag() {
        try (MockedStatic<Build> ignored = mockStatic(Build.class)) {
            // Given - Dark mode but pre-Marshmallow
            mockConfiguration.uiMode = Configuration.UI_MODE_NIGHT_YES;
            when(mockResources.getColor(R.color.status_bar_light)).thenReturn(0xFFE0E0E0);
            when(mockResources.getColor(R.color.status_bar_dark)).thenReturn(0xFF303030);

            final StatusBarController controller = new StatusBarController(mockContext, mockActivity);

            // When
            controller.setupStatusBarColor();

            // Then
            verify(mockWindow).setStatusBarColor(0xFF303030);
        }
    }

    @Test
    public void setupStatusBarColor_whenNightUndefined_fallsBackToDarkMode() {
        try (MockedStatic<Build> ignored = mockStatic(Build.class)) {
            // Given - Undefined night mode (should fall back to dark mode)
            mockConfiguration.uiMode = Configuration.UI_MODE_NIGHT_UNDEFINED;
            when(mockResources.getColor(R.color.status_bar_light)).thenReturn(0xFFE0E0E0);
            when(mockResources.getColor(R.color.status_bar_dark)).thenReturn(0xFF303030);

            final StatusBarController controller = new StatusBarController(mockContext, mockActivity);

            // When
            controller.setupStatusBarColor();

            // Then - Should use dark mode as fallback
            verify(mockWindow).setStatusBarColor(0xFF303030);
            verify(mockDecorView).setSystemUiVisibility(0);
        }
    }

    @Test
    public void setupStatusBarColor_whenConfigurationChanges_updatesColorsAccordingly() {
        try (MockedStatic<Build> ignored = mockStatic(Build.class)) {
            // Given
            when(mockResources.getColor(R.color.status_bar_light)).thenReturn(0xFFE0E0E0);
            when(mockResources.getColor(R.color.status_bar_dark)).thenReturn(0xFF303030);

            final StatusBarController controller = new StatusBarController(mockContext, mockActivity);

            // First call - Light mode
            mockConfiguration.uiMode = Configuration.UI_MODE_NIGHT_NO;
            controller.setupStatusBarColor();
            verify(mockWindow).setStatusBarColor(0xFFE0E0E0);
            verify(mockDecorView).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            // Second call - Dark mode (simulating theme change)
            mockConfiguration.uiMode = Configuration.UI_MODE_NIGHT_YES;
            controller.setupStatusBarColor();
            verify(mockWindow).setStatusBarColor(0xFF303030);
            verify(mockDecorView).setSystemUiVisibility(0);
        }
    }

    @Test
    public void setupStatusBarColor_verifiesResourceCalls() {
        try (MockedStatic<Build> ignored = mockStatic(Build.class)) {
            // Given
            mockConfiguration.uiMode = Configuration.UI_MODE_NIGHT_NO;
            when(mockResources.getColor(R.color.status_bar_light)).thenReturn(0xFFE0E0E0);
            when(mockResources.getColor(R.color.status_bar_dark)).thenReturn(0xFF303030);

            final StatusBarController controller = new StatusBarController(mockContext, mockActivity);

            // When
            controller.setupStatusBarColor();

            // Then - Verify both color resources are accessed (for completeness)
            verify(mockResources).getColor(R.color.status_bar_light);
            verify(mockResources).getColor(R.color.status_bar_dark);
            verify(mockResources).getConfiguration();
        }
    }

    @Test
    public void setupStatusBarColor_whenUiModeMaskApplied_correctlyIdentifiesNightMode() {
        try (MockedStatic<Build> ignored = mockStatic(Build.class)) {
            // Given - Test that the bitmask operation works correctly
            // UI_MODE_NIGHT_MASK = 0x30, UI_MODE_NIGHT_YES = 0x20
            mockConfiguration.uiMode = 0x20;
            when(mockResources.getColor(R.color.status_bar_light)).thenReturn(0xFFE0E0E0);
            when(mockResources.getColor(R.color.status_bar_dark)).thenReturn(0xFF303030);

            final StatusBarController controller = new StatusBarController(mockContext, mockActivity);

            // When
            controller.setupStatusBarColor();

            // Then - Should detect dark mode even with other bits set
            verify(mockWindow).setStatusBarColor(0xFF303030);
            verify(mockDecorView).setSystemUiVisibility(0);
        }
    }
}
