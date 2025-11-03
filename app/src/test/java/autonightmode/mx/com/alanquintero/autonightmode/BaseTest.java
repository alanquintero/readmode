/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode;

import android.util.Log;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class BaseTest {

    protected MockedStatic<Log> logMock;

    @BeforeEach
    public void mockLog() {
        logMock = Mockito.mockStatic(Log.class);
        logMock.when(() -> Log.d(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
        logMock.when(() -> Log.e(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
        logMock.when(() -> Log.i(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
        // add others if needed
    }

    @AfterEach
    public void closeLogMock() {
        if (logMock != null) {
            logMock.close();
        }
    }
}
