/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode;

import static org.junit.Assert.assertEquals;

import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE, sdk = {28}) // pick API level for testing
public class MainActivityTest {

    private MainActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.buildActivity(MainActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void setColorSettingsText_whenNoColorSelected_setsPlaceholderText() {
        TextView textView = new TextView(activity);

        // Arrange
        activity.currentColorDropdownPosition = activity.noColorDropdownPosition;

        // Act
        activity.setColorSettingsText(textView);

        // Assert
        assertEquals(
                activity.getString(R.string.color_settings_placeholder),
                textView.getText().toString()
        );
    }

    @Test
    public void setColorSettingsText_whenColorSelected_setsFormattedText() {
        TextView textView = new TextView(activity);

        // Arrange
        activity.colors = new String[]{"None", "Soft Beige", "Soft Blue"};
        activity.currentColorDropdownPosition = 2; // "Soft Blue"

        // Act
        activity.setColorSettingsText(textView);

        // Assert
        String expected = activity.getString(
                R.string.color_settings_format,
                activity.getString(R.string.color_settings_placeholder),
                "Soft Blue"
        );
        assertEquals(expected, textView.getText().toString());
    }
}
