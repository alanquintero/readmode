/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode.ui.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.Spinner;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import autonightmode.mx.com.alanquintero.autonightmode.R;
import autonightmode.mx.com.alanquintero.autonightmode.command.ReadModeCommand;
import autonightmode.mx.com.alanquintero.autonightmode.model.ReadModeSettings;
import autonightmode.mx.com.alanquintero.autonightmode.observer.dropdown.ColorDropdownSubject;
import autonightmode.mx.com.alanquintero.autonightmode.ui.dialog.CustomColorDialog;
import autonightmode.mx.com.alanquintero.autonightmode.util.Constants;
import autonightmode.mx.com.alanquintero.autonightmode.util.PrefsHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.M})
public class ColorDropdownControllerTest {

    private AutoCloseable mocks;
    private Context mockContext;
    private FragmentActivity mockActivity;
    private View mockRootView;
    private CustomColorDialog mockCustomColorDialog;
    private ColorDropdownSubject mockColorDropdownSubject;
    private ReadModeCommand mockReadModeCommand;
    private ReadModeSettings mockReadModeSettings;
    private PrefsHelper mockPrefsHelper;
    private Spinner mockColorSpinner;
    private FragmentManager mockFragmentManager;

    private String[] colorNames;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        // Setup mocks
        mockContext = mock(Context.class);
        mockActivity = mock(FragmentActivity.class);
        mockRootView = mock(View.class);
        mockCustomColorDialog = mock(CustomColorDialog.class);
        mockColorDropdownSubject = mock(ColorDropdownSubject.class);
        mockReadModeCommand = mock(ReadModeCommand.class);
        mockReadModeSettings = mock(ReadModeSettings.class);
        mockPrefsHelper = mock(PrefsHelper.class);
        mockColorSpinner = mock(Spinner.class);
        mockFragmentManager = mock(FragmentManager.class);

        // Setup common mock behavior
        when(mockRootView.findViewById(R.id.colorSpinner)).thenReturn(mockColorSpinner);
        when(mockActivity.getSupportFragmentManager()).thenReturn(mockFragmentManager);

        // Setup test data
        colorNames = new String[]{"Red", "Green", "Blue", "Custom"};
        when(mockReadModeSettings.getCustomColor()).thenReturn("#FF0000");
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void constructor_initializesPrefsHelperAndFindsViews() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(mockContext)).thenReturn(mockPrefsHelper);

            // When
            new ColorDropdownController(
                    mockContext, mockActivity, mockRootView, mockCustomColorDialog,
                    mockColorDropdownSubject, mockReadModeCommand, mockReadModeSettings, colorNames
            );

            // Then
            prefsHelperStatic.verify(() -> PrefsHelper.init(mockContext));
            verify(mockRootView).findViewById(R.id.colorSpinner);
        }
    }

    @Test
    public void handleColorSelection_whenNonCustomColor_updatesSettingsAndPreferences() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(mockContext)).thenReturn(mockPrefsHelper);

            ColorDropdownController controller = new ColorDropdownController(
                    mockContext, mockActivity, mockRootView, mockCustomColorDialog,
                    mockColorDropdownSubject, mockReadModeCommand, mockReadModeSettings, colorNames
            );

            // When - Select non-custom color (position 1)
            controller.handleColorSelection(1);

            // Then
            verify(mockReadModeSettings).setColorDropdownPosition(1);
            verify(mockColorDropdownSubject).setCurrentColorDropdownPosition(1);
            verify(mockPrefsHelper).saveProperty(Constants.PREF_COLOR_DROPDOWN, 1);
            verify(mockPrefsHelper).saveProperty(Constants.PREF_COLOR, Constants.COLOR_HEX_ARRAY[1]);
        }
    }

    @Test
    public void handleColorSelection_whenNonCustomColorAndReadModeOn_updatesReadMode() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(mockContext)).thenReturn(mockPrefsHelper);
            when(mockReadModeSettings.isReadModeOn()).thenReturn(true);

            ColorDropdownController controller = new ColorDropdownController(
                    mockContext, mockActivity, mockRootView, mockCustomColorDialog,
                    mockColorDropdownSubject, mockReadModeCommand, mockReadModeSettings, colorNames
            );

            // When
            controller.handleColorSelection(1);

            // Then
            verify(mockReadModeCommand).updateReadMode();
        }
    }

    @Test
    public void handleColorSelection_whenNonCustomColorAndReadModeOff_doesNotUpdateReadMode() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(mockContext)).thenReturn(mockPrefsHelper);
            when(mockReadModeSettings.isReadModeOn()).thenReturn(false);

            ColorDropdownController controller = new ColorDropdownController(
                    mockContext, mockActivity, mockRootView, mockCustomColorDialog,
                    mockColorDropdownSubject, mockReadModeCommand, mockReadModeSettings, colorNames
            );

            // When
            controller.handleColorSelection(1);

            // Then
            verify(mockReadModeCommand, never()).updateReadMode();
        }
    }

    @Test
    public void handleColorSelection_whenCustomColorSelected_showsCustomColorDialog() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(mockContext)).thenReturn(mockPrefsHelper);

            ColorDropdownController controller = new ColorDropdownController(
                    mockContext, mockActivity, mockRootView, mockCustomColorDialog,
                    mockColorDropdownSubject, mockReadModeCommand, mockReadModeSettings, colorNames
            );

            // When - Select custom color position
            int customPosition = Constants.CUSTOM_COLOR_DROPDOWN_POSITION;
            controller.handleColorSelection(customPosition);

            // Then
            verify(mockCustomColorDialog).show(mockFragmentManager, "CustomColorDialogOpenedFromDropdown");
            verify(mockReadModeCommand, never()).updateReadMode();
        }
    }

    @Test
    public void getColorSpinner_returnsCorrectSpinner() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class)) {
            // Given
            prefsHelperStatic.when(() -> PrefsHelper.init(mockContext)).thenReturn(mockPrefsHelper);

            ColorDropdownController controller = new ColorDropdownController(
                    mockContext, mockActivity, mockRootView, mockCustomColorDialog,
                    mockColorDropdownSubject, mockReadModeCommand, mockReadModeSettings, colorNames
            );

            // When
            Spinner spinner = controller.getColorSpinner();

            // Then
            assert spinner == mockColorSpinner;
        }
    }
}