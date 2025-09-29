/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;

import com.contilabs.readmode.BaseTest;
import com.contilabs.readmode.manager.ReadModeManager;
import com.contilabs.readmode.model.ReadModeSettings;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

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
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void updateReadMode() {
        baseReadModeCommand.updateReadMode();

        Mockito.verify(readModeManager).updateOverlay();
    }

    @Test
    public void pauseReadMode_readModeIsOff() {
        readModeSettings.setIsReadModeOn(false);

        baseReadModeCommand.pauseReadMode();

        Mockito.verify(readModeManager).stopReadMode();
        assertFalse(readModeSettings.wasReadModeOn());
    }

    @Test
    public void pauseReadMode_readModeIsOn() {
        readModeSettings.setIsReadModeOn(true);

        baseReadModeCommand.pauseReadMode();

        Mockito.verify(readModeManager).stopReadMode();
        assertTrue(readModeSettings.wasReadModeOn());
    }

    @Test
    public void resumeReadMode_wasReadModeOnIsFalse() {
        readModeSettings.setWasReadModeOn(false);

        baseReadModeCommand.resumeReadMode();

        Mockito.verify(readModeManager, never()).startReadMode();
    }

    @Test
    public void resumeReadMode_wasReadModeOnIsTrue() {
        readModeSettings.setWasReadModeOn(true);

        baseReadModeCommand.resumeReadMode();

        Mockito.verify(readModeManager).startReadMode();
    }

    @Test
    public void stopReadMode() {
        baseReadModeCommand.stopReadMode();

        Mockito.verify(readModeManager).stopReadMode();
    }
}
