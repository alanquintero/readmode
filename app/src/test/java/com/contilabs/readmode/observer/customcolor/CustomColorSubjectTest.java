/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.observer.customcolor;

import static org.mockito.ArgumentMatchers.anyString;
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
public class CustomColorSubjectTest extends BaseTest {

    @Mock
    private CustomColorObserver observer1;
    @Mock
    private CustomColorObserver observer2;

    private AutoCloseable mocks;
    private CustomColorSubject customColorSubject;
    private final String color = "#FF0000";

    @BeforeEach
    public void setup() {
        // Initialize mocks
        mocks = MockitoAnnotations.openMocks(this);

        customColorSubject = new CustomColorSubject();
    }

    @AfterEach
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void registerAndNotifyObservers() {
        customColorSubject.registerObserver(observer1);
        customColorSubject.registerObserver(observer2);

        customColorSubject.setCustomColor(color);

        Mockito.verify(observer1).onCustomColorChange(color);
        Mockito.verify(observer2).onCustomColorChange(color);
    }

    @Test
    public void unregisterAllObservers() {
        customColorSubject.registerObserver(observer1);
        customColorSubject.registerObserver(observer2);
        customColorSubject.unregisterAllObservers();

        customColorSubject.setCustomColor(color);

        Mockito.verify(observer1, never()).onCustomColorChange(anyString());
        Mockito.verify(observer2, never()).onCustomColorChange(anyString());
    }

    @Test
    public void notifyObservers_NoObservers_NoCrash() {
        customColorSubject.setCustomColor(color);

        Mockito.verify(observer1, never()).onCustomColorChange(anyString());
        Mockito.verify(observer2, never()).onCustomColorChange(anyString());
    }
}
