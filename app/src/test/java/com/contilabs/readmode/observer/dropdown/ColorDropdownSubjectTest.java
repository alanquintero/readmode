/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.observer.dropdown;

import static org.mockito.ArgumentMatchers.anyInt;
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
public class ColorDropdownSubjectTest extends BaseTest {

    @Mock
    private ColorDropdownObserver observer1;
    @Mock
    private ColorDropdownObserver observer2;

    private AutoCloseable mocks;
    private ColorDropdownSubject colorDropdownSubject;
    private final int currentColorDropdownPosition = 1;

    @BeforeEach
    void setup() {
        // Initialize mocks
        mocks = MockitoAnnotations.openMocks(this);

        colorDropdownSubject = new ColorDropdownSubject();
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void testRegisterAndNotifyObservers() {
        colorDropdownSubject.registerObserver(observer1);
        colorDropdownSubject.registerObserver(observer2);

        colorDropdownSubject.setCurrentColorDropdownPosition(currentColorDropdownPosition);

        Mockito.verify(observer1).onColorDropdownPositionChange(currentColorDropdownPosition);
        Mockito.verify(observer2).onColorDropdownPositionChange(currentColorDropdownPosition);
    }

    @Test
    void testUnregisterAllObservers() {
        colorDropdownSubject.registerObserver(observer1);
        colorDropdownSubject.registerObserver(observer2);
        colorDropdownSubject.unregisterAllObservers();

        colorDropdownSubject.setCurrentColorDropdownPosition(currentColorDropdownPosition);

        Mockito.verify(observer1, never()).onColorDropdownPositionChange(anyInt());
        Mockito.verify(observer2, never()).onColorDropdownPositionChange(anyInt());
    }

    @Test
    void testNotifyObservers_NoObservers_NoCrash() {
        colorDropdownSubject.setCurrentColorDropdownPosition(currentColorDropdownPosition);

        Mockito.verify(observer1, never()).onColorDropdownPositionChange(anyInt());
        Mockito.verify(observer2, never()).onColorDropdownPositionChange(anyInt());
    }
}
