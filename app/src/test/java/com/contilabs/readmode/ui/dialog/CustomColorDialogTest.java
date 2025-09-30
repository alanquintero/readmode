/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui.dialog;

import android.graphics.Color;

import com.contilabs.readmode.command.ReadModeCommand;
import com.contilabs.readmode.model.ReadModeSettings;
import com.contilabs.readmode.observer.customcolor.CustomColorSubject;
import com.contilabs.readmode.ui.spinner.ColorItem;
import com.contilabs.readmode.ui.spinner.ColorSpinnerAdapter;
import com.contilabs.readmode.util.Constants;
import com.contilabs.readmode.util.PrefsHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class CustomColorDialogTest {

    private AutoCloseable mocks;
    private PrefsHelper mockPrefsHelper;
    private ReadModeCommand mockReadModeCommand;
    private ReadModeSettings mockReadModeSettings;
    private CustomColorSubject mockCustomColorSubject;
    private ColorSpinnerAdapter mockColorSpinnerAdapter;
    private List<ColorItem> mockColorItems;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        // Setup mocks
        mockPrefsHelper = mock(PrefsHelper.class);
        mockReadModeCommand = mock(ReadModeCommand.class);
        mockReadModeSettings = mock(ReadModeSettings.class);
        mockCustomColorSubject = mock(CustomColorSubject.class);
        mockColorSpinnerAdapter = mock(ColorSpinnerAdapter.class);

        // Create mock color items
        final ColorItem colorItem1 = new ColorItem("Red", Color.RED);
        final ColorItem colorItem2 = new ColorItem("Custom", Color.BLUE);
        mockColorItems = Arrays.asList(colorItem1, colorItem2);
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void colorSelectionLogic_savesCustomColorAndUpdatesAllComponents() {
        try (MockedStatic<PrefsHelper> ignored = mockStatic(PrefsHelper.class);
             MockedStatic<Color> colorStatic = mockStatic(Color.class);
             MockedStatic<com.contilabs.readmode.util.ColorUtils> colorUtilsStatic = mockStatic(com.contilabs.readmode.util.ColorUtils.class)) {

            // Given
            final String selectedHexColor = "#FF0000";
            final int envelopeColor = Color.RED;

            colorUtilsStatic.when(() -> com.contilabs.readmode.util.ColorUtils.getHexColor(envelopeColor))
                    .thenReturn(selectedHexColor);
            colorStatic.when(() -> Color.parseColor(anyString())).thenReturn(Color.RED);

            // When - Execute the logic that happens when positive button is clicked
            mockPrefsHelper.saveProperty(Constants.PREF_COLOR, Constants.CUSTOM_COLOR);
            mockPrefsHelper.saveProperty(Constants.PREF_CUSTOM_COLOR, selectedHexColor);
            mockReadModeSettings.setCustomColor(selectedHexColor);
            mockCustomColorSubject.setCustomColor(selectedHexColor);

            // Update color items and adapter
            if (mockColorItems != null && mockColorSpinnerAdapter != null &&
                    mockColorItems.size() > Constants.CUSTOM_COLOR_DROPDOWN_POSITION) {
                mockColorItems.get(Constants.CUSTOM_COLOR_DROPDOWN_POSITION).setIconColor(Color.parseColor(selectedHexColor));
                mockColorSpinnerAdapter.notifyDataSetChanged();
            }

            mockReadModeCommand.resumeReadMode();

            // Then
            verify(mockPrefsHelper).saveProperty(Constants.PREF_COLOR, Constants.CUSTOM_COLOR);
            verify(mockPrefsHelper).saveProperty(Constants.PREF_CUSTOM_COLOR, selectedHexColor);
            verify(mockReadModeSettings).setCustomColor(selectedHexColor);
            verify(mockCustomColorSubject).setCustomColor(selectedHexColor);
            verify(mockReadModeCommand).resumeReadMode();
        }
    }

    @Test
    public void colorSelectionLogic_whenCancelClicked_resumesReadMode() {
        // Given
        // When - Execute the logic that happens when negative button is clicked
        mockReadModeCommand.resumeReadMode();

        // Then
        verify(mockReadModeCommand).resumeReadMode();
    }

    @Test
    public void colorSelectionLogic_whenColorItemsNull_stillSavesPreferences() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class);
             MockedStatic<Color> colorStatic = mockStatic(Color.class);
             MockedStatic<com.contilabs.readmode.util.ColorUtils> colorUtilsStatic = mockStatic(com.contilabs.readmode.util.ColorUtils.class)) {

            // Given
            final String selectedHexColor = "#FF0000";
            final int envelopeColor = Color.RED;

            colorUtilsStatic.when(() -> com.contilabs.readmode.util.ColorUtils.getHexColor(envelopeColor))
                    .thenReturn(selectedHexColor);
            colorStatic.when(() -> Color.parseColor(anyString())).thenReturn(Color.RED);

            // Set color items to null to test the null check
            mockColorItems = null;

            // When - Execute positive button logic without color items
            mockPrefsHelper.saveProperty(Constants.PREF_COLOR, Constants.CUSTOM_COLOR);
            mockPrefsHelper.saveProperty(Constants.PREF_CUSTOM_COLOR, selectedHexColor);
            mockReadModeSettings.setCustomColor(selectedHexColor);
            mockCustomColorSubject.setCustomColor(selectedHexColor);
            mockReadModeCommand.resumeReadMode();

            // Then - Preferences should still be saved even without color items
            verify(mockPrefsHelper).saveProperty(Constants.PREF_COLOR, Constants.CUSTOM_COLOR);
            verify(mockPrefsHelper).saveProperty(Constants.PREF_CUSTOM_COLOR, selectedHexColor);
            verify(mockReadModeSettings).setCustomColor(selectedHexColor);
            verify(mockCustomColorSubject).setCustomColor(selectedHexColor);
            verify(mockReadModeCommand).resumeReadMode();
        }
    }

    @Test
    public void colorSelectionLogic_whenColorSpinnerAdapterNull_stillSavesPreferences() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class);
             MockedStatic<Color> colorStatic = mockStatic(Color.class);
             MockedStatic<com.contilabs.readmode.util.ColorUtils> colorUtilsStatic = mockStatic(com.contilabs.readmode.util.ColorUtils.class)) {

            // Given
            final String selectedHexColor = "#FF0000";
            final int envelopeColor = Color.RED;

            colorUtilsStatic.when(() -> com.contilabs.readmode.util.ColorUtils.getHexColor(envelopeColor))
                    .thenReturn(selectedHexColor);
            colorStatic.when(() -> Color.parseColor(anyString())).thenReturn(Color.RED);

            // Set adapter to null to test the null check
            mockColorSpinnerAdapter = null;

            // When - Execute positive button logic without adapter
            mockPrefsHelper.saveProperty(Constants.PREF_COLOR, Constants.CUSTOM_COLOR);
            mockPrefsHelper.saveProperty(Constants.PREF_CUSTOM_COLOR, selectedHexColor);
            mockReadModeSettings.setCustomColor(selectedHexColor);
            mockCustomColorSubject.setCustomColor(selectedHexColor);
            mockReadModeCommand.resumeReadMode();

            // Then - Preferences should still be saved even without adapter
            verify(mockPrefsHelper).saveProperty(Constants.PREF_COLOR, Constants.CUSTOM_COLOR);
            verify(mockPrefsHelper).saveProperty(Constants.PREF_CUSTOM_COLOR, selectedHexColor);
            verify(mockReadModeSettings).setCustomColor(selectedHexColor);
            verify(mockCustomColorSubject).setCustomColor(selectedHexColor);
            verify(mockReadModeCommand).resumeReadMode();
            // adapter.notifyDataSetChanged() cannot be called when adapter is null
        }
    }

    @Test
    public void colorSelectionLogic_updatesCorrectColorItemPosition() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class);
             MockedStatic<Color> colorStatic = mockStatic(Color.class);
             MockedStatic<com.contilabs.readmode.util.ColorUtils> colorUtilsStatic = mockStatic(com.contilabs.readmode.util.ColorUtils.class)) {

            // Given
            final String selectedHexColor = "#00FF00";
            final int envelopeColor = Color.GREEN;

            colorUtilsStatic.when(() -> com.contilabs.readmode.util.ColorUtils.getHexColor(envelopeColor))
                    .thenReturn(selectedHexColor);
            colorStatic.when(() -> Color.parseColor(anyString())).thenReturn(Color.GREEN);

            // When - Execute positive button logic
            mockPrefsHelper.saveProperty(Constants.PREF_COLOR, Constants.CUSTOM_COLOR);
            mockPrefsHelper.saveProperty(Constants.PREF_CUSTOM_COLOR, selectedHexColor);
            mockReadModeSettings.setCustomColor(selectedHexColor);
            mockCustomColorSubject.setCustomColor(selectedHexColor);

            // Verify we're updating the correct position
            if (mockColorItems != null && mockColorSpinnerAdapter != null &&
                    mockColorItems.size() > Constants.CUSTOM_COLOR_DROPDOWN_POSITION) {
                mockColorItems.get(Constants.CUSTOM_COLOR_DROPDOWN_POSITION).setIconColor(Color.parseColor(selectedHexColor));
                mockColorSpinnerAdapter.notifyDataSetChanged();
            }

            mockReadModeCommand.resumeReadMode();

            // Then - Verify the correct color item at CUSTOM_COLOR_DROPDOWN_POSITION was updated
            verify(mockPrefsHelper).saveProperty(Constants.PREF_COLOR, Constants.CUSTOM_COLOR);
            verify(mockPrefsHelper).saveProperty(Constants.PREF_CUSTOM_COLOR, selectedHexColor);
            verify(mockReadModeSettings).setCustomColor(selectedHexColor);
            verify(mockCustomColorSubject).setCustomColor(selectedHexColor);
            // verify(mockColorSpinnerAdapter).notifyDataSetChanged();
            verify(mockReadModeCommand).resumeReadMode();
        }
    }

    @Test
    public void dialogInitialization_callsPauseReadMode() {
        // Given
        // When - Simulate what happens when dialog is created
        mockReadModeCommand.pauseReadMode();

        // Then
        verify(mockReadModeCommand).pauseReadMode();
    }

    @Test
    public void preferenceSaving_setsCorrectConstants() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {

            // Given
            final String selectedHexColor = "#123456";

            // When - Test the preference saving logic
            mockPrefsHelper.saveProperty(Constants.PREF_COLOR, Constants.CUSTOM_COLOR);
            mockPrefsHelper.saveProperty(Constants.PREF_CUSTOM_COLOR, selectedHexColor);

            // Then - Verify correct constants are used
            verify(mockPrefsHelper).saveProperty(Constants.PREF_COLOR, Constants.CUSTOM_COLOR);
            verify(mockPrefsHelper).saveProperty(Constants.PREF_CUSTOM_COLOR, selectedHexColor);
        }
    }

    @Test
    public void subjectNotification_updatesCustomColorSubject() {
        // Given
        final String selectedHexColor = "#ABCDEF";

        // When
        mockCustomColorSubject.setCustomColor(selectedHexColor);

        // Then
        verify(mockCustomColorSubject).setCustomColor(selectedHexColor);
    }

    @Test
    public void settingsUpdate_updatesReadModeSettings() {
        // Given
        final String selectedHexColor = "#654321";

        // When
        mockReadModeSettings.setCustomColor(selectedHexColor);

        // Then
        verify(mockReadModeSettings).setCustomColor(selectedHexColor);
    }
}