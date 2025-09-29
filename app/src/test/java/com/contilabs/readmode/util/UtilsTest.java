/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;

import com.contilabs.readmode.BaseTest;
import com.contilabs.readmode.R;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UtilsTest extends BaseTest {

    @Mock
    private Context context;

    private AutoCloseable mocks;

    @BeforeEach
    public void setup() {
        // Initialize mocks
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void setAppTheme_SystemDefault() {
        try (MockedStatic<AppCompatDelegate> delegateMock = mockStatic(AppCompatDelegate.class)) {
            final Constants.ThemeMode themeMode = Constants.ThemeMode.SYSTEM_DEFAULT;

            Utils.setAppTheme(themeMode);

            delegateMock.verify(() -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM));
        }
    }

    @Test
    public void setAppTheme_LightMode() {
        try (MockedStatic<AppCompatDelegate> delegateMock = mockStatic(AppCompatDelegate.class)) {
            final Constants.ThemeMode themeMode = Constants.ThemeMode.LIGHT;

            Utils.setAppTheme(themeMode);

            delegateMock.verify(() -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO));
        }
    }

    @Test
    public void setAppTheme_DarkMode() {
        try (MockedStatic<AppCompatDelegate> delegateMock = mockStatic(AppCompatDelegate.class)) {
            final Constants.ThemeMode themeMode = Constants.ThemeMode.DARK;

            Utils.setAppTheme(themeMode);

            delegateMock.verify(() -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES));
        }
    }

    @Test
    public void isDarkMode_true() {
        final Resources resources = mock(Resources.class);
        final Configuration configuration = mock(Configuration.class);
        configuration.uiMode = 0x20;
        doReturn(resources).when(context).getResources();
        doReturn(configuration).when(resources).getConfiguration();

        final boolean isDarkMode = Utils.isDarkMode(context);

        assertTrue(isDarkMode);
    }

    @Test
    public void isDarkMode_false() {
        final Resources resources = mock(Resources.class);
        final Configuration configuration = mock(Configuration.class);
        configuration.uiMode = 0x30;
        doReturn(resources).when(context).getResources();
        doReturn(configuration).when(resources).getConfiguration();

        final boolean isDarkMode = Utils.isDarkMode(context);

        assertFalse(isDarkMode);
    }

    @Test
    public void createDialogTitle() {
        final LayoutInflater layoutInflater = mock(LayoutInflater.class);
        final View customTitle = mock(View.class);
        final TextView textView = mock(TextView.class);
        final int titleRes = R.string.title_auto_start_read_mode;

        try (MockedStatic<LayoutInflater> mockedStatic = Mockito.mockStatic(LayoutInflater.class)) {
            mockedStatic.when(() -> LayoutInflater.from(context))
                    .thenReturn(layoutInflater);
            Mockito.when(layoutInflater.inflate(R.layout.dialog_title, null))
                    .thenReturn(customTitle);
            doReturn(textView).when(customTitle).findViewById(R.id.dialog_title);

            final View result = Utils.createDialogTitle(context, titleRes);

            Mockito.verify(textView).setText(titleRes);
            assertNotNull(result);
        }
    }
}
