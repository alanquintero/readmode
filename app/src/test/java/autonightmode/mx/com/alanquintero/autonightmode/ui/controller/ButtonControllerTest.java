/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode.ui.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import autonightmode.mx.com.alanquintero.autonightmode.R;
import autonightmode.mx.com.alanquintero.autonightmode.command.ReadModeCommand;
import autonightmode.mx.com.alanquintero.autonightmode.model.ReadModeSettings;
import autonightmode.mx.com.alanquintero.autonightmode.ui.dialog.CustomColorDialog;
import autonightmode.mx.com.alanquintero.autonightmode.util.ColorUtils;
import autonightmode.mx.com.alanquintero.autonightmode.util.Constants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.M})
public class ButtonControllerTest {

    private AutoCloseable mocks;
    private Context mockContext;
    private FragmentActivity mockActivity;
    private View mockRootView;
    private ReadModeCommand mockReadModeCommand;
    private ReadModeSettings mockReadModeSettings;
    private CustomColorDialog mockCustomColorDialog;
    private Button mockCustomColorButton;
    private Button mockStartStopButton;
    private FragmentManager mockFragmentManager;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        // Setup mocks
        mockContext = mock(Context.class);
        mockActivity = mock(FragmentActivity.class);
        mockRootView = mock(View.class);
        mockReadModeCommand = mock(ReadModeCommand.class);
        mockReadModeSettings = mock(ReadModeSettings.class);
        mockCustomColorDialog = mock(CustomColorDialog.class);
        mockCustomColorButton = mock(Button.class);
        mockStartStopButton = mock(Button.class);
        mockFragmentManager = mock(FragmentManager.class);

        // Setup common mock behavior
        when(mockRootView.findViewById(R.id.customColorButton)).thenReturn(mockCustomColorButton);
        when(mockRootView.findViewById(R.id.startStopButton)).thenReturn(mockStartStopButton);
        when(mockActivity.getSupportFragmentManager()).thenReturn(mockFragmentManager);
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void constructor_initializesButtonsCorrectly() {
        // Given
        // Setup is already done in setUp()

        // When
        new ButtonController(
                mockContext, mockActivity, mockRootView,
                mockReadModeCommand, mockReadModeSettings, mockCustomColorDialog
        );

        // Then
        verify(mockRootView).findViewById(R.id.customColorButton);
        verify(mockRootView).findViewById(R.id.startStopButton);
    }

    @Test
    public void setupButtons_whenCustomColorPosition_showsCustomColorButton() {
        try (MockedStatic<ContextCompat> contextCompatStatic = mockStatic(ContextCompat.class)) {
            // Given
            when(mockReadModeSettings.getColorDropdownPosition()).thenReturn(Constants.CUSTOM_COLOR_DROPDOWN_POSITION);
            when(mockReadModeSettings.getCustomColor()).thenReturn("#FF0000");
            mockContextCompatForStartButton(contextCompatStatic);

            final ButtonController controller = new ButtonController(
                    mockContext, mockActivity, mockRootView,
                    mockReadModeCommand, mockReadModeSettings, mockCustomColorDialog
            );

            // When
            controller.setupButtons();

            // Then
            verify(mockCustomColorButton).setVisibility(View.VISIBLE);
            verify(mockCustomColorButton).setOnClickListener(any(View.OnClickListener.class));
            verify(mockStartStopButton).setOnClickListener(any(View.OnClickListener.class));
        }
    }

    @Test
    public void setupButtons_whenNotCustomColorPosition_hidesCustomColorButton() {
        try (MockedStatic<ContextCompat> contextCompatStatic = mockStatic(ContextCompat.class)) {
            // Given
            when(mockReadModeSettings.getColorDropdownPosition()).thenReturn(0); // Not custom color
            mockContextCompatForStartButton(contextCompatStatic);

            final ButtonController controller = new ButtonController(
                    mockContext, mockActivity, mockRootView,
                    mockReadModeCommand, mockReadModeSettings, mockCustomColorDialog
            );

            // When
            controller.setupButtons();

            // Then
            verify(mockCustomColorButton, never()).setVisibility(View.VISIBLE);
            verify(mockCustomColorButton).setOnClickListener(any(View.OnClickListener.class));
            verify(mockStartStopButton).setOnClickListener(any(View.OnClickListener.class));
        }
    }

    @Test
    public void customColorButtonClick_showsCustomColorDialog() {
        try (MockedStatic<ContextCompat> contextCompatStatic = mockStatic(ContextCompat.class)) {
            // Given
            when(mockReadModeSettings.getColorDropdownPosition()).thenReturn(Constants.CUSTOM_COLOR_DROPDOWN_POSITION);
            when(mockReadModeSettings.getCustomColor()).thenReturn("#FF0000");
            mockContextCompatForStartButton(contextCompatStatic);

            final ButtonController controller = new ButtonController(
                    mockContext, mockActivity, mockRootView,
                    mockReadModeCommand, mockReadModeSettings, mockCustomColorDialog
            );
            controller.setupButtons();

            // Capture the click listener
            ArgumentCaptor<View.OnClickListener> clickListenerCaptor = ArgumentCaptor.forClass(View.OnClickListener.class);
            verify(mockCustomColorButton).setOnClickListener(clickListenerCaptor.capture());
            View.OnClickListener clickListener = clickListenerCaptor.getValue();

            // When
            clickListener.onClick(mockCustomColorButton);

            // Then
            verify(mockCustomColorDialog).show(mockFragmentManager, "CustomColorDialogOpenedFromButton");
        }
    }

    @Test
    public void startStopButtonClick_whenReadModeOn_stopsReadMode() {
        try (MockedStatic<ContextCompat> contextCompatStatic = mockStatic(ContextCompat.class)) {
            // Given
            when(mockReadModeSettings.isReadModeOn()).thenReturn(true);
            mockContextCompatForStopButton(contextCompatStatic);

            final ButtonController controller = new ButtonController(
                    mockContext, mockActivity, mockRootView,
                    mockReadModeCommand, mockReadModeSettings, mockCustomColorDialog
            );
            controller.setupButtons();

            // Capture the click listener
            ArgumentCaptor<View.OnClickListener> clickListenerCaptor = ArgumentCaptor.forClass(View.OnClickListener.class);
            verify(mockStartStopButton).setOnClickListener(clickListenerCaptor.capture());
            final View.OnClickListener clickListener = clickListenerCaptor.getValue();

            // When
            clickListener.onClick(mockStartStopButton);

            // Then
            verify(mockReadModeCommand).stopReadMode();
        }
    }

    @Test
    public void startStopButtonClick_whenReadModeOff_startsReadMode() {
        try (MockedStatic<ContextCompat> contextCompatStatic = mockStatic(ContextCompat.class)) {
            // Given
            when(mockReadModeSettings.isReadModeOn()).thenReturn(false);
            mockContextCompatForStartButton(contextCompatStatic);

            final ButtonController controller = new ButtonController(
                    mockContext, mockActivity, mockRootView,
                    mockReadModeCommand, mockReadModeSettings, mockCustomColorDialog
            );
            controller.setupButtons();

            // Capture the click listener
            ArgumentCaptor<View.OnClickListener> clickListenerCaptor = ArgumentCaptor.forClass(View.OnClickListener.class);
            verify(mockStartStopButton).setOnClickListener(clickListenerCaptor.capture());
            final View.OnClickListener clickListener = clickListenerCaptor.getValue();

            // When
            clickListener.onClick(mockStartStopButton);

            // Then
            verify(mockReadModeCommand).startReadMode();
        }
    }

    @Test
    public void applyStartStopButtonStyle_whenReadModeOn_setsStopStyle() {
        try (MockedStatic<ContextCompat> contextCompatStatic = mockStatic(ContextCompat.class)) {
            // Given
            mockContextCompatForStopButton(contextCompatStatic);
            when(mockContext.getString(R.string.stop)).thenReturn("Stop");

            final ButtonController controller = new ButtonController(
                    mockContext, mockActivity, mockRootView,
                    mockReadModeCommand, mockReadModeSettings, mockCustomColorDialog
            );

            // When
            controller.applyStartStopButtonStyle(true);

            // Then
            verify(mockStartStopButton).setBackgroundTintList(any(android.content.res.ColorStateList.class));
        }
    }

    @Test
    public void applyStartStopButtonStyle_whenReadModeOff_setsStartStyle() {
        try (MockedStatic<ContextCompat> contextCompatStatic = mockStatic(ContextCompat.class)) {
            // Given
            mockContextCompatForStartButton(contextCompatStatic);
            when(mockContext.getString(R.string.start)).thenReturn("Start");

            final ButtonController controller = new ButtonController(
                    mockContext, mockActivity, mockRootView,
                    mockReadModeCommand, mockReadModeSettings, mockCustomColorDialog
            );

            // When
            controller.applyStartStopButtonStyle(false);

            // Then
            verify(mockStartStopButton).setBackgroundTintList(any(android.content.res.ColorStateList.class));
        }
    }

    @Test
    public void applyCustomColorButtonStyle_whenVeryLightColor_setsBlackText() {
        try (MockedStatic<ColorUtils> colorUtilsStatic = mockStatic(ColorUtils.class)) {
            // Given
            final String customColor = "#FFFFFF";
            colorUtilsStatic.when(() -> ColorUtils.toButtonCompatibleColor(customColor)).thenReturn(Color.WHITE);
            colorUtilsStatic.when(() -> ColorUtils.isVeryLightColor(Color.WHITE)).thenReturn(true);

            final ButtonController controller = new ButtonController(
                    mockContext, mockActivity, mockRootView,
                    mockReadModeCommand, mockReadModeSettings, mockCustomColorDialog
            );

            // When
            controller.applyCustomColorButtonStyle(customColor);

            // Then
            verify(mockCustomColorButton).setBackgroundTintList(any(android.content.res.ColorStateList.class));
            verify(mockCustomColorButton).setTextColor(Color.BLACK);
        }
    }

    @Test
    public void applyCustomColorButtonStyle_whenNotVeryLightColor_setsWhiteText() {
        try (MockedStatic<ColorUtils> colorUtilsStatic = mockStatic(ColorUtils.class)) {
            // Given
            final String customColor = "#000000";
            colorUtilsStatic.when(() -> ColorUtils.toButtonCompatibleColor(customColor)).thenReturn(Color.BLACK);
            colorUtilsStatic.when(() -> ColorUtils.isVeryLightColor(Color.BLACK)).thenReturn(false);

            final ButtonController controller = new ButtonController(
                    mockContext, mockActivity, mockRootView,
                    mockReadModeCommand, mockReadModeSettings, mockCustomColorDialog
            );

            // When
            controller.applyCustomColorButtonStyle(customColor);

            // Then
            verify(mockCustomColorButton).setBackgroundTintList(any(android.content.res.ColorStateList.class));
            verify(mockCustomColorButton).setTextColor(Color.WHITE);
        }
    }

    @Test
    public void onReadModeChanged_updatesStartStopButtonStyle() {
        try (MockedStatic<ContextCompat> contextCompatStatic = mockStatic(ContextCompat.class)) {
            // Given
            mockContextCompatForStopButton(contextCompatStatic);
            when(mockContext.getString(R.string.stop)).thenReturn("Stop");

            final ButtonController controller = new ButtonController(
                    mockContext, mockActivity, mockRootView,
                    mockReadModeCommand, mockReadModeSettings, mockCustomColorDialog
            );

            // When
            controller.onReadModeChanged(true);

            // Then
            verify(mockStartStopButton).setBackgroundTintList(any(android.content.res.ColorStateList.class));
        }
    }

    @Test
    public void onColorDropdownPositionChange_whenCustomColor_showsCustomColorButton() {
        try (MockedStatic<ContextCompat> contextCompatStatic = mockStatic(ContextCompat.class)) {
            // Given
            when(mockReadModeSettings.isReadModeOn()).thenReturn(false);
            mockContextCompatForStartButton(contextCompatStatic);

            // Set up custom color in constants
            Constants.COLOR_HEX_ARRAY[Constants.CUSTOM_COLOR_DROPDOWN_POSITION] = Constants.CUSTOM_COLOR;

            final ButtonController controller = new ButtonController(
                    mockContext, mockActivity, mockRootView,
                    mockReadModeCommand, mockReadModeSettings, mockCustomColorDialog
            );

            // When
            controller.onColorDropdownPositionChange(Constants.CUSTOM_COLOR_DROPDOWN_POSITION);

            // Then
            verify(mockCustomColorButton).setVisibility(View.VISIBLE);
            verify(mockStartStopButton).setBackgroundTintList(any(android.content.res.ColorStateList.class));
        }
    }

    @Test
    public void onColorDropdownPositionChange_whenNotCustomColor_hidesCustomColorButton() {
        try (MockedStatic<ContextCompat> contextCompatStatic = mockStatic(ContextCompat.class)) {
            // Given
            when(mockReadModeSettings.isReadModeOn()).thenReturn(false);
            mockContextCompatForStartButton(contextCompatStatic);

            final ButtonController controller = new ButtonController(
                    mockContext, mockActivity, mockRootView,
                    mockReadModeCommand, mockReadModeSettings, mockCustomColorDialog
            );

            // When
            controller.onColorDropdownPositionChange(0); // Not custom color

            // Then
            verify(mockCustomColorButton).setVisibility(View.GONE);
            verify(mockStartStopButton).setBackgroundTintList(any(android.content.res.ColorStateList.class));
        }
    }

    @Test
    public void onCustomColorChange_updatesCustomColorButtonStyle() {
        try (MockedStatic<ColorUtils> colorUtilsStatic = mockStatic(ColorUtils.class)) {
            // Given
            final String customColor = "#FF0000";
            colorUtilsStatic.when(() -> ColorUtils.toButtonCompatibleColor(customColor)).thenReturn(Color.RED);
            colorUtilsStatic.when(() -> ColorUtils.isVeryLightColor(Color.RED)).thenReturn(false);

            final ButtonController controller = new ButtonController(
                    mockContext, mockActivity, mockRootView,
                    mockReadModeCommand, mockReadModeSettings, mockCustomColorDialog
            );

            // When
            controller.onCustomColorChange(customColor);

            // Then
            verify(mockCustomColorButton).setBackgroundTintList(any(android.content.res.ColorStateList.class));
            verify(mockCustomColorButton).setTextColor(Color.WHITE);
        }
    }

    // Helper methods for mocking ContextCompat
    private void mockContextCompatForStartButton(MockedStatic<ContextCompat> contextCompatStatic) {
        android.content.res.ColorStateList mockColorStateList = mock(android.content.res.ColorStateList.class);
        contextCompatStatic.when(() -> ContextCompat.getColorStateList(mockContext, R.color.button_start))
                .thenReturn(mockColorStateList);
    }

    private void mockContextCompatForStopButton(MockedStatic<ContextCompat> contextCompatStatic) {
        android.content.res.ColorStateList mockColorStateList = mock(android.content.res.ColorStateList.class);
        contextCompatStatic.when(() -> ContextCompat.getColorStateList(mockContext, R.color.button_stop))
                .thenReturn(mockColorStateList);
    }
}
