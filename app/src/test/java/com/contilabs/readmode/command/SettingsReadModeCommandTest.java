/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.command;

import static org.mockito.Mockito.never;

import com.contilabs.readmode.BaseTest;
import com.contilabs.readmode.manager.ReadModeManager;
import com.contilabs.readmode.model.ReadModeSettings;

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
    void setup() {
        // Initialize mocks
        mocks = MockitoAnnotations.openMocks(this);

        readModeSettings = ReadModeSettings.init();
        settingsReadModeCommand = new SettingsReadModeCommand(readModeManager, readModeSettings);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void startReadMode_readModeIsOffAndAutoReadModeIsOff() {
        readModeSettings.setIsReadModeOn(false);
        readModeSettings.setAutoStartReadMode(false);

        settingsReadModeCommand.startReadMode();

        Mockito.verify(readModeManager, never()).startReadMode();
    }

    @Test
    public void startReadMode_readModeIsOnAndAutoReadModeIsOff() {
        readModeSettings.setIsReadModeOn(true);
        readModeSettings.setAutoStartReadMode(false);

        settingsReadModeCommand.startReadMode();

        Mockito.verify(readModeManager).startReadMode();
    }

    @Test
    public void startReadMode_readModeIsOffAndAutoReadModeIsOn() {
        readModeSettings.setIsReadModeOn(false);
        readModeSettings.setAutoStartReadMode(true);

        settingsReadModeCommand.startReadMode();

        Mockito.verify(readModeManager).startReadMode();
    }

    @Test
    public void startReadMode_readModeIsOnAndAutoReadModeIsOn() {
        readModeSettings.setIsReadModeOn(true);
        readModeSettings.setAutoStartReadMode(true);

        settingsReadModeCommand.startReadMode();

        Mockito.verify(readModeManager).startReadMode();
    }
}
