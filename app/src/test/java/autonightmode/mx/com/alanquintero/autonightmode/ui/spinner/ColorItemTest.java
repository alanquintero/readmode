/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode.ui.spinner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ColorItemTest {

    @Test
    public void constructor_setsNameAndColor() {
        // Given
        final String name = "Test Color";
        final int color = 0x123456;

        // When
        final ColorItem item = new ColorItem(name, color);

        // Then
        assertEquals(name, item.getName());
        assertEquals(color, item.getIconColor());
    }

    @Test
    public void getters_returnCorrectValues() {
        // Given
        final ColorItem item = new ColorItem("Blue", 0xFF0000FF);

        // When & Then
        assertEquals("Blue", item.getName());
        assertEquals(0xFF0000FF, item.getIconColor());
    }
}
