/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode.observer.readmode;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;

import autonightmode.mx.com.alanquintero.autonightmode.BaseTest;

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
    public void setup() {
        // Initialize mocks
        mocks = MockitoAnnotations.openMocks(this);

        readModeSubject = new ReadModeSubject();
    }

    @AfterEach
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void registerAndNotifyObservers() {
        // Given
        readModeSubject.registerObserver(observer1);
        readModeSubject.registerObserver(observer2);

        // When
        readModeSubject.setReadModeOn(readModeOn);

        // Then
        Mockito.verify(observer1).onReadModeChanged(readModeOn);
        Mockito.verify(observer2).onReadModeChanged(readModeOn);
    }

    @Test
    public void unregisterAllObservers() {
        // Given
        readModeSubject.registerObserver(observer1);
        readModeSubject.registerObserver(observer2);
        readModeSubject.unregisterAllObservers();

        // When
        readModeSubject.setReadModeOn(readModeOn);

        // Then
        Mockito.verify(observer1, never()).onReadModeChanged(anyBoolean());
        Mockito.verify(observer2, never()).onReadModeChanged(anyBoolean());
    }

    @Test
    public void notifyObservers_NoObservers_NoCrash() {
        // When
        readModeSubject.setReadModeOn(readModeOn);

        // Then
        Mockito.verify(observer1, never()).onReadModeChanged(anyBoolean());
        Mockito.verify(observer2, never()).onReadModeChanged(anyBoolean());
    }
}
