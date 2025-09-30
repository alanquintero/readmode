/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mockStatic;

import android.graphics.Color;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ColorUtilsTest {

    @Test
    public void getHexColor() {
        // Given
        final String hexColor = ColorUtils.getHexColor(123);

        // When & Then
        assertEquals("#0000007B", hexColor);
    }

    @Test
    public void toButtonCompatibleColor() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            // The color value returned by parseColor
            final int mockColor = 0x1100FF00; // semi-transparent green
            final int expected = 0x4D00FF00;
            colorMock.when(() -> Color.parseColor("#1100FF00")).thenReturn(mockColor);
            colorMock.when(() -> Color.alpha(mockColor)).thenReturn(1);
            colorMock.when(() -> Color.red(mockColor)).thenReturn(2);
            colorMock.when(() -> Color.green(mockColor)).thenReturn(3);
            colorMock.when(() -> Color.blue(mockColor)).thenReturn(4);
            colorMock.when(() -> Color.argb(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(expected);

            // When
            final int result = ColorUtils.toButtonCompatibleColor("#1100FF00");

            // Then
            assertEquals(expected, result);
        }
    }

    @Test
    public void adjustColor() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            final int baseColor = 0xFF112233; // arbitrary color
            // Expected channels after subtracting intensity
            final int intensity = 5;
            final int brightness = 20;
            final int expectedRed = Math.max(0, 0x11 - intensity); // 0x11 - 5 = 0x0C
            final int expectedGreen = Math.max(0, 0x22 - intensity); // 0x22 - 5 = 0x1D
            final int expectedBlue = Math.max(0, 0x33 - intensity); // 0x33 - 5 = 0x2E
            final int expectedAlpha = 150 - brightness; // 150 - 20 = 130
            final int expected = (expectedAlpha << 24) | (expectedRed << 16) | (expectedGreen << 8) | expectedBlue;

            // Mock the color channel methods
            colorMock.when(() -> Color.red(baseColor)).thenReturn(0x11);
            colorMock.when(() -> Color.green(baseColor)).thenReturn(0x22);
            colorMock.when(() -> Color.blue(baseColor)).thenReturn(0x33);
            colorMock.when(() -> Color.argb(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(expected);

            // When
            final int result = ColorUtils.adjustColor(baseColor, intensity, brightness);

            // Then
            assertEquals(expected, result);
        }
    }

    @Test
    public void isVeryLightColor_true() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            final int color = 0xFFFFFFFF; // arbitrary color
            // Mock channels to simulate a very light color
            colorMock.when(() -> Color.red(color)).thenReturn(250);
            colorMock.when(() -> Color.green(color)).thenReturn(240);
            colorMock.when(() -> Color.blue(color)).thenReturn(230);

            // When
            final boolean result = ColorUtils.isVeryLightColor(color);

            // Then
            assertTrue(result, "Color should be considered very light");
        }
    }

    @Test
    public void isVeryLightColor_false() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            final int color = 0xFF112233; // arbitrary dark color
            // Mock channels to simulate a darker color
            colorMock.when(() -> Color.red(color)).thenReturn(10);
            colorMock.when(() -> Color.green(color)).thenReturn(20);
            colorMock.when(() -> Color.blue(color)).thenReturn(30);

            // When
            final boolean result = ColorUtils.isVeryLightColor(color);

            // Then
            assertFalse(result, "Color should not be considered very light");
        }
    }

    @Test
    public void isColorDark_true() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            final int color = 0xFF112233; // arbitrary dark color
            // Mock RGB values to simulate a dark color
            colorMock.when(() -> Color.red(color)).thenReturn(20);
            colorMock.when(() -> Color.green(color)).thenReturn(30);
            colorMock.when(() -> Color.blue(color)).thenReturn(40);

            // When
            final boolean result = ColorUtils.isColorDark(color);

            // Then
            assertTrue(result, "Color should be considered dark");
        }
    }

    @Test
    public void isColorDark_false() {
        try (MockedStatic<Color> colorMock = mockStatic(Color.class)) {
            // Given
            final int color = 0xFFFFFFFF; // arbitrary light color
            // Mock RGB values to simulate a light color
            colorMock.when(() -> Color.red(color)).thenReturn(250);
            colorMock.when(() -> Color.green(color)).thenReturn(240);
            colorMock.when(() -> Color.blue(color)).thenReturn(230);

            // When
            final boolean result = ColorUtils.isColorDark(color);

            // Then
            assertFalse(result, "Color should not be considered dark");
        }
    }
}
