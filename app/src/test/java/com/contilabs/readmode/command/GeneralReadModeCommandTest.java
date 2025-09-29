/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.command;

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
public class GeneralReadModeCommandTest extends BaseTest {

    @Mock
    private ReadModeManager readModeManager;

    private AutoCloseable mocks;
    private GeneralReadModeCommand generalReadModeCommand;

    @BeforeEach
    public void setup() {
        // Initialize mocks
        mocks = MockitoAnnotations.openMocks(this);

        generalReadModeCommand = new GeneralReadModeCommand(readModeManager, ReadModeSettings.init());
    }

    @AfterEach
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void startReadMode() {
        generalReadModeCommand.startReadMode();

        Mockito.verify(readModeManager).startReadMode();
    }
}
