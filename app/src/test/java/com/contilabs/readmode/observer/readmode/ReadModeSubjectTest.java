/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.observer.readmode;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;

import com.contilabs.readmode.BaseTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReadModeSubjectTest extends BaseTest {

    @Mock
    private ReadModeObserver observer1;
    @Mock
    private ReadModeObserver observer2;

    private AutoCloseable mocks;
    private ReadModeSubject readModeSubject;
    private final boolean readModeOn = true;

    @BeforeEach
    void setup() {
        // Initialize mocks
        mocks = MockitoAnnotations.openMocks(this);

        readModeSubject = new ReadModeSubject();
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void testRegisterAndNotifyObservers() {
        readModeSubject.registerObserver(observer1);
        readModeSubject.registerObserver(observer2);

        readModeSubject.setReadModeOn(readModeOn);

        Mockito.verify(observer1).onReadModeChanged(readModeOn);
        Mockito.verify(observer2).onReadModeChanged(readModeOn);
    }

    @Test
    void testUnregisterAllObservers() {
        readModeSubject.registerObserver(observer1);
        readModeSubject.registerObserver(observer2);
        readModeSubject.unregisterAllObservers();

        readModeSubject.setReadModeOn(readModeOn);

        Mockito.verify(observer1, never()).onReadModeChanged(anyBoolean());
        Mockito.verify(observer2, never()).onReadModeChanged(anyBoolean());
    }

    @Test
    void testNotifyObservers_NoObservers_NoCrash() {
        readModeSubject.setReadModeOn(readModeOn);

        Mockito.verify(observer1, never()).onReadModeChanged(anyBoolean());
        Mockito.verify(observer2, never()).onReadModeChanged(anyBoolean());
    }
}
