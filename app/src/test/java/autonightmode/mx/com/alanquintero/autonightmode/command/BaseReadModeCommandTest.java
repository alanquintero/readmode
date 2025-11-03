/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
public class BaseReadModeCommandTest extends BaseTest {

    @Mock
    private ReadModeManager readModeManager;

    private AutoCloseable mocks;
    private ReadModeSettings readModeSettings;
    private BaseReadModeCommand baseReadModeCommand;

    @BeforeEach
    public void setup() {
        // Initialize mocks
        mocks = MockitoAnnotations.openMocks(this);

        readModeSettings = ReadModeSettings.init();

        baseReadModeCommand = new BaseReadModeCommand(readModeManager, readModeSettings) {
            @Override
            public void startReadMode() {
                // do nothing
            }
        };
    }

    @AfterEach
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void updateReadMode() {
        // When
        baseReadModeCommand.updateReadMode();

        // Then
        Mockito.verify(readModeManager).updateOverlay();
    }

    @Test
    public void pauseReadMode_readModeIsOff() {
        // Given
        readModeSettings.setIsReadModeOn(false);

        // When
        baseReadModeCommand.pauseReadMode();

        // Then
        Mockito.verify(readModeManager).stopReadMode();
        assertFalse(readModeSettings.wasReadModeOn());
    }

    @Test
    public void pauseReadMode_readModeIsOn() {
        // Given
        readModeSettings.setIsReadModeOn(true);

        // When
        baseReadModeCommand.pauseReadMode();

        // Then
        Mockito.verify(readModeManager).stopReadMode();
        assertTrue(readModeSettings.wasReadModeOn());
    }

    @Test
    public void resumeReadMode_wasReadModeOnIsFalse() {
        // Given
        readModeSettings.setWasReadModeOn(false);

        // When
        baseReadModeCommand.resumeReadMode();

        // Then
        Mockito.verify(readModeManager, never()).startReadMode();
    }

    @Test
    public void resumeReadMode_wasReadModeOnIsTrue() {
        // Given
        readModeSettings.setWasReadModeOn(true);

        // When
        baseReadModeCommand.resumeReadMode();

        // Then
        Mockito.verify(readModeManager).startReadMode();
    }

    @Test
    public void stopReadMode() {
        // When
        baseReadModeCommand.stopReadMode();

        // Then
        Mockito.verify(readModeManager).stopReadMode();
    }
}
