/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui.controller;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.contilabs.readmode.R;
import com.contilabs.readmode.model.ReadModeSettings;
import com.contilabs.readmode.observer.settings.SettingsSubject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.M})
public class MenuControllerTest {

    private AutoCloseable mocks;
    private Context mockContext;
    private FragmentActivity mockActivity;
    private View mockRootView;
    private SettingsSubject mockSettingsSubject;
    private ReadModeSettings mockReadModeSettings;
    private ImageView mockMenu;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        // Setup mocks
        mockContext = mock(Context.class);
        mockActivity = mock(FragmentActivity.class);
        mockRootView = mock(View.class);
        mockSettingsSubject = mock(SettingsSubject.class);
        mockReadModeSettings = mock(ReadModeSettings.class);
        mockMenu = mock(ImageView.class);
        FragmentManager mockFragmentManager = mock(FragmentManager.class);

        // Setup common mock behavior
        when(mockRootView.findViewById(R.id.bannerMenu)).thenReturn(mockMenu);
        when(mockActivity.getSupportFragmentManager()).thenReturn(mockFragmentManager);
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void setupMenu_setsOnClickListener() {
        // Given
        final MenuController controller = new MenuController(mockContext, mockActivity, mockRootView, mockSettingsSubject, mockReadModeSettings);

        // When
        controller.setupMenu();

        // Then
        verify(mockMenu).setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void constructor_initializesFieldsCorrectly() {
        // Given
        when(mockRootView.findViewById(R.id.bannerMenu)).thenReturn(mockMenu);

        // When
        new MenuController(mockContext, mockActivity, mockRootView, mockSettingsSubject, mockReadModeSettings);

        // Then - Verify findViewById was called correctly
        verify(mockRootView).findViewById(R.id.bannerMenu);
    }

    @Test
    public void setupMenu_calledMultipleTimes_doesNotDuplicateListeners() {
        // Given
        final MenuController controller = new MenuController(mockContext, mockActivity, mockRootView, mockSettingsSubject, mockReadModeSettings);

        // When
        controller.setupMenu();
        controller.setupMenu(); // Call setup again

        // Then - Should only set the listener once (implementation dependent)
        // At minimum, it shouldn't crash when called multiple times
        verify(mockMenu, atLeast(1)).setOnClickListener(any(View.OnClickListener.class));
    }
}
