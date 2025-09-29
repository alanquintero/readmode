/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.observer.settings;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

import com.contilabs.readmode.BaseTest;
import com.contilabs.readmode.util.Constants;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SettingsSubjectTest extends BaseTest {

    @Mock
    private SettingsObserver observer1;
    @Mock
    private SettingsObserver observer2;

    private AutoCloseable mocks;
    private SettingsSubject settingsSubject;
    private final Constants.SETTING_OPTIONS setting = Constants.SETTING_OPTIONS.RESET_APP_DATA;

    @BeforeEach
    public void setup() {
        // Initialize mocks
        mocks = MockitoAnnotations.openMocks(this);

        settingsSubject = new SettingsSubject();
    }

    @AfterEach
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void registerAndNotifyObservers() {
        settingsSubject.registerObserver(observer1);
        settingsSubject.registerObserver(observer2);

        settingsSubject.onSettingsChanged(setting);

        Mockito.verify(observer1).onSettingsChanged(setting);
        Mockito.verify(observer2).onSettingsChanged(setting);
    }

    @Test
    public void unregisterAllObservers() {
        settingsSubject.registerObserver(observer1);
        settingsSubject.registerObserver(observer2);
        settingsSubject.unregisterAllObservers();

        settingsSubject.onSettingsChanged(setting);

        Mockito.verify(observer1, never()).onSettingsChanged(any());
        Mockito.verify(observer2, never()).onSettingsChanged(any());
    }

    @Test
    public void notifyObservers_NoObservers_NoCrash() {
        settingsSubject.onSettingsChanged(setting);

        Mockito.verify(observer1, never()).onSettingsChanged(any());
        Mockito.verify(observer2, never()).onSettingsChanged(any());
    }
}
