/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode.command;

import static org.mockito.Mockito.never;

import autonightmode.mx.com.alanquintero.autonightmode.BaseTest;
import autonightmode.mx.com.alanquintero.autonightmode.manager.ReadModeManager;
import autonightmode.mx.com.alanquintero.autonightmode.model.ReadModeSettings;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SettingsReadModeCommandTest extends BaseTest {

    @Mock
    private ReadModeManager readModeManager;

    private AutoCloseable mocks;
    private ReadModeSettings readModeSettings;
    private SettingsReadModeCommand settingsReadModeCommand;

    @BeforeEach
    public void setup() {
        // Initialize mocks
        mocks = MockitoAnnotations.openMocks(this);

        readModeSettings = ReadModeSettings.init();
        settingsReadModeCommand = new SettingsReadModeCommand(readModeManager, readModeSettings);
    }

    @AfterEach
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void startReadMode_readModeIsOffAndAutoReadModeIsOff() {
        // Given
        readModeSettings.setIsReadModeOn(false);
        readModeSettings.setAutoStartReadMode(false);

        // When
        settingsReadModeCommand.startReadMode();

        // Then
        Mockito.verify(readModeManager, never()).startReadMode();
    }

    @Test
    public void startReadMode_readModeIsOnAndAutoReadModeIsOff() {
        // Given
        readModeSettings.setIsReadModeOn(true);
        readModeSettings.setAutoStartReadMode(false);

        // When
        settingsReadModeCommand.startReadMode();

        // Then
        Mockito.verify(readModeManager).startReadMode();
    }

    @Test
    public void startReadMode_readModeIsOffAndAutoReadModeIsOn() {
        // Given
        readModeSettings.setIsReadModeOn(false);
        readModeSettings.setAutoStartReadMode(true);

        // When
        settingsReadModeCommand.startReadMode();

        // Then
        Mockito.verify(readModeManager).startReadMode();
    }

    @Test
    public void startReadMode_readModeIsOnAndAutoReadModeIsOn() {
        // Given
        readModeSettings.setIsReadModeOn(true);
        readModeSettings.setAutoStartReadMode(true);

        // When
        settingsReadModeCommand.startReadMode();

        // Then
        Mockito.verify(readModeManager).startReadMode();
    }
}
